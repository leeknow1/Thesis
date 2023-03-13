package com.miras.cclearner.service;

import com.miras.cclearner.entity.FeedbackEntity;
import com.miras.cclearner.repository.FeedbackEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackEntityRepository feedbackRepository;

    public String feedbackMain(@RequestParam(required = false, defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FeedbackEntity> feedbacks = feedbackRepository.findAll(pageable);
        model.addAttribute("feedbacks", feedbacks.getContent());

        return "feedback";
    }

    public String addFeedback(@ModelAttribute("feedback") FeedbackEntity feedback) {
        return "createFeedback";
    }

    public String addFeedback(@ModelAttribute("feedback") FeedbackEntity feedback, Principal principal) {
        controlFeedbacks();
        feedback.setUsername(principal.getName());
        feedback.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        feedbackRepository.save(feedback);
        return "redirect:/api/feedback";
    }

    public String replyToFeedback(@ModelAttribute("feedback") FeedbackEntity feedback,
                                  @PathVariable Long id,
                                  Model model) {
        FeedbackEntity reply = feedbackRepository.findById(id).get();
        model.addAttribute("username", reply.getUsername());
        model.addAttribute("replyMessage", reply.getMessage());
        return "replyToFeedback";
    }

    public String replyToFeedback(@ModelAttribute("feedback") FeedbackEntity feedback,
                                  @PathVariable Long id,
                                  Principal principal) {
        controlFeedbacks();
        FeedbackEntity newFeedback = new FeedbackEntity();
        newFeedback.setUsername(principal.getName());
        newFeedback.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        String message = "@" + feedbackRepository.findById(id).get().getUsername() + ", " + feedback.getMessage();
        newFeedback.setMessage(message);

        feedbackRepository.save(newFeedback);
        return "redirect:/api/feedback";
    }

    public String deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/api/feedback";
    }

    public String deleteByDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        feedbackRepository.deleteBy7Days(calendar);
        return "redirect:/api/feedback";
    }

    private void controlFeedbacks() {
        List<FeedbackEntity> feedbacks = feedbackRepository.findAll(Sort.by("id"));
        if (feedbacks.size() == 50)
            feedbackRepository.deleteById(feedbacks.get(0).getId());
    }
}

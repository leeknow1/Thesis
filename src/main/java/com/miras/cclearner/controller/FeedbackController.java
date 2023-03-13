package com.miras.cclearner.controller;

import com.miras.cclearner.entity.Feedback;
import com.miras.cclearner.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService service;

    @GetMapping
    public String feedbackMain(@RequestParam(required = false, defaultValue = "1") int page, Model model) {
        return service.feedbackMain(page, model);
    }

    @GetMapping("/add")
    public String addFeedback(@ModelAttribute("feedback") Feedback feedback) {
        return service.addFeedback(feedback);
    }

    @PostMapping("/add")
    public String addFeedback(@ModelAttribute("feedback") Feedback feedback, Principal principal) {
        return service.addFeedback(feedback, principal);
    }

    @GetMapping("/reply/{id}")
    public String replyToFeedback(@ModelAttribute("feedback") Feedback feedback,
                                  @PathVariable Long id,
                                  Model model) {
        return service.replyToFeedback(feedback, id, model);
    }

    @PostMapping("/reply/{id}")
    public String replyToFeedback(@ModelAttribute("feedback") Feedback feedback,
                                  @PathVariable Long id,
                                  Principal principal) {
        return service.replyToFeedback(feedback, id, principal);
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        return service.deleteFeedback(id);
    }

    @GetMapping("/admin/delete")
    @Transactional
    public String deleteByDate() {
        return service.deleteByDate();
    }
}

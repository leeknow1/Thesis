package com.miras.cclearner.service;

import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.RolesRepository;
import com.miras.cclearner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Service
@RequiredArgsConstructor
public class HomePageService {
    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    public String registration(@ModelAttribute("newUser") UserDTO user) {
        return "registration";
    }

    public String registration(@ModelAttribute("newUser") UserDTO user, Model model) {
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("messageUsername", true);
            return "registration";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("messageEmail", true);
            return "registration";
        }

        Users newUser = new Users();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoleID() == null) user.setRoleID("2");
        newUser.setRole(roleRepository.findById(Long.valueOf(user.getRoleID())).orElseThrow());

        userRepository.save(newUser);

        return "redirect:/api/home";
    }

    public String checkEmail(@ModelAttribute("user") Users user) {
        return "emailCheck";
    }

    public String checkEmail(@ModelAttribute("user") Users user, Model model) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("message", true);
            return checkEmail(user);
        }

        int code = (int) ((Math.random() * (10000 - 1000)) + 1000);
        Users userCode = userRepository.findByEmail(user.getEmail());
        userCode.setResetCode(code);
        userRepository.save(userCode);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setText("Hello, " + userCode.getUsername() + "!\nYour verification code is " + code + ".");
        simpleMailMessage.setSubject("Chinese Character Learning");
        simpleMailMessage.setFrom("miras11832@gmail.com");
        javaMailSender.send(simpleMailMessage);

        return "redirect:/api/refresh-password";
    }

    public String refreshPassword(@ModelAttribute("user") Users user) {
        return "refreshPassword";
    }

    public String refreshPassword(@ModelAttribute("user") Users user, Model model) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("message", true);
            return "refreshPassword";
        }

        Users refreshPassword = userRepository.findByEmail(user.getEmail());

        if (!user.getResetCode().equals(refreshPassword.getResetCode())) {
            model.addAttribute("messageCode", true);
            return "refreshPassword";
        }

        refreshPassword.setPassword(passwordEncoder.encode(user.getPassword()));
        refreshPassword.setResetCode(null);
        userRepository.save(refreshPassword);

        return "redirect:/api/home";
    }
}

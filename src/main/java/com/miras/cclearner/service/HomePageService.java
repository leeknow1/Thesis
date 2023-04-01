package com.miras.cclearner.service;

import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.RolesRepository;
import com.miras.cclearner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class HomePageService {
    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (user.getRoleID() == null)
            user.setRoleID("2");
        newUser.setRole(roleRepository.findById(Long.valueOf(user.getRoleID())).orElseThrow());

        userRepository.save(newUser);

        return "redirect:/api/home";
    }

    public String refreshPassword(@ModelAttribute("user") Users user){
        return "refreshPassword";
    }

    public String refreshPassword(@ModelAttribute("user") Users user, Model model){
        if (!userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("message", true);
            return "refreshPassword";
        }

        Users refreshPassword = userRepository.findByEmail(user.getEmail());
        refreshPassword.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(refreshPassword);

        return "redirect:/api/home";
    }
}

package com.miras.cclearner.service;

import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.RolesRepository;
import com.miras.cclearner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public String registration(@ModelAttribute("newUser") UserDTO user) {
        return "registration";
    }

    public String registration(@ModelAttribute("newUser") UserDTO user, Model model) {
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("message", true);
            return "registration";
        }

        if (user.getRoleID() == null) user.setRoleID("2");

        Users newUser = new Users(null, user.getUsername(), passwordEncoder.encode(user.getPassword()), roleRepository.findById(Long.parseLong(user.getRoleID())).get());

        userRepository.save(newUser);

        return "redirect:/api/home";
    }
}

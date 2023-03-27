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

        Users newUser = new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoleID() == null)
            user.setRoleID("2");
        newUser.setRole(roleRepository.findById(Long.valueOf(user.getRoleID())).orElseThrow());

        userRepository.save(newUser);

        return "redirect:/api/home";
    }
}

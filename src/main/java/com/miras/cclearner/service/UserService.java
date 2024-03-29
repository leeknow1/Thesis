package com.miras.cclearner.service;

import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.RolesRepository;
import com.miras.cclearner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository roleRepository;

    public String getAllUsers(@RequestParam(defaultValue = "1", required = false, value = "page") int page,
                              @RequestParam(required = false, defaultValue = "", value = "user") String username,
                              Model model){
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id"));
        Page<Users> users;

        if(username.isBlank()){
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findAllByUsername(username, pageable);
        }

        model.addAttribute("users", users.getContent());
        model.addAttribute("threePages", getThreePages(page, users.getTotalPages()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "allUsers";
    }

    public String editUser(@PathVariable Long id, Model model){
        Users user = userRepository.findById(id).orElseThrow();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setDownloads(user.getDownloads());
        userDTO.setPoints(user.getPoints());
        model.addAttribute("user", userDTO);
        return "editUser";
    }

    public String editUser(@PathVariable Long id, @ModelAttribute("user") UserDTO userDTO, Model model){
        Users user = userRepository.findById(id).orElseThrow();

        if (!userDTO.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(userDTO.getUsername())) {
            model.addAttribute("messageUsername", true);
            return "editUser";
        }

        if (!userDTO.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            model.addAttribute("messageEmail", true);
            return "editUser";
        }

        user.setUsername(userDTO.getUsername());
        user.setDownloads(userDTO.getDownloads());
        user.setPoints(userDTO.getPoints());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getRoleID() == null)
            userDTO.setRoleID("2");

        if(!userDTO.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user.setRole(roleRepository.findById(Long.valueOf(userDTO.getRoleID())).orElseThrow());
        userRepository.save(user);
        return "redirect:/api/admin/users";
    }

    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "redirect:/api/admin/users";
    }

    private List<Integer> getThreePages(int currentPage , int totalPages){
        List<Integer> threePages = new ArrayList<>();

        if(currentPage == 1) {
            threePages.add(currentPage + 1);
            threePages.add(currentPage + 2);
            threePages.add(currentPage + 3);
        } else if(currentPage < (totalPages - 3)){
            threePages.add(currentPage);
            threePages.add(currentPage + 1);
            threePages.add(currentPage + 2);
        } else {
            threePages.add(totalPages - 3);
            threePages.add(totalPages - 2);
            threePages.add(totalPages - 1);
        }

        return threePages;
    }
}

package com.miras.cclearner.controller;

import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getAllUsers(@RequestParam(defaultValue = "1", required = false, value = "page") int page, Model model){
        return userService.getAllUsers(page, model);
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model){
        return userService.editUser(id, model);
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") UserDTO userDTO, Model model){
        return userService.editUser(id, userDTO, model);
    }
}

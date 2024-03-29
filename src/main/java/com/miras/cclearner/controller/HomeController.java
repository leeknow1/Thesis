package com.miras.cclearner.controller;


import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.Category;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.service.CategoryService;
import com.miras.cclearner.service.CharacterService;
import com.miras.cclearner.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.miras.cclearner.CclearnerApplication.resetTimer;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomePageService service;
    private final CategoryService categoryService;

    private final CharacterService characterService;

    @GetMapping("/home")
    public String getCategories(@RequestParam(required = false, defaultValue = "", value = "name") String name, Model model) {
        return categoryService.getCategories(name, model);
    }

    @GetMapping("/home/reset-timer")
    public String resetTime(Model model){
        resetTimer();
        return categoryService.getCategories("", model);
    }

    @GetMapping("/category/user/{category}")
    public String getCharactersByCategory(@PathVariable(name = "category") Long id,
                                          Model model) {
        return characterService.getCharactersByCategory(id, model);
    }

    @GetMapping("/category/admin/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        return categoryService.addCharCategory(category);
    }

    @PostMapping("/category/admin/add")
    public String addCategory(@ModelAttribute("category") Category category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {
        return categoryService.addCharCategory(category, img, bindingResult, model);
    }

    @GetMapping("/category/admin/edit/{category}")
    public String editCategory(@PathVariable(name = "category") Long cateId, Model model) {
        return categoryService.editCharCategory(cateId, model);
    }

    @PostMapping("/category/admin/edit/{category}")
    public String editCategory(@PathVariable(name = "category") Long cateId, @ModelAttribute("categoryObj") Category category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {
        return categoryService.editCharCategory(cateId, category, img, bindingResult, model);
    }

    @GetMapping("/category/admin/delete/{category}")
    public String deleteCategory(@PathVariable(name = "category") Long cateId){
        return categoryService.deleteCategory(cateId);
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("newUser") UserDTO user) {
        return service.registration(user);
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("newUser") UserDTO user, Model model) {
        return service.registration(user, model);
    }

    @GetMapping("/check-email")
    public String checkEmail(@ModelAttribute("user") Users user){
        return service.checkEmail(user);
    }

    @PostMapping("/check-email")
    public String checkEmail(@ModelAttribute("user") Users user, Model model){
        return service.checkEmail(user, model);
    }

    @GetMapping("/refresh-password")
    public String refreshPassword(@ModelAttribute("user") Users user){
        return service.refreshPassword(user);
    }

    @PostMapping("/refresh-password")
    public String refreshPassword(@ModelAttribute("user") Users user, Model model){
        return service.refreshPassword(user, model);
    }
}

package com.miras.cclearner.controller;


import com.miras.cclearner.dto.UserDTO;
import com.miras.cclearner.entity.CategoryEntity;
import com.miras.cclearner.service.CategoryService;
import com.miras.cclearner.service.CharacterService;
import com.miras.cclearner.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomePageService service;
    private final CategoryService categoryService;

    private final CharacterService characterService;

    @GetMapping("/home")
    public String getCategories(Model model) {
        return categoryService.getCategories(model);
    }

    @GetMapping("/category/user/{category}")
    public String getCharactersByCategory(@PathVariable(name = "category") Long id, Model model) {
        return characterService.getCharactersByCategory(id, model);
    }

    @GetMapping("/category/admin/add")
    public String addCategory(@ModelAttribute("category") CategoryEntity category) {
        return categoryService.addCharCategory(category);
    }

    @PostMapping("/category/admin/add")
    public String addCategory(@ModelAttribute("category") CategoryEntity category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {
        return categoryService.addCharCategory(category, img, bindingResult, model);
    }

    @GetMapping("/category/admin/edit/{category}")
    public String editCategory(@PathVariable(name = "category") Long cateId, Model model) {
        return categoryService.editCharCategory(cateId, model);
    }

    @PostMapping("/category/admin/edit/{category}")
    public String editCategory(@PathVariable(name = "category") Long cateId, @ModelAttribute("categoryObj") CategoryEntity category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {
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
}

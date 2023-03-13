package com.miras.cclearner.controller;

import com.miras.cclearner.entity.CharactersEntity;
import com.miras.cclearner.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/user/{id}")
    public String getOneCharacterByCategory(@PathVariable(name = "id") Long charId, Model model) {
        return characterService.getOneCharacter(charId, model);
    }

    @GetMapping("/admin/add")
    public String addCharacter(@ModelAttribute("character") CharactersEntity character) {
        return characterService.addCharacter(character);
    }

    @PostMapping("/admin/add")
    public String addCharacter(@ModelAttribute("character") CharactersEntity character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return characterService.addCharacter(character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/admin/edit/{id}")
    public String editCharacter(@PathVariable(name = "id") Long charId, Model model) {
        return characterService.editCharacter(charId, model);
    }

    @PostMapping("/admin/edit/{id}")
    public String editCharacter(@PathVariable(name = "id") Long charId, @ModelAttribute("character") CharactersEntity character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return characterService.editCharacter(charId, character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteCharacter(@PathVariable(name = "id") Long charId) {
        return characterService.deleteCharacter(charId);
    }
}
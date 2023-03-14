package com.miras.cclearner.controller;

import com.miras.cclearner.entity.Character;
import com.miras.cclearner.service.CharacterRequestService;
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
public class CharacterRequestController {

    private final CharacterRequestService service;

    @GetMapping("/request")
    public String getRequestCharacters(Model model) {
        return service.getRequestCharacters(model);
    }

    @GetMapping("/request/mine")
    public String getUserRequestCharacters(Principal principal, Model model){
        return service.getUserRequestCharacters(principal, model);
    }

    @GetMapping("/request/{id}")
    public String getOneRequestCharacterByCategory(@PathVariable(name = "id") Long charId, Model model) {
        return service.getOneRequestCharacter(charId, model);
    }

    @GetMapping("/request/{id}/like")
    public String likeRequestCharacter(@PathVariable(name = "id") Long charId, Model model, Principal principal) {
        return service.likeRequestCharacter(charId, principal);
    }

    @GetMapping("/request/{id}/dislike")
    public String dislikeRequestCharacter(@PathVariable(name = "id") Long charId, Model model, Principal principal) {
        return service.dislikeRequestCharacter(charId, principal);
    }

    @GetMapping("/adult/request/add")
    public String requestAddCharacter(@ModelAttribute("character") Character character) {
        return service.requestAddCharacter(character);
    }

    @PostMapping("/adult/request/add")
    public String requestAddCharacter(@ModelAttribute("character") Character character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return service.requestAddCharacter(character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/adult/request/edit/{id}")
    public String requestEditCharacter(@PathVariable(name = "id") Long charId, Model model) {
        return service.requestEditCharacter(charId, model);
    }

    @PostMapping("/adult/request/edit/{id}")
    public String requestEditCharacter(@PathVariable(name = "id") Long charId, @ModelAttribute("character") Character character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return service.requestEditCharacter(charId, character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/admin/request/approve/{id}")
    public String requestApproved(@PathVariable(name = "id") Long charId) {
        return service.requestApproved(charId);
    }

    @GetMapping("/admin/request/disapprove/{id}")
    public String requestDisapproved(@PathVariable(name = "id") Long charId) {
        return service.requestDisapproved(charId);
    }
}

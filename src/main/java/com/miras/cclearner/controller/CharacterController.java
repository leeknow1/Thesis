package com.miras.cclearner.controller;

import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.Character;
import com.miras.cclearner.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.zip.ZipOutputStream;

import static com.miras.cclearner.service.ServiceUtils.zipFile;

@Controller
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/user/{id}")
    public String getOneCharacterByCategory(@PathVariable(name = "id") Long charId, Model model, Principal principal) {
        return characterService.getOneCharacter(charId, model, principal);
    }

    @GetMapping("/mine")
    public String getUsersCharacters(Principal principal, Model model){
        return characterService.getUsersCharacters(principal, model);
    }

    @GetMapping("/admin/add")
    public String addCharacter(@ModelAttribute("character") Character character, Model model) {
        return characterService.addCharacter(character, model);
    }

    @PostMapping("/admin/add")
    public String addCharacter(@ModelAttribute("character") Character character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return characterService.addCharacter(character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/admin/edit/{id}")
    public String editCharacter(@PathVariable(name = "id") Long charId, Model model) {
        return characterService.editCharacter(charId, model);
    }

    @PostMapping("/admin/edit/{id}")
    public String editCharacter(@PathVariable(name = "id") Long charId, @ModelAttribute("character") Character character, @RequestParam(value = "img") MultipartFile img, @RequestParam(value = "aud") MultipartFile aud, @RequestParam(value = "vid") MultipartFile vid, BindingResult bindingResult, Model model, Principal principal) {
        return characterService.editCharacter(charId, character, img, aud, vid, bindingResult, model, principal);
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteCharacter(@PathVariable(name = "id") Long charId) {
        return characterService.deleteCharacter(charId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadCharacter(@PathVariable Long id, Principal principal) throws IOException {
        Resource file = characterService.downloadCharacter(id, principal);
        HttpHeaders headers = new HttpHeaders();

        if (file == null) {
            headers.add("Location", "/api/characters/user/" + id);
            return new ResponseEntity<String>(headers, HttpStatus.FOUND);
        }

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"");
        headers.add("Content-Type", "application/octet-stream");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(file);
    }
}
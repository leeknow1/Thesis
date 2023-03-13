package com.miras.cclearner.service;

import com.miras.cclearner.common.CustomValidator;
import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.CategoryEntity;
import com.miras.cclearner.entity.CharactersEntity;
import com.miras.cclearner.repository.CategoryEntityRepository;
import com.miras.cclearner.repository.CharacterEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import static com.miras.cclearner.CclearnerApplication.passedOneHour;
import static com.miras.cclearner.service.ServiceUtils.getFormattedDate;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterEntityRepository charRepository;
    private final CategoryEntityRepository categoryRepository;
    private final FilePathUtils filePathUtils;
    private final CustomValidator customValidator;

    public String getCharactersByCategory(@PathVariable(name = "category") Long id, Model model) {
        List<CharactersEntity> characters = charRepository.findAllByCategory(id);

        model.addAttribute("character", characters);

        if (characters.size() != 0)
            model.addAttribute("path", filePathUtils.getCharPath());

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "allChars";
    }

    public String getOneCharacter(@PathVariable(name = "id") Long charId, Model model) {
        CharactersEntity character = charRepository.findById(charId).get();

        model.addAttribute("character", character);
        model.addAttribute("videoPath", filePathUtils.getCharVidPath(character.getName()));
        model.addAttribute("audPath", filePathUtils.getCharAudPath(character.getName()));

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "oneChar";
    }

    public String addCharacter(@ModelAttribute("character") CharactersEntity character) {
        return "createChar";
    }

    public String addCharacter(@ModelAttribute("character") CharactersEntity character,
                               @RequestParam(value = "img") MultipartFile img,
                               @RequestParam(value = "aud") MultipartFile aud,
                               @RequestParam(value = "vid") MultipartFile vid,
                               BindingResult bindingResult,
                               Model model,
                               Principal principal
    ) {

        if (bindingResult.hasErrors()) {
            return addCharacter(character);
        }

        if (customValidator.checkName(character.getName())) {
            model.addAttribute("isNameInvalid", true);
            return addCharacter(character);
        }

        Path path = Paths.get(filePathUtils.getCharAbsPath() + "/" + character.getName());
        Path pathImg = Paths.get(filePathUtils.getCharImgAbsPath(character.getName()));
        Path pathAud = Paths.get(filePathUtils.getCharAudAbsPath(character.getName()));
        Path pathVid = Paths.get(filePathUtils.getCharVidAbsPath(character.getName()));

        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
                Files.createDirectory(pathImg);
                Files.createDirectory(pathAud);
                Files.createDirectory(pathVid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            img.transferTo(new File(pathImg + "/" + img.getOriginalFilename()));
            aud.transferTo(new File(pathAud + "/" + aud.getOriginalFilename()));
            vid.transferTo(new File(pathVid + "/" + vid.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        character.setImageName(img.getOriginalFilename());
        character.setAudioName(aud.getOriginalFilename());
        character.setVideoName(vid.getOriginalFilename());
        character.setAuthor(principal.getName());
        character.setUpdatedDate(getFormattedDate());
        character.setStatus("APPROVED");

        charRepository.save(character);

        return "redirect:/api/home";
    }

    public String editCharacter(@PathVariable(name = "id") Long charId,
                                Model model) {
        model.addAttribute("character", charRepository.findById(charId).get());
        return "editChar";
    }

    public String editCharacter(@PathVariable(name = "id") Long charId,
                                @ModelAttribute("character") CharactersEntity character,
                                @RequestParam(value = "img") MultipartFile img,
                                @RequestParam(value = "aud") MultipartFile aud,
                                @RequestParam(value = "vid") MultipartFile vid,
                                BindingResult bindingResult,
                                Model model,
                                Principal principal) {

        if (bindingResult.hasErrors()) {
            return editCharacter(charId, model);
        }

        CharactersEntity changingChar = charRepository.findById(charId).get();

        if (!changingChar.getName().equals(character.getName())) {
            File oldDirName = new File(filePathUtils.getCharAbsPath(changingChar.getName()));
            File newDirName = new File(filePathUtils.getCharAbsPath(character.getName()));
            oldDirName.renameTo(newDirName);
            changingChar.setName(character.getName());
        }

        try {
            if (!img.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharImgAbsPath(changingChar.getName() + "/" + changingChar.getImageName()));
                Files.delete(path);
                img.transferTo(new File(filePathUtils.getCharImgAbsPath(changingChar.getName()) + "/" + img.getOriginalFilename()));
                changingChar.setImageName(img.getOriginalFilename());
            }
            if (!aud.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharAudAbsPath(changingChar.getName()) + "/" + changingChar.getAudioName());
                Files.delete(path);
                aud.transferTo(new File(filePathUtils.getCharAudAbsPath(changingChar.getName()) + "/" + aud.getOriginalFilename()));
                changingChar.setAudioName(aud.getOriginalFilename());
            }
            if (!vid.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharVidAbsPath(changingChar.getName()) + "/" + changingChar.getVideoName());
                Files.delete(path);
                vid.transferTo(new File(filePathUtils.getCharVidAbsPath(changingChar.getName()) + "/" + vid.getOriginalFilename()));
                changingChar.setVideoName(vid.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        changingChar.setDescription(character.getDescription());
        changingChar.setExample(character.getExample());
        changingChar.setAuthor(principal.getName());
        changingChar.setUpdatedDate(getFormattedDate());
        charRepository.save(changingChar);

        return "redirect:/api/home";
    }

    public String deleteCharacter(@PathVariable(name = "id") Long charId) {
        CharactersEntity character = charRepository.findById(charId).get();

        Path path = Paths.get(filePathUtils.getCharAbsPath(character.getName()));
        Path pathImg = Paths.get(filePathUtils.getCharImgAbsPath(character.getName()) + "/" + character.getImageName());
        Path pathAud = Paths.get(filePathUtils.getCharAudAbsPath(character.getName()) + "/" + character.getAudioName());
        Path pathVid = Paths.get(filePathUtils.getCharVidAbsPath(character.getName()) + "/" + character.getVideoName());

        try {
            Files.delete(pathImg);
            Files.delete(pathAud);
            Files.delete(pathVid);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        deleteAllCharRequests(charId);
        charRepository.deleteById(charId);
        return "redirect:/api/home";
    }

    void deleteAllCharRequests(Long id) {
//        for(CharactersRequestEntity request : charRequestRepository.findAllByOriginalId(id)){
//            requestService.requestDisapproved(request.getCategoryId(), request.getId());
//        }
    }
}

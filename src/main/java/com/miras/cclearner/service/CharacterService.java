package com.miras.cclearner.service;

import com.miras.cclearner.common.CustomValidator;
import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.Character;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.CategoryRepository;
import com.miras.cclearner.repository.CharacterRepository;
import com.miras.cclearner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static com.miras.cclearner.CclearnerApplication.passedOneHour;
import static com.miras.cclearner.service.ServiceUtils.getFormattedDate;
import static com.miras.cclearner.service.ServiceUtils.zipFile;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository charRepository;
    private final FilePathUtils filePathUtils;
    private final CustomValidator customValidator;
    private final CharacterRequestService requestService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public String getCharactersByCategory(@PathVariable(name = "category") Long id,
                                          Model model) {
        List<Character> characters = charRepository.findAllByCategory(id);
        model.addAttribute("character", characters);

        if (characters.size() != 0)
            model.addAttribute("path", filePathUtils.getCharPath());

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "allChars";
    }

    public String getUsersCharacters(Principal principal, Model model) {
        Users user = userRepository.findByUsername(principal.getName());
        List<Character> characters = charRepository.findAllByUser(user.getUsername());
        model.addAttribute("character", characters);
        model.addAttribute("info", true);
        model.addAttribute("points", user.getPoints());
        model.addAttribute("downloads", user.getDownloads());

        if (characters.size() != 0)
            model.addAttribute("path", filePathUtils.getCharPath());

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "allChars";
    }

    public String getOneCharacter(@PathVariable(name = "id") Long charId, Model model, Principal principal) {
        Character character = charRepository.findById(charId).orElseThrow();

        if(principal != null) {
            Users user = userRepository.findByUsername(principal.getName());
            if(user.getDownloads() == 0)
                model.addAttribute("notAllowedDownload", true);
        }

        model.addAttribute("character", character);
        model.addAttribute("videoPath", filePathUtils.getCharPathVid(character.getName()));
        model.addAttribute("audPath", filePathUtils.getCharPathAud(character.getName()));

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "oneChar";
    }

    public String addCharacter(@ModelAttribute("character") Character character, Model model) {
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "createChar";
    }

    public String addCharacter(@ModelAttribute("character") Character character,
                               @RequestParam(value = "img") MultipartFile img,
                               @RequestParam(value = "aud") MultipartFile aud,
                               @RequestParam(value = "vid") MultipartFile vid,
                               BindingResult bindingResult,
                               Model model,
                               Principal principal
    ) {

        if (bindingResult.hasErrors()) {
            return addCharacter(character, model);
        }

        if (customValidator.checkName(character.getName())) {
            model.addAttribute("isNameInvalid", true);
            return addCharacter(character, model);
        }

        Path path = Paths.get(filePathUtils.getCharAbsPath() + "/" + character.getName());
        Path pathImg = Paths.get(filePathUtils.getCharAbsPathImg(character.getName()));
        Path pathAud = Paths.get(filePathUtils.getCharAbsPathAud(character.getName()));
        Path pathVid = Paths.get(filePathUtils.getCharAbsPathVid(character.getName()));

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
        model.addAttribute("character", charRepository.findById(charId).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "editChar";
    }

    public String editCharacter(@PathVariable(name = "id") Long charId,
                                @ModelAttribute("character") Character character,
                                @RequestParam(value = "img") MultipartFile img,
                                @RequestParam(value = "aud") MultipartFile aud,
                                @RequestParam(value = "vid") MultipartFile vid,
                                BindingResult bindingResult,
                                Model model,
                                Principal principal) {

        if (bindingResult.hasErrors()) {
            return editCharacter(charId, model);
        }

        Character changingChar = charRepository.findById(charId).orElseThrow();
        character.setStatus(changingChar.getStatus());
        character.setImageName(changingChar.getImageName());
        character.setAudioName(changingChar.getAudioName());
        character.setVideoName(changingChar.getVideoName());

        if (!changingChar.getName().equals(character.getName())) {
            if (customValidator.checkName(character.getName())) {
                model.addAttribute("isNameInvalid", true);
                return editCharacter(charId, model);
            }

            File oldDirName = new File(filePathUtils.getCharAbsPath(changingChar.getName()));
            File newDirName = new File(filePathUtils.getCharAbsPath(character.getName()));
            oldDirName.renameTo(newDirName);
        }

        try {
            if (!img.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharAbsPathImg(changingChar.getName()) + "/" + changingChar.getImageName());
                Files.delete(path);
                img.transferTo(new File(filePathUtils.getCharAbsPathImg(changingChar.getName()) + "/" + img.getOriginalFilename()));
                character.setImageName(img.getOriginalFilename());
            }
            if (!aud.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharAbsPathAud(changingChar.getName()) + "/" + changingChar.getAudioName());
                Files.delete(path);
                aud.transferTo(new File(filePathUtils.getCharAbsPathAud(changingChar.getName()) + "/" + aud.getOriginalFilename()));
                character.setAudioName(aud.getOriginalFilename());
            }
            if (!vid.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCharAbsPathVid(changingChar.getName()) + "/" + changingChar.getVideoName());
                Files.delete(path);
                vid.transferTo(new File(filePathUtils.getCharAbsPathVid(changingChar.getName()) + "/" + vid.getOriginalFilename()));
                character.setVideoName(vid.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        character.setAuthor(principal.getName());
        character.setUpdatedDate(getFormattedDate());
        charRepository.save(character);

        return "redirect:/api/home";
    }

    public String deleteCharacter(@PathVariable(name = "id") Long charId) {
        Character character = charRepository.findById(charId).orElseThrow();

        Path pathImg = Paths.get(filePathUtils.getCharAbsPathImg(character.getName()) + "/" + character.getImageName());
        Path pathAud = Paths.get(filePathUtils.getCharAbsPathAud(character.getName()) + "/" + character.getAudioName());
        Path pathVid = Paths.get(filePathUtils.getCharAbsPathVid(character.getName()) + "/" + character.getVideoName());

        try {
            Files.delete(pathImg);
            Files.delete(pathAud);
            Files.delete(pathVid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteAllCharRequests(charId);
        charRepository.deleteById(charId);
        return "redirect:/api/home";
    }

    void deleteAllCharRequests(Long id) {
        for(Character request : charRepository.findAllByOriginalId(id)){
            requestService.requestDisapproved(request.getId());
        }
    }

    public Resource downloadCharacter(@PathVariable Long id, Principal principal) throws IOException {
        Users user = userRepository.findByUsername(principal.getName());
        user.setDownloads(user.getDownloads() - 1);
        userRepository.save(user);

        Character character = charRepository.findById(id).orElseThrow();
        String sourceFile = filePathUtils.getCharAbsPath(character.getName());

        FileOutputStream fos = new FileOutputStream("myzip.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(sourceFile);
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

        File zip = new File("myzip.zip");
        Path path = Paths.get(zip.getAbsolutePath());
        return new UrlResource(path.toUri());
    }
}

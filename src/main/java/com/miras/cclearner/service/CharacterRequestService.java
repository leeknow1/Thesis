package com.miras.cclearner.service;

import com.miras.cclearner.common.CustomValidator;
import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.*;
import com.miras.cclearner.repository.*;
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

import static com.miras.cclearner.service.ServiceUtils.getFormattedDate;
import static com.miras.cclearner.service.ServiceUtils.splitString;
import static com.miras.cclearner.service.ServiceUtils.getRequestName;

@Service
@RequiredArgsConstructor
public class CharacterRequestService {

    private final CharacterEntityRepository charRepository;
    private final FilePathUtils filePathUtils;
    private final UserEntityRepository userRepository;
    private final CustomValidator customValidator;
    private final LikesEntityRepository likesRepository;

    public String getRequestCharacters(Model model) {
        List<CharactersEntity> characters = charRepository.findAllModified();
        model.addAttribute("character", characters);

        if(characters.size() != 0)
            model.addAttribute("path", filePathUtils.getCharRequestPath());

        return "request/allCharsRequest";
    }

    public String getOneRequestCharacter(@PathVariable(name = "id") Long charId, Model model) {
        CharactersEntity character = charRepository.findById(charId).orElseThrow();

        String name = getRequestName(character.getName());

        model.addAttribute("character", character);
        model.addAttribute("videoPath", filePathUtils.getCharRequestVidPath(name));
        model.addAttribute("audPath", filePathUtils.getCharRequestAudPath(name));

        return "request/oneRequestChar";
    }

    public String requestAddCharacter(@ModelAttribute("character") CharactersEntity character) {
        return "request/requestAddChar";
    }

    public String requestAddCharacter(@ModelAttribute("character") CharactersEntity character,
                                      @RequestParam(value = "img") MultipartFile img,
                                      @RequestParam(value = "aud") MultipartFile aud,
                                      @RequestParam(value = "vid") MultipartFile vid,
                                      BindingResult bindingResult,
                                      Model model,
                                      Principal principal) {
        if (bindingResult.hasErrors()) {
            return requestAddCharacter(character);
        }

        if (customValidator.checkCharImgName(img.getOriginalFilename())) {
            model.addAttribute("isImgInvalid", true);
            return requestAddCharacter(character);
        }

        if (customValidator.checkCharAudName(aud.getOriginalFilename())) {
            model.addAttribute("isAudInvalid", true);
            return requestAddCharacter(character);
        }

        if (customValidator.checkCharVidName(vid.getOriginalFilename())) {
            model.addAttribute("isVidInvalid", true);
            return requestAddCharacter(character);
        }

        Path path = Paths.get(filePathUtils.getCharAbsRequestPath() + "/" + character.getName());
        Path pathImg = Paths.get(filePathUtils.getCharAbsRequestImgPath(character.getName()));
        Path pathAud = Paths.get(filePathUtils.getCharAbsRequestAudPath(character.getName()));
        Path pathVid = Paths.get(filePathUtils.getCharAbsRequestVidPath(character.getName()));

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

        UserEntity user = userRepository.findByUsername(principal.getName());

        CharactersEntity request = new CharactersEntity();
        request.setName(character.getName());
        request.setDescription(character.getDescription());
        request.setExample(character.getExample());
        request.setVideoName(character.getExample());
        request.setAuthor(user.getUsername());
        request.setImageName(img.getOriginalFilename());
        request.setAudioName(aud.getOriginalFilename());
        request.setVideoName(vid.getOriginalFilename());
        request.setUpdatedDate(getFormattedDate());
        request.setLikes(0);
        request.setDislikes(0);
        request.setStatus("REQUEST");

        charRepository.save(request);

        return "redirect:/api/home";
    }

    public String requestEditCharacter(@PathVariable(name = "id") Long charId,
                                       Model model) {
        model.addAttribute("character", charRepository.findById(charId).orElseThrow());
        return "request/requestEditChar";
    }

    public String requestEditCharacter(@PathVariable(name = "id") Long charId,
                                       @ModelAttribute("character") CharactersEntity character,
                                       @RequestParam(value = "img") MultipartFile img,
                                       @RequestParam(value = "aud") MultipartFile aud,
                                       @RequestParam(value = "vid") MultipartFile vid,
                                       BindingResult bindingResult,
                                       Model model,
                                       Principal principal) {

        if (bindingResult.hasErrors()) {
            return requestEditCharacter(charId, model);
        }

        if (customValidator.checkCharImgName(img.getOriginalFilename())) {
            model.addAttribute("isImgInvalid", true);
            return requestEditCharacter(charId, model);
        }

        if (customValidator.checkCharAudName(aud.getOriginalFilename())) {
            model.addAttribute("isAudInvalid", true);
            return requestEditCharacter(charId, model);
        }

        if (customValidator.checkCharVidName(vid.getOriginalFilename())) {
            model.addAttribute("isVidInvalid", true);
            return requestEditCharacter(charId, model);
        }

        CharactersEntity originalChar = charRepository.findById(charId).orElseThrow();
        UserEntity user = userRepository.findByUsername(principal.getName());

        CharactersEntity requestChar = new CharactersEntity();
        requestChar.setAuthor(user.getUsername());
        requestChar.setOriginalId(originalChar.getId());
        requestChar.setName(originalChar.getName() + " --> " + character.getName());
        requestChar.setDescription(originalChar.getDescription() + " --> " + character.getDescription());
        requestChar.setExample(originalChar.getExample() + " --> " + character.getExample());
        requestChar.setUpdatedDate(getFormattedDate());
        requestChar.setLikes(0);
        requestChar.setDislikes(0);
        requestChar.setStatus("REQUEST");

        requestChar.setImageName(null);
        requestChar.setAudioName(null);
        requestChar.setVideoName(null);

        Path path = Paths.get(filePathUtils.getCharAbsRequestPath() + "/" + originalChar.getName());
        Path pathImg = Paths.get(filePathUtils.getCharAbsRequestImgPath(originalChar.getName()));
        Path pathAud = Paths.get(filePathUtils.getCharAbsRequestAudPath(originalChar.getName()));
        Path pathVid = Paths.get(filePathUtils.getCharAbsRequestVidPath(originalChar.getName()));

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
            if (!img.isEmpty()) {
                img.transferTo(new File(pathImg + "/" + img.getOriginalFilename()));
                requestChar.setImageName(img.getOriginalFilename());
            }
            if (!aud.isEmpty()) {
                aud.transferTo(new File(pathAud + "/" + aud.getOriginalFilename()));
                requestChar.setAudioName(aud.getOriginalFilename());
            }
            if (!vid.isEmpty()) {
                vid.transferTo(new File(pathVid + "/" + vid.getOriginalFilename()));
                requestChar.setVideoName(vid.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        charRepository.save(requestChar);

        return "redirect:/api/home";
    }

    public String requestApproved(@PathVariable(name = "id") Long charId) {
        CharactersEntity request = charRepository.findById(charId).orElseThrow();

        if(request.getOriginalId() != null) {
            CharactersEntity character = charRepository.findById(request.getOriginalId()).orElseThrow();
            try {
                if (request.getImageName() != null) {
                    Path oldImg = Paths.get(filePathUtils.getCharImgAbsPath(character.getName()) + "/" + character.getImageName());
                    Files.delete(oldImg);
                    Path from = Paths.get(filePathUtils.getCharAbsRequestImgPath(character.getName()) + "/" + request.getImageName());
                    Path to = Paths.get(filePathUtils.getCharImgAbsPath(character.getName()) + "/" + request.getImageName());
                    Files.move(from, to);
                    character.setImageName(request.getImageName());
                }
                if (request.getAudioName() != null) {
                    Path oldAud = Paths.get(filePathUtils.getCharAudAbsPath(character.getName()) + "/" + character.getAudioName());
                    Files.delete(oldAud);
                    Path from = Paths.get(filePathUtils.getCharAbsRequestAudPath(character.getName()) + "/" + request.getAudioName());
                    Path to = Paths.get(filePathUtils.getCharAudAbsPath(character.getName()) + "/" + request.getAudioName());
                    Files.move(from, to);
                    character.setAudioName(request.getAudioName());
                }
                if (request.getVideoName() != null) {
                    Path oldVid = Paths.get(filePathUtils.getCharVidAbsPath(character.getName()) + "/" + character.getVideoName());
                    Files.delete(oldVid);
                    Path from = Paths.get(filePathUtils.getCharAbsRequestVidPath(character.getName()) + "/" + request.getVideoName());
                    Path to = Paths.get(filePathUtils.getCharVidAbsPath(character.getName()) + "/" + request.getVideoName());
                    Files.move(from, to);
                    character.setVideoName(request.getVideoName());
                }
                File oldDirName = new File(filePathUtils.getCharAbsPath(character.getName()));
                File newDirName = new File(filePathUtils.getCharAbsPath(splitString(request.getName())));
                oldDirName.renameTo(newDirName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            character.setName(splitString(request.getName()));
            character.setDescription(splitString(request.getDescription()));
            character.setExample(splitString(request.getExample()));
            character.setAuthor(request.getAuthor());
            character.setUpdatedDate(getFormattedDate());
            charRepository.save(character);
            charRepository.deleteById(charId);
        } else {
            request.setStatus("APPROVED");
            request.setLikes(0);
            request.setDislikes(0);
            request.setUpdatedDate(getFormattedDate());

            Path path = Paths.get(filePathUtils.getCharAbsPath() + "/" + request.getName());
            Path pathImg = Paths.get(filePathUtils.getCharImgAbsPath(request.getName()));
            Path pathAud = Paths.get(filePathUtils.getCharAudAbsPath(request.getName()));
            Path pathVid = Paths.get(filePathUtils.getCharVidAbsPath(request.getName()));

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
                if (request.getImageName() != null) {
                    Path from = Paths.get(filePathUtils.getCharAbsRequestImgPath(request.getName()) + "/" + request.getImageName());
                    Path to = Paths.get(filePathUtils.getCharImgAbsPath(request.getName()) + "/" + request.getImageName());
                    Files.move(from, to);
                }
                if (request.getAudioName() != null) {
                    Path from = Paths.get(filePathUtils.getCharAbsRequestAudPath(request.getName()) + "/" + request.getAudioName());
                    Path to = Paths.get(filePathUtils.getCharAudAbsPath(request.getName()) + "/" + request.getAudioName());
                    Files.move(from, to);
                }
                if (request.getVideoName() != null) {
                    Path from = Paths.get(filePathUtils.getCharAbsRequestVidPath(request.getName()) + "/" + request.getVideoName());
                    Path to = Paths.get(filePathUtils.getCharVidAbsPath(request.getName()) + "/" + request.getVideoName());
                    Files.move(from, to);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            charRepository.save(request);
        }

        return "redirect:/api/characters/request";
    }

    public String requestDisapproved(@PathVariable(name = "id") Long charId) {
        CharactersEntity request = charRepository.findById(charId).orElseThrow();
        String name = getRequestName(request.getName());

        try {
            if (request.getImageName() != null) {
                Path pathImgRequest = Paths.get(filePathUtils.getCharAbsRequestImgPath(name) + "/" + request.getImageName());
                Files.delete(pathImgRequest);
            }
            if (request.getAudioName() != null) {
                Path pathAudRequest = Paths.get(filePathUtils.getCharAbsRequestAudPath(name) + "/" + request.getAudioName());
                Files.delete(pathAudRequest);
            }
            if (request.getVideoName() != null) {
                Path pathVidRequest = Paths.get(filePathUtils.getCharAbsRequestVidPath(name) + "/" + request.getVideoName());
                Files.delete(pathVidRequest);
            }

            charRepository.deleteById(charId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/api/characters/request";
    }

    public String likeRequestCharacter(@PathVariable(name = "id") Long charId, Principal principal) {
        CharactersEntity character = charRepository.findById(charId).orElseThrow();
        UserEntity user = userRepository.findByUsername(principal.getName());

        LikesEntity like = likesRepository.isUserLikedChar(user.getId(), charId);
        LikesEntity dislike = likesRepository.isUserDislikedChar(user.getId(), charId);

        if (like == null) {
            if (dislike != null) {
                character.setDislikes(character.getDislikes() - 1);
                likesRepository.delete(dislike);
            }

            character.setLikes(character.getLikes() + 1);
            LikesEntity likes = new LikesEntity();
            likes.setUserId(user.getId());
            likes.setCharId(charId);
            likes.setLiked(true);
            likesRepository.save(likes);
            charRepository.save(character);
        } else {
            character.setLikes(character.getLikes() - 1);
            likesRepository.delete(like);
            charRepository.save(character);
        }

        return "redirect:/api/characters/request";
    }

    public String dislikeRequestCharacter(@PathVariable(name = "id") Long charId, Principal principal) {
        CharactersEntity character = charRepository.findById(charId).orElseThrow();
        UserEntity user = userRepository.findByUsername(principal.getName());

        LikesEntity like = likesRepository.isUserLikedChar(user.getId(), charId);
        LikesEntity dislike = likesRepository.isUserDislikedChar(user.getId(), charId);

        if (dislike == null) {
            if (like != null) {
                character.setLikes(character.getLikes() - 1);
                likesRepository.delete(like);
            }
            character.setDislikes(character.getDislikes() + 1);
            LikesEntity likes = new LikesEntity();
            likes.setUserId(user.getId());
            likes.setCharId(charId);
            likes.setDisliked(true);
            likesRepository.save(likes);
            charRepository.save(character);
        } else {
            character.setDislikes(character.getDislikes() - 1);
            likesRepository.delete(dislike);
            charRepository.save(character);
        }

        return "redirect:/api/characters/request";
    }
}

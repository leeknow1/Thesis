package com.miras.cclearner.service;

import com.miras.cclearner.common.CustomValidator;
import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.Character;
import com.miras.cclearner.entity.Likes;
import com.miras.cclearner.entity.Users;
import com.miras.cclearner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

import static com.miras.cclearner.service.ServiceUtils.*;

@Service
@RequiredArgsConstructor
public class CharacterRequestService {

    private final CharacterRepository charRepository;
    private final FilePathUtils filePathUtils;
    private final UserRepository userRepository;
    private final CustomValidator customValidator;
    private final LikesRepository likesRepository;
    private final CategoryRepository categoryRepository;
    private final RolesRepository roleRepository;

    public String getRequestCharacters(Model model) {
        List<Character> characters = charRepository.findAllModified();

        if (characters.size() != 0) {
            for (Character character : characters) {
                character.setName(getRequestName(character.getName()));
            }
            model.addAttribute("path", filePathUtils.getRequestPath());
        }

        model.addAttribute("character", characters);

        return "request/allCharsRequest";
    }

    public String getUserRequestCharacters(Principal principal, Model model) {
        List<Character> characters = charRepository.findAllRequestByUser(principal.getName());
        if (characters.size() != 0) {
            for (Character character : characters) {
                character.setName(getRequestName(character.getName()));
            }
            model.addAttribute("path", filePathUtils.getRequestPath());
        }

        model.addAttribute("character", characters);

        return "request/allCharsRequest";
    }

    public String getOneRequestCharacter(@PathVariable(name = "id") Long charId, Model model) {
        Character character = charRepository.findById(charId).orElseThrow();

        String name = getRequestName(character.getName());

        model.addAttribute("character", character);
        model.addAttribute("videoPath", filePathUtils.getRequestPathVid(name));
        model.addAttribute("audPath", filePathUtils.getRequestPathAud(name));

        return "request/oneRequestChar";
    }

    public String requestAddCharacter(@ModelAttribute("character") Character character, Model model) {
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "request/requestAddChar";
    }

    public String requestAddCharacter(@ModelAttribute("character") Character character,
                                      @RequestParam(value = "img") MultipartFile img,
                                      @RequestParam(value = "aud") MultipartFile aud,
                                      @RequestParam(value = "vid") MultipartFile vid,
                                      BindingResult bindingResult,
                                      Model model,
                                      Principal principal) {
        if (bindingResult.hasErrors()) {
            return requestAddCharacter(character, model);
        }

        if (customValidator.checkName(character.getName())) {
            model.addAttribute("isNameInvalid", true);
            return requestAddCharacter(character, model);
        }

        if (customValidator.checkCharImgName(img.getOriginalFilename())) {
            model.addAttribute("isImgInvalid", true);
            return requestAddCharacter(character, model);
        }

        if (customValidator.checkCharAudName(aud.getOriginalFilename())) {
            model.addAttribute("isAudInvalid", true);
            return requestAddCharacter(character, model);
        }

        if (customValidator.checkCharVidName(vid.getOriginalFilename())) {
            model.addAttribute("isVidInvalid", true);
            return requestAddCharacter(character, model);
        }

        Path path = Paths.get(filePathUtils.getRequestAbsPath(character.getName()));
        Path pathImg = Paths.get(filePathUtils.getRequestAbsPathImg(character.getName()));
        Path pathAud = Paths.get(filePathUtils.getRequestAbsPathAud(character.getName()));
        Path pathVid = Paths.get(filePathUtils.getRequestAbsPathVid(character.getName()));

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

        Users user = userRepository.findByUsername(principal.getName());

        Character request = new Character();
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
        request.setCategory(character.getCategory());

        charRepository.save(request);

        return "redirect:/api/home";
    }

    public String requestEditCharacter(@PathVariable(name = "id") Long charId,
                                       Model model) {
        model.addAttribute("character", charRepository.findById(charId).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "request/requestEditChar";
    }

    public String requestEditCharacter(@PathVariable(name = "id") Long charId,
                                       @ModelAttribute("character") Character character,
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

        Character originalChar = charRepository.findById(charId).orElseThrow();

        if (!originalChar.getName().equals(character.getName())) {
            if (customValidator.checkName(character.getName())) {
                model.addAttribute("isNameInvalid", true);
                return requestEditCharacter(charId, model);
            }
        }

        Users user = userRepository.findByUsername(principal.getName());

        Character requestChar = new Character();
        requestChar.setAuthor(user.getUsername());
        requestChar.setOriginalId(originalChar.getId());
        requestChar.setName(originalChar.getName() + " --> " + character.getName());
        requestChar.setDescription(originalChar.getDescription() + " --> " + character.getDescription());
        requestChar.setExample(originalChar.getExample() + " --> " + character.getExample());
        requestChar.setUpdatedDate(getFormattedDate());
        requestChar.setLikes(0);
        requestChar.setDislikes(0);
        requestChar.setStatus("REQUEST");
        requestChar.setCategory(character.getCategory());

        requestChar.setImageName(null);
        requestChar.setAudioName(null);
        requestChar.setVideoName(null);

        Path path = Paths.get(filePathUtils.getRequestAbsPath(character.getName()));
        Path pathImg = Paths.get(filePathUtils.getRequestAbsPathImg(character.getName()));
        Path pathAud = Paths.get(filePathUtils.getRequestAbsPathAud(character.getName()));
        Path pathVid = Paths.get(filePathUtils.getRequestAbsPathVid(character.getName()));

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
        Character request = charRepository.findById(charId).orElseThrow();

        if(request.getOriginalId() != null) {
            Character character = charRepository.findById(request.getOriginalId()).orElseThrow();
            try {
                if (request.getImageName() != null) {
                    Path oldImg = Paths.get(filePathUtils.getCharAbsPathImg(character.getName()) + "/" + character.getImageName());
                    Files.delete(oldImg);
                    Path from = Paths.get(filePathUtils.getRequestAbsPathImg(getRequestName(request.getName())) + "/" + request.getImageName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathImg(character.getName()) + "/" + request.getImageName());
                    Files.move(from, to);
                    character.setImageName(request.getImageName());
                }
                if (request.getAudioName() != null) {
                    Path oldAud = Paths.get(filePathUtils.getCharAbsPathAud(character.getName()) + "/" + character.getAudioName());
                    Files.delete(oldAud);
                    Path from = Paths.get(filePathUtils.getRequestAbsPathAud(getRequestName(request.getName())) + "/" + request.getAudioName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathAud(character.getName()) + "/" + request.getAudioName());
                    Files.move(from, to);
                    character.setAudioName(request.getAudioName());
                }
                if (request.getVideoName() != null) {
                    Path oldVid = Paths.get(filePathUtils.getCharAbsPathVid(character.getName()) + "/" + character.getVideoName());
                    Files.delete(oldVid);
                    Path from = Paths.get(filePathUtils.getRequestAbsPathVid(getRequestName(request.getName())) + "/" + request.getVideoName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathVid(character.getName()) + "/" + request.getVideoName());
                    Files.move(from, to);
                    character.setVideoName(request.getVideoName());
                }
                File oldDirName = new File(filePathUtils.getCharAbsPath(character.getName()));
                File newDirName = new File(filePathUtils.getCharAbsPath(getRequestName(request.getName())));
                oldDirName.renameTo(newDirName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            character.setName(getRequestName(request.getName()));
            character.setDescription(getRequestName(request.getDescription()));
            character.setExample(getRequestName(request.getExample()));
            character.setAuthor(request.getAuthor());
            character.setUpdatedDate(getFormattedDate());
            character.setCategory(request.getCategory());
            charRepository.deleteById(charId);
            charRepository.save(character);
        } else {
            request.setStatus("APPROVED");
            request.setLikes(0);
            request.setDislikes(0);
            request.setUpdatedDate(getFormattedDate());

            Path path = Paths.get(filePathUtils.getCharAbsPath() + "/" + request.getName());
            Path pathImg = Paths.get(filePathUtils.getCharAbsPathImg(request.getName()));
            Path pathAud = Paths.get(filePathUtils.getCharAbsPathAud(request.getName()));
            Path pathVid = Paths.get(filePathUtils.getCharAbsPathVid(request.getName()));

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
                    Path from = Paths.get(filePathUtils.getRequestAbsPathImg(request.getName()) + "/" + request.getImageName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathImg(request.getName()) + "/" + request.getImageName());
                    Files.move(from, to);
                }
                if (request.getAudioName() != null) {
                    Path from = Paths.get(filePathUtils.getRequestAbsPathAud(request.getName()) + "/" + request.getAudioName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathAud(request.getName()) + "/" + request.getAudioName());
                    Files.move(from, to);
                }
                if (request.getVideoName() != null) {
                    Path from = Paths.get(filePathUtils.getRequestAbsPathVid(request.getName()) + "/" + request.getVideoName());
                    Path to = Paths.get(filePathUtils.getCharAbsPathVid(request.getName()) + "/" + request.getVideoName());
                    Files.move(from, to);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            charRepository.save(request);
        }

        Users user = userRepository.findByUsername(request.getAuthor());
        user.setPoints(user.getPoints() + 10);
        user.setDownloads(user.getDownloads() + 10);
        if(user.getRole().getId() == 2 && user.getPoints() >= 100){
            user.setRole(roleRepository.findById(3L).orElseThrow());
        }
        userRepository.save(user);

        return "redirect:/api/characters/request";
    }

    public String requestDisapproved(@PathVariable(name = "id") Long charId) {
        Character request = charRepository.findById(charId).orElseThrow();
        String name = getRequestName(request.getName());

        try {
            if (request.getImageName() != null) {
                Path pathImgRequest = Paths.get(filePathUtils.getRequestAbsPathImg(name) + "/" + request.getImageName());
                Files.delete(pathImgRequest);
            }
            if (request.getAudioName() != null) {
                Path pathAudRequest = Paths.get(filePathUtils.getRequestAbsPathAud(name) + "/" + request.getAudioName());
                Files.delete(pathAudRequest);
            }
            if (request.getVideoName() != null) {
                Path pathVidRequest = Paths.get(filePathUtils.getRequestAbsPathVid(name) + "/" + request.getVideoName());
                Files.delete(pathVidRequest);
            }

            charRepository.deleteById(charId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/api/characters/request";
    }

    public String likeRequestCharacter(@PathVariable(name = "id") Long charId, Principal principal) {
        Character character = charRepository.findById(charId).orElseThrow();
        Users user = userRepository.findByUsername(principal.getName());

        Likes like = likesRepository.isUserLikedChar(user.getId(), charId);
        Likes dislike = likesRepository.isUserDislikedChar(user.getId(), charId);

        if (like == null) {
            if (dislike != null) {
                character.setDislikes(character.getDislikes() - 1);
                likesRepository.delete(dislike);
            }
            character.setLikes(character.getLikes() + 1);

            if (character.getLikes() - character.getDislikes() > 2) {
                requestApproved(charId);
            } else {
                Likes likes = new Likes();
                likes.setUserId(user.getId());
                likes.setCharId(charId);
                likes.setLiked(true);
                likesRepository.save(likes);
                charRepository.save(character);
            }
        } else {
            character.setLikes(character.getLikes() - 1);
            likesRepository.delete(like);
            charRepository.save(character);
        }

        return "redirect:/api/characters/request";
    }

    public String dislikeRequestCharacter(@PathVariable(name = "id") Long charId, Principal principal) {
        Character character = charRepository.findById(charId).orElseThrow();
        Users user = userRepository.findByUsername(principal.getName());

        Likes like = likesRepository.isUserLikedChar(user.getId(), charId);
        Likes dislike = likesRepository.isUserDislikedChar(user.getId(), charId);

        if (dislike == null) {
            if (like != null) {
                character.setLikes(character.getLikes() - 1);
                likesRepository.delete(like);
            }
            character.setDislikes(character.getDislikes() + 1);

            if (character.getDislikes() - character.getLikes() > 2) {
                requestDisapproved(charId);
            } else {
                Likes likes = new Likes();
                likes.setUserId(user.getId());
                likes.setCharId(charId);
                likes.setDisliked(true);
                likesRepository.save(likes);
                charRepository.save(character);
            }
        } else {
            character.setDislikes(character.getDislikes() - 1);
            likesRepository.delete(dislike);
            charRepository.save(character);
        }

        return "redirect:/api/characters/request";
    }
}

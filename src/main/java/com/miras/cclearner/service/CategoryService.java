package com.miras.cclearner.service;

import com.miras.cclearner.common.CustomValidator;
import com.miras.cclearner.common.FilePathUtils;
import com.miras.cclearner.entity.Category;
import com.miras.cclearner.entity.Character;
import com.miras.cclearner.repository.CategoryRepository;
import com.miras.cclearner.repository.CharacterRepository;
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
import java.util.ArrayList;
import java.util.List;

import static com.miras.cclearner.CclearnerApplication.passedOneHour;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final FilePathUtils filePathUtils;

    private final CustomValidator customValidator;

    private final CharacterRepository characterRepository;

    public String getCategories(String name, Model model) {
        List<Category> categories = new ArrayList<>();
        List<Character> characters = new ArrayList<>();

        if(name.isBlank()) {
            categories = categoryRepository.findAll(Sort.by("id"));
            model.addAttribute("path", filePathUtils.getCategoryPath());
        } else {
            name = "%" + name + "%";
            characters =  characterRepository.findAllByName(name);
            if (characters.size() != 0)
                model.addAttribute("path", filePathUtils.getCharPath());
        }

        model.addAttribute("category", categories);
        model.addAttribute("character", characters);

        if (passedOneHour()) {
            model.addAttribute("passedHour", true);
        }

        return "categoryChar";
    }

    public String addCharCategory(@ModelAttribute("category") Category category) {
        return "createCharCategory";
    }

    public String addCharCategory(@ModelAttribute("category") Category category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return addCharCategory(category);
        }

        if (customValidator.checkCharCategoryName(category.getName())) {
            model.addAttribute("isNameInvalid", true);
            return addCharCategory(category);
        }

        Path path = Paths.get(filePathUtils.getCategoryAbsPath(category.getName()));

        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            img.transferTo(new File(filePathUtils.getCategoryAbsPath(category.getName()) + "/" + img.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        category.setImgName(img.getOriginalFilename());
        categoryRepository.save(category);

        return "redirect:/api/home";
    }

    public String editCharCategory(@PathVariable(name = "category") Long cateId, Model model) {
        model.addAttribute("categoryObj", categoryRepository.findById(cateId).orElseThrow());
        return "editCharCategory";
    }

    public String editCharCategory(@PathVariable(name = "category") Long cateId, @ModelAttribute("categoryObj") Category category, @RequestParam("img") MultipartFile img, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return editCharCategory(cateId, model);
        }

        Category changingCategory = categoryRepository.findById(cateId).orElseThrow();

        if (!changingCategory.getName().equals(category.getName())) {
            File oldDirName = new File(filePathUtils.getCategoryAbsPath(changingCategory.getName()));
            File newDirName = new File(filePathUtils.getCategoryAbsPath(category.getName()));
            oldDirName.renameTo(newDirName);
            changingCategory.setName(category.getName());
        }


        try {
            if (!img.isEmpty()) {
                Path path = Paths.get(filePathUtils.getCategoryAbsPath(category.getName()) + "/" + changingCategory.getImgName());
                Files.delete(path);
                img.transferTo(new File(filePathUtils.getCategoryAbsPath(category.getName()) + "/" + img.getOriginalFilename()));
                changingCategory.setImgName(img.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        categoryRepository.save(changingCategory);

        return "redirect:/api/home";
    }

    public String deleteCategory(Long cateId) {
        Category category = categoryRepository.findById(cateId).orElseThrow();
        try{
            Path path = Paths.get(filePathUtils.getCategoryAbsPath(category.getName()));
            Path pathImg = Paths.get(filePathUtils.getCategoryAbsPath(category.getName()) + "/" + category.getImgName());
            Files.delete(pathImg);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        categoryRepository.delete(category);
        return "redirect:/api/home";
    }
}

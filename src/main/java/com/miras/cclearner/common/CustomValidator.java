package com.miras.cclearner.common;

import com.miras.cclearner.repository.CategoryEntityRepository;
import com.miras.cclearner.repository.CharacterEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomValidator {

    private final CharacterEntityRepository characterRepository;

    private final CategoryEntityRepository charCategoryRepository;

    public boolean checkCharImgName(String name) {
        return characterRepository.existsByImageName(name);
    }

    public boolean checkCharAudName(String name) {
        return characterRepository.existsByAudioName(name);
    }

    public boolean checkCharVidName(String name) {
        return characterRepository.existsByVideoName(name);
    }

    public boolean checkCharCategoryName(String name) {
        return charCategoryRepository.existsByName(name);
    }

    public boolean checkName(String name) {
        return characterRepository.existsByName(name);
    }
}

package com.miras.cclearner.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathUtils {

    @Value("${files.char.path}")
    private String charPath;

    @Value("${file.char.abs.path}")
    private String charAbsPath;

    @Value("${files.request.char.path}")
    private String charRequestPath;

    @Value("${file.request.char.abs.path}")
    private String charAbsRequestPath;

    @Value("${file.category.path}")
    private String categoryPath;

    @Value("${file.category.abs.path}")
    private String categoryAbsPath;

    public String getCategoryPath() {
        return categoryPath;
    }

    public String getCategoryAbsPath() {
        return categoryAbsPath;
    }

    public String getCategoryPath(String name) {
        return categoryPath + "/" + name;
    }

    public String getCategoryAbsPath(String name) {
        return categoryAbsPath + "/" + name;
    }

    public String getCharPath() {
        return charPath;
    }

    public String getCharCategoryPath(String categoryName) {
        return charPath + "/" + categoryName;
    }


    public String getCharImgPath(String categoryName) {
        return charPath + "/" + categoryName + "/img";
    }

    public String getCharVidPath(String categoryName) {
        return charPath + "/" + categoryName + "/vid";
    }

    public String getCharAudPath(String categoryName) {
        return charPath + "/" + categoryName + "/aud";
    }

    public String getCharAbsPath() {
        return charAbsPath;
    }

    public String getCharAbsPath(String name) {
        return charAbsPath + "/" + name;
    }

    public String getCharImgAbsPath(String name) {
        return charAbsPath + "/" + name + "/img";
    }

    public String getCharVidAbsPath(String name) {
        return charAbsPath + "/" + name + "/vid";
    }

    public String getCharAudAbsPath(String name) {
        return charAbsPath + "/" + name + "/aud";
    }

        /*
        REQUEST PATHS
         */

    public String getCharRequestPath() {
        return charRequestPath;
    }

    public String getCharRequestImgPath(String name) {
        return charRequestPath + "/" + name + "/img";
    }

    public String getCharRequestAudPath(String name) {
        return charRequestPath + "/" + name + "/aud";
    }

    public String getCharRequestVidPath(String name) {
        return charRequestPath + "/" + name + "/vid";
    }

    public String getCharAbsRequestPath() {
        return charAbsRequestPath;
    }

    public String getCharAbsRequestImgPath(String name) {
        return charAbsRequestPath + "/" + name + "/img";
    }

    public String getCharAbsRequestAudPath(String name) {
        return charAbsRequestPath + "/" + name + "/aud";
    }

    public String getCharAbsRequestVidPath(String name) {
        return charAbsRequestPath + "/" + name + "/vid";
    }
}

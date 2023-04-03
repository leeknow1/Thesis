package com.miras.cclearner.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathUtils {

    private final String charPath = "/characters/";

    private final String categoryPath = "/categories/";

    private final String requestPath = "/requests/";

    @Value("${files.char.path}")
    private String charAbsPath;

    @Value("${files.category.path}")
    private String categoryAbsPath;

    @Value("${files.request.path}")
    private String requestAbsPath;

    public String getCharPath() {
        return charPath;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public String getRequestPath() {
        return requestPath;
    }

    //ABSOLUTE PATH

    public String getCharAbsPath() {
        return charAbsPath;
    }

    public String getCategoryAbsPath() {
        return categoryAbsPath;
    }

    public String getRequestAbsPath() {
        return requestAbsPath;
    }

    public String getCharAbsPath(String name) {
        return charAbsPath + name;
    }

    public String getCategoryAbsPath(String name) {
        return categoryAbsPath + name;
    }

    public String getRequestAbsPath(String name) {
        return requestAbsPath + name;
    }

    //CHARACTER

    public String getCharPathImg(String name) {
        return charPath + name + "/img";
    }

    public String getCharPathAud(String name) {
        return charPath + name + "/aud";
    }

    public String getCharPathVid(String name) {
        return charPath + name + "/vid";
    }

    public String getCharAbsPathImg(String name) {
        return charAbsPath + name + "/img";
    }

    public String getCharAbsPathAud(String name) {
        return charAbsPath + name + "/aud";
    }

    public String getCharAbsPathVid(String name) {
        return charAbsPath + name + "/vid";
    }

    //REQUEST

    public String getRequestPathImg(String name) {
        return requestPath + name + "/img";
    }

    public String getRequestPathAud(String name) {
        return requestPath + name + "/aud";
    }

    public String getRequestPathVid(String name) {
        return requestPath + name + "/vid";
    }

    public String getRequestAbsPathImg(String name) {
        return requestAbsPath + name + "/img";
    }

    public String getRequestAbsPathAud(String name) {
        return requestAbsPath + name + "/aud";
    }

    public String getRequestAbsPathVid(String name) {
        return requestAbsPath + name + "/vid";
    }
}

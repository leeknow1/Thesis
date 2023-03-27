package com.miras.cclearner.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String password;
    private String roleID;

    public UserDTO() {
    }

    public UserDTO(String username) {
        this.username = username;
    }
}

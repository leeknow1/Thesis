package com.miras.cclearner.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String email;
    private String username;
    private String password;
    private String roleID;
    private Integer points;
    private Integer downloads;
}

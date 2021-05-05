package com.application.imagerepo.transferObjects;

import lombok.Data;

@Data
public class UserTO {

    private Long id;

    private String username;

    private String password;

    private String email;
}

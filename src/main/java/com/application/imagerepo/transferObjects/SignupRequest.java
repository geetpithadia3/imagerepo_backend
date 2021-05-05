package com.application.imagerepo.transferObjects;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;

    private String username;

    private String password;
}

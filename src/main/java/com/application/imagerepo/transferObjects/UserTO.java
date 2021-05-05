package com.application.imagerepo.transferObjects;

import lombok.Data;

import java.util.List;

@Data
public class UserTO {

    private Long id;

    private String username;

    private String password;

    private String email;

    private List<ImageTO> images;
}

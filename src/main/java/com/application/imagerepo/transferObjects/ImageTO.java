package com.application.imagerepo.transferObjects;

import com.application.imagerepo.user.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


import java.io.Serializable;
import java.util.Date;

@Data
public class ImageTO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String tags;

    private String storageURL;

    private Date timstamp;

    private User user;

    private MultipartFile imageFile;
}

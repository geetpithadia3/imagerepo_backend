package com.application.imagerepo.transferObjects;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageFormTO {

    private String title;

    private String description;

    private MultipartFile imageFile;
}

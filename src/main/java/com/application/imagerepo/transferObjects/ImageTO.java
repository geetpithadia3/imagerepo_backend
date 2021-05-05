package com.application.imagerepo.transferObjects;

import lombok.Data;


import java.io.Serializable;
import java.util.Date;

@Data
public class ImageTO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String tags;

    private String storageURL;

    private Date timestamp;
}

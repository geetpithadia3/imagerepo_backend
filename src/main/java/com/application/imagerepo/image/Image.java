package com.application.imagerepo.image;

import com.application.imagerepo.user.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "images")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    String title;

    @Column(nullable = true)
    String description;

    @Column(nullable = true)
    private String tags;

    @Column(nullable = false)
    private String accessType;

    @Column(nullable = true)
    private String storageURL;

    @Column
    private Date timestamp;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
}

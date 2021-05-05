package com.application.imagerepo.user;

import com.application.imagerepo.image.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String role;

    @OneToMany(mappedBy="user")
    @JsonIgnoreProperties("user")
    private List<Image> images;


}

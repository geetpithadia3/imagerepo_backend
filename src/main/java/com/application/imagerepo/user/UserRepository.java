package com.application.imagerepo.user;

import com.application.imagerepo.image.Image;
import com.application.imagerepo.user.views.UserDetailsView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    public Optional<User> findByUsername(String username);

    public Optional<UserDetailsView> findViewById(Long id);



}

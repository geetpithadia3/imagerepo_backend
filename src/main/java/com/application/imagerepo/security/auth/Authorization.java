package com.application.imagerepo.security.auth;

import com.application.imagerepo.security.jwt.JWTTokenUtil;
import com.application.imagerepo.user.views.UserDetailsView;
import com.application.imagerepo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Authorization {

    @Autowired
    JWTTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;


    public Boolean isAuthorized(String authHeader, Long userId) throws Exception {
        String token = authHeader.split(" ")[1].trim();
        String tokenUserName = jwtTokenUtil.getUsernameFromToken(token);
        Optional<UserDetailsView> userDetails = userService.findUserViewById(userId);
        if (!userDetails.isPresent())
            //TODO: Change Exception
            throw new Exception("User not found");
        if (!userDetails.get().getUsername().equals(tokenUserName))
            return false;
        return true;

    }

    public Boolean isAuthorized(String authHeader, String username) throws Exception {
        String token = authHeader.split(" ")[1].trim();
        String tokenUserName = jwtTokenUtil.getUsernameFromToken(token);
        if (!username.equals(tokenUserName))
            return false;
        return true;

    }

}

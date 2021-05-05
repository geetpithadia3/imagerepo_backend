package com.application.imagerepo.user;

import com.application.imagerepo.transferObjects.SignupRequest;
import com.application.imagerepo.transferObjects.SignupResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> addUser(@RequestBody SignupRequest user) {
        try {
            return ResponseEntity.ok()
                    .body(modelMapper.map(userService.addUser(modelMapper.map(user, User.class)), SignupResponse.class));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

package com.application.imagerepo.user;

import com.application.imagerepo.transferObjects.ImageTO;
import com.application.imagerepo.transferObjects.SignupRequest;
import com.application.imagerepo.transferObjects.SignupResponse;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user/{id}/images")
    public ResponseEntity<List<ImageTO>> getUserImages(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok()
                    .body(userService.getUserImages(id, authorizationHeader));
        } catch (BadCredentialsException | NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

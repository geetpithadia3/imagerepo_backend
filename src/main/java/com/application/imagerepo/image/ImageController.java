package com.application.imagerepo.image;

import com.application.imagerepo.transferObjects.ImageFormTO;
import com.application.imagerepo.transferObjects.ImageTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    private final ModelMapper modelMapper;

    public ImageController(ImageService imageService, ModelMapper modelMapper) {
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/image")
    public ResponseEntity<ImageTO> uploadImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @ModelAttribute ImageFormTO imageForm) throws IOException {
        try {
            return ResponseEntity.ok().body(modelMapper.map(imageService.uploadImageToS3AndPersist(imageForm, authorizationHeader), ImageTO.class));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity deleteImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("id") Long id) {
        try {
            imageService.authorizeUserAndDeleteImage(id, authorizationHeader);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/image")
    public ResponseEntity<List<ImageTO>> showAllPublicImages() {
        try {
            return ResponseEntity.ok().body(imageService.getPublicImages().stream().map(image -> modelMapper.map(image, ImageTO.class)).collect(Collectors.toList()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/image/user/{userId}")
    public ResponseEntity<List<ImageTO>> showAllUserImages(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok().body(imageService.getUserImages(authorizationHeader, userId).stream().map(image -> modelMapper.map(image, ImageTO.class)).collect(Collectors.toList()));
        } catch (BadCredentialsException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

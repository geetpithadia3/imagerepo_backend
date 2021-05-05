package com.application.imagerepo.image;

import com.application.imagerepo.transferObjects.ImageFormTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/image")
    public ResponseEntity uploadImage(@ModelAttribute ImageFormTO imageForm) throws IOException {
        try {
            imageService.uploadImageToS3AndPersist(imageForm);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity deleteImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("id") Long id) {
        try {
            imageService.authorizaUserAndDeleteImage(id, authorizationHeader);
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/image")
    public ResponseEntity<List<Image>> showAllImages() {
        try {
            return ResponseEntity.ok().body(imageService.getPublicImages());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

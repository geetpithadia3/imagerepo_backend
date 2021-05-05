package com.application.imagerepo.image;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.application.imagerepo.security.jwt.JWTTokenUtil;
import com.application.imagerepo.transferObjects.ImageFormTO;
import com.application.imagerepo.transferObjects.ImageTO;
import com.application.imagerepo.utils.Rekognition;
import com.application.imagerepo.utils.S3;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    S3 s3Client;

    @Autowired
    Rekognition rekognitionClient;

    @Autowired
    JWTTokenUtil jwtTokenUtil;

    public void uploadImageToS3AndPersist(ImageFormTO imageForm) throws IOException {
        File file = new File("src/main/resources/" + imageForm.getImageFile().getOriginalFilename());

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(imageForm.getImageFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Upload Image to S3
        Image imageObjectToPersist = modelMapper.map(imageForm, Image.class);
        imageObjectToPersist.setTags(String.join(",", rekognitionClient.getImageLabels(file)));
        imageObjectToPersist.setStorageURL(s3Client.uploadFile(file));
        imageObjectToPersist.setAccessType(ImageAccessType.PUBLIC.toString());
        imageRepository.save(imageObjectToPersist);
    }

    public List<Image> getImagesByUser(Long userId) {
        return imageRepository.findImagesByUser_Id(userId);
    }

    public List<Image> getPublicImages() {
        return imageRepository.findImagesByAccessType(ImageAccessType.PUBLIC.toString());
    }

    public void authorizaUserAndDeleteImage(Long id, String authorizationHeader) throws Exception {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.split(" ")[1].trim();
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            Optional<Image> image = imageRepository.findById(id);
            if(!image.isPresent())
                throw new Exception("Image not found!");
            if(!image.get().getUser().getUsername().equals(tokenUsername))
                throw new BadCredentialsException("Not Authorized");
            s3Client.deleteFile(image.get().getTitle());
            imageRepository.deleteById(id);
        }


    }

}

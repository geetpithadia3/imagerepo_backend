package com.application.imagerepo.image;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.application.imagerepo.security.jwt.JWTTokenUtil;
import com.application.imagerepo.transferObjects.ImageFormTO;
import com.application.imagerepo.transferObjects.ImageTO;
import com.application.imagerepo.transferObjects.UserTO;
import com.application.imagerepo.user.User;
import com.application.imagerepo.user.UserRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    S3 s3Client;

    @Autowired
    Rekognition rekognitionClient;

    @Autowired
    JWTTokenUtil jwtTokenUtil;

    public Image uploadImageToS3AndPersist(ImageFormTO imageForm, String authorizationHeader) throws IOException {

        Image imageObjectToPersist = modelMapper.map(imageForm, Image.class);
        //Fetching user details from authorization token
        String token = authorizationHeader.split(" ")[1].trim();
        String tokenUserName = jwtTokenUtil.getUsernameFromToken(token);
        imageObjectToPersist.setUser(userRepository.findByUsername(tokenUserName));
        imageObjectToPersist.setAccessType(ImageAccessType.PUBLIC.name());
        imageObjectToPersist.setTimestamp(new Date());
        // Converting MultipartFile to File
        File file = new File("src/main/resources/" + imageForm.getImageFile().getOriginalFilename() + "_" + tokenUserName);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(imageForm.getImageFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fetching labels from AWS Rekognition
        imageObjectToPersist.setTags(String.join(",", rekognitionClient.getImageLabels(file)));
        // Upload Image to S3
        imageObjectToPersist.setStorageURL(s3Client.uploadFile(file));
        file.delete();
        return imageRepository.save(imageObjectToPersist);


    }

    public List<Image> getImagesByUser(Long userId) {
        return imageRepository.findImagesByUser_Id(userId);
    }

    public List<Image> getPublicImages() {
        return imageRepository.findImagesByAccessType(ImageAccessType.PUBLIC.toString());
    }

    public void authorizeUserAndDeleteImage(Long id, String authorizationHeader) throws Exception {

        String token = authorizationHeader.split(" ")[1].trim();
        Optional<Image> image = imageRepository.findById(id);
        if (!image.isPresent())
            throw new Exception("Image not found!");
        if (!jwtTokenUtil.isAuthorized(token, modelMapper.map(image.get().getUser(), UserTO.class)))
            throw new BadCredentialsException("Not Authorized");
        s3Client.deleteFile(image.get().getTitle() + "_" + image.get().getUser().getUsername());
        imageRepository.deleteById(id);


    }

}

package com.application.imagerepo.image;

import com.application.imagerepo.security.auth.Authorization;
import com.application.imagerepo.security.jwt.JWTTokenUtil;
import com.application.imagerepo.transferObjects.ImageFormTO;
import com.application.imagerepo.transferObjects.ImageTO;
import com.application.imagerepo.transferObjects.UserTO;
import com.application.imagerepo.user.UserRepository;
import com.application.imagerepo.utils.ImageUtils;
import com.application.imagerepo.utils.Rekognition;
import com.application.imagerepo.utils.S3;
import com.fasterxml.jackson.core.JsonParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @Autowired
    Authorization authorization;

    public Image uploadImageToS3AndPersist(ImageFormTO imageForm, String authorizationHeader) throws IOException {

        Image imageObjectToPersist = modelMapper.map(imageForm, Image.class);

        //Fetching user details from authorization token
        String token = authorizationHeader.split(" ")[1].trim();
        String tokenUserName = jwtTokenUtil.getUsernameFromToken(token);
        imageObjectToPersist.setUser(userRepository.findByUsername(tokenUserName).get());
        imageObjectToPersist.setTimestamp(new Date());
        imageObjectToPersist.setStorageObjectName(ImageUtils.formatTitle(imageForm.getTitle(), tokenUserName));

        // Converting MultipartFile to File
        File file = convertToFile(imageForm.getImageFile(), imageObjectToPersist.getStorageObjectName());

        //Saving to S3
        s3Client.uploadFile(file);

        // Fetching labels from AWS Rekognition
        imageObjectToPersist.setTags(String.join(",", rekognitionClient.getImageLabels(file)));

        file.delete();
        return imageRepository.save(imageObjectToPersist);


    }

    public List<ImageTO> getUserImages(String authorizationHeader, Long userId) throws Exception {
        if (!authorization.isAuthorized(authorizationHeader, userId)) {
            throw new BadCredentialsException("Not Authorized");
        }

        List<Image> userImages = getImagesByUser(userId);
        return userImages.parallelStream().map(image -> populateImageContent(image)).collect(Collectors.toList());
    }

    private File convertToFile(MultipartFile mfile, String fileName) throws IOException {
        File file = new File(fileName);
        file.deleteOnExit();
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(mfile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public List<ImageTO> getPublicImages() {
        List<Image> images = imageRepository.findImagesByAccessType(ImageAccessType.PUBLIC.toString());
        return images.parallelStream().map(image -> populateImageContent(image)).collect(Collectors.toList());

    }

    private ImageTO populateImageContent(Image image) {
        ImageTO imageWithContent = modelMapper.map(image, ImageTO.class);
        try {
                imageWithContent.setContent(ImageUtils.getEncodedContent(s3Client.getImage(image.getStorageObjectName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageWithContent;
    }

    private List<Image> getImagesByUser(long userid) {
        return imageRepository.findImagesByUser_Id(userid);
    }


    public void authorizeUserAndDeleteImage(Long id, String authorizationHeader) throws Exception {
        Optional<Image> image = imageRepository.findById(id);
        if (!image.isPresent())
            throw new Exception("Image not found!");
        if (!authorization.isAuthorized(authorizationHeader, modelMapper.map(image.get().getUser(), UserTO.class).getUsername()))
            // TODO: Change the type of exception
            throw new BadCredentialsException("Not Authorized");
        s3Client.deleteFile(image.get().getStorageObjectName());
        imageRepository.deleteById(id);
    }


}

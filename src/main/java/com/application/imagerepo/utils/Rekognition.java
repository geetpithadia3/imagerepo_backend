package com.application.imagerepo.utils;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Rekognition {
    final String bucket_name = "imagerepostoragepublic";
    final String object_url = "https://imagerepostoragepublic.s3.amazonaws.com/";
    final AmazonRekognition rekognitionClient;

    public Rekognition() {
        rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new EnvironmentVariableCredentialsProvider()).withRegion(Regions.US_EAST_1).build();
    }

    public List<String> getImageLabels(File image) throws IOException {
        ByteBuffer imageBytes;
        try (InputStream inputStream = new FileInputStream(image)) {
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withBytes(imageBytes))
                .withMaxLabels(10)
                .withMinConfidence(77F);
        try {

            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            return result.getLabels().stream().map(label -> label.getName()).collect(Collectors.toList());


        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
            throw e;
        }
    }

}

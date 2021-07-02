package com.application.imagerepo.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class S3 {
    final String bucket_name = "imagerepostoragepublic";
    final String object_url = "https://imagerepostoragepublic.s3.amazonaws.com/";
    final AmazonS3 s3;

    public S3() {
        s3 = AmazonS3ClientBuilder.standard().withCredentials(new EnvironmentVariableCredentialsProvider()).withRegion(Regions.US_EAST_1).build();
    }

    public String uploadFile(File file) {
        try {
            s3.putObject(bucket_name, file.getName(), file);
            return object_url + file.getName();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            throw e;
        }
    }

    public S3Object getImage(String name) {
        try {
            return s3.getObject(bucket_name, name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            throw e;
        }
    }
    public String getImageString(String name) {
        try {
            return s3.getObjectAsString(bucket_name, name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            throw e;
        }
    }


    public void deleteFile(String filename) {
        try {
            s3.deleteObject(bucket_name, filename);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            throw e;
        }
    }


}

package com.application.imagerepo.utils;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import ij.IJ;
import ij.ImagePlus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class ImageUtils {

    public static String getEncodedContent(S3Object image) throws IOException {
        BufferedImage image1 = null;
        InputStream imageInputStream = image.getObjectContent();
        try {
            image1 = ImageIO.read(imageInputStream);
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            imageInputStream.close();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image1, "png", baos);
        } catch (IOException e) {
            //TODO: Add Exception
            e.printStackTrace();
        }
        byte[] res = baos.toByteArray();
        baos.close();
        return new String(Base64.getEncoder().encode(res), StandardCharsets.UTF_8);
    }

    public static String formatTitle(String title, String username) {
        return String.join("_", Arrays.asList(title, username));
    }

}

package com.application.imagerepo.controller;

import com.application.imagerepo.image.Image;
import com.application.imagerepo.image.ImageAccessType;
import com.application.imagerepo.image.ImageService;
import com.application.imagerepo.transferObjects.SignupRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

//    @BeforeAll
//    void setupAuth(){
//
//        SignupRequest signupRequest = new SignupRequest.SignUpRequestBuilder()
//                .withEmail("test@gmail.com")
//                .withPassword("test")
//                .withUsername("test").build();
//
//
//    }

//    @Test
//    void givenNoJWTTokenWhenGetImagesShouldReturnAllPublicImages() throws Exception {
//        List<Image> imageList = new ArrayList<Image>();
//        imageList.add(new Image("First Image", "First Image", ImageAccessType.PUBLIC.name()));
//        imageList.add(new Image("Second Image", "Second Image", ImageAccessType.PRIVATE.name()));
//        imageList.add(new Image("Third Image", "Third Image", ImageAccessType.PUBLIC.name()));
//
//        when(imageService.getPublicImages()).thenReturn(imageList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/image").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
//    }


}

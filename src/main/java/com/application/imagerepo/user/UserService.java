package com.application.imagerepo.user;

import com.application.imagerepo.security.jwt.JWTTokenUtil;
import com.application.imagerepo.transferObjects.ImageTO;
import com.application.imagerepo.transferObjects.UserTO;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTTokenUtil jwtTokenUtil;

    public UserTO addUser(User user) {
        user.setRole(ROLES.USER.name());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return modelMapper.map(userRepository.save(user), UserTO.class);
    }

    public UserTO authenticate(UserTO user) throws InvalidKeyException, NotFoundException {
        Optional<User> userFromDB = userRepository.findById(user.getId());
        if (userFromDB.isPresent()) {
            if (userFromDB.get().getPassword().equals(user.getPassword())) {
                return user;
            } else {
                throw new InvalidKeyException("Invalid Credentials");
            }
        } else {
            throw new NotFoundException("User not found!");
        }

    }

    public List<ImageTO> getUserImages(Long id, String authorizationHeader) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new NotFoundException("User not found!");
        String token = authorizationHeader.split(" ")[1].trim();
        if (!jwtTokenUtil.isAuthorized(token, modelMapper.map(user.get(), UserTO.class)))
        throw new BadCredentialsException("Not Authorized");

        return modelMapper.map(user.get(), UserTO.class).getImages();

    }

}

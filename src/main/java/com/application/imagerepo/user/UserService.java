package com.application.imagerepo.user;

import com.application.imagerepo.transferObjects.UserTO;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

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

}

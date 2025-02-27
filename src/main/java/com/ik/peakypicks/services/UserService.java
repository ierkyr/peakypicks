package com.ik.peakypicks.services;

import com.ik.peakypicks.model.User;
import com.ik.peakypicks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public User registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)){
            throw new RuntimeException("Username already taken");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);
        return userRepository.save(user);
    }

    public boolean authenticateUser (String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user!=null) {
            return passwordEncoder.matches(password, user.get().getPasswordHash());
        }
        return false;
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

}

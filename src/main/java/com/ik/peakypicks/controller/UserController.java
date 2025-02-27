package com.ik.peakypicks.controller;

import com.ik.peakypicks.dto.UserDTO;
import com.ik.peakypicks.model.User;
import com.ik.peakypicks.security.JwtResponse;
import com.ik.peakypicks.security.JwtUtil;
import com.ik.peakypicks.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.registerUser(userDTO.getUsername(), userDTO.getPassword());
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok().body(new JwtResponse("User registered successfully", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        boolean authenticated = userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
        if (authenticated) {
            User user = userService.getUserByUsername(userDTO.getUsername());
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse("Login successful", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials. Please try again");
        }
    }
}

package com.ik.peakypicks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ik.peakypicks.dto.PickDTO;
import com.ik.peakypicks.model.Pick;
import com.ik.peakypicks.security.JwtUtil;
import com.ik.peakypicks.services.GoogleApiService;
import com.ik.peakypicks.services.PickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class PickController {

    private final PickService pickService;
    private final GoogleApiService googleApiService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PickController(PickService pickService, GoogleApiService googleApiService, JwtUtil jwtUtil) {
        this.pickService = pickService;
        this.googleApiService = googleApiService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("search/{textQuery}")
    public ResponseEntity<?> getPicks(@PathVariable String textQuery) {
        try {
            List<Pick> picks = googleApiService.getPicks(textQuery);
            return ResponseEntity.ok(picks);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePick(@RequestHeader("Authorization") String token, @RequestBody PickDTO pickDTO) {
        try {
            String username = extractUsernameFromToken(token);

            String serviceResponse = pickService.savePick(pickDTO, username);

            if (serviceResponse.equals("Pick already saved for this user!")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(serviceResponse);
            }
            return ResponseEntity.ok(serviceResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("user")
    public ResponseEntity<?> getUserPicks(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            List<Pick> userPicks = pickService.getUserPicks(username);
            return ResponseEntity.ok(userPicks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deletePick(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> requestBody) {
        String googleId = requestBody.get("googleId");

        if (googleId == null || googleId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Google ID is required");
        }

        try {
            String username = extractUsernameFromToken(token);
            pickService.deletePick(googleId, username);
            return ResponseEntity.ok("Pick deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }


    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header.");
        }
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return username;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token.", e);
        }
    }

}

package com.ik.peakypicks.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    // Extract the username (subject) from the JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract any claim from the JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate a JWT token from the username
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return createToken(claims, username);
    }

    // Generate the token using claims and username
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)  // set the username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))  // set expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // sign the token
                .compact();
    }

    // validate if the JWT token is valid
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);  // extract username from the token
        return (extractedUsername.equals(username)) && !isTokenExpired(token);  // validate token with username and expiration
    }

    // check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Compare expiration date with current time
    }

    // extract the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extract expiration claim
    }

    // extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())  // Set the signing key to verify the token
                .build()
                .parseClaimsJws(token)  // Parse the token
                .getBody();  // Get the claims from the token body
    }

    // get the signing key from the secret key (base64 encoded)
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Decode the base64 secret key
        return Keys.hmacShaKeyFor(keyBytes);  // Return a HMAC signing key
    }
}

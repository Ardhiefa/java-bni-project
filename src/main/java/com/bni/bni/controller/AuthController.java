package com.bni.bni.controller;

import com.bni.bni.entity.User;
import com.bni.bni.service.AuthService;
import com.bni.bni.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = body.get("username");
            String password = body.get("password");
            String email = body.getOrDefault("email", null);

            String message = authService.register(username, password, email);

            response.put("status", message.equals("Registered successfully") ? 200 : 409);
            response.put("message", message);

            return ResponseEntity.status((int) response.get("status")).body(response);
        } catch (IllegalArgumentException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        String token = authService.login(username, password);

        Map<String, Object> response = new HashMap<>();
        if (token != null) {
            Optional<User> userOpt = authService.getUserByUsername(username);
            User user = userOpt.get();

            response.put("status", 200);
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("message", "Login Berhasil");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 401);
            response.put("message", "Invalid credentials or inactive account");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(400).body(response);
        }

        try {
            String token = authHeader.replace("Bearer ", "").trim();

            if (!jwtUtil.validateToken(token)) {
                response.put("status", 401);
                response.put("message", "Token tidak valid atau expired");
                return ResponseEntity.status(401).body(response);
            }

            Claims claims = jwtUtil.getAllClaimsFromToken(token);

            response.put("status", 200);
            response.put("username", claims.getSubject());
            response.put("role", claims.get("role"));
            response.put("issuedAt", claims.getIssuedAt());
            response.put("expiration", claims.getExpiration());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 401);
            response.put("message", "Token tidak valid");
            return ResponseEntity.status(401).body(response);
        }
    }
}
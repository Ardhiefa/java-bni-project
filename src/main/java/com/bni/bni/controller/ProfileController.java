package com.bni.bni.controller;

import com.bni.bni.entity.Profile;
import com.bni.bni.service.ProfileService;
import com.bni.bni.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProfile(@RequestHeader(value = "Authorization") String authHeader,
                                                                 @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(400).body(response);
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            response.put("status", 401);
            response.put("message", "Token tidak valid atau expired");
            return ResponseEntity.status(401).body(response);
        }

        Long userId;
        try {
            userId = Long.valueOf(body.get("userId"));
        } catch (NumberFormatException e) {
            response.put("status", 400);
            response.put("message", "Invalid userId format.");
            return ResponseEntity.status(400).body(response);
        }

        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String placeOfBirth = body.get("placeOfBirth");
        LocalDate dateOfBirth = null;
        if (body.containsKey("dateOfBirth") && body.get("dateOfBirth") != null) {
            dateOfBirth = LocalDate.parse(body.get("dateOfBirth"));
        }


        if (userId == null || firstName == null) {
            response.put("status", 400);
            response.put("message", "userId and firstName are required.");
            return ResponseEntity.status(400).body(response);
        }

        String message = profileService.createProfile(userId, firstName, lastName, placeOfBirth, dateOfBirth);

        if (message.equals("Profile created successfully")) {
            response.put("status", 200);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 409);
            response.put("message", message);
            return ResponseEntity.status(409).body(response);
        }
    }

    @GetMapping("/detail/{profileId}") // Mengubah path variable menjadi profileId
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader(value = "Authorization") String authHeader,
                                                          @PathVariable Long profileId) { // Mengubah parameter menjadi profileId
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(400).body(response);
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            response.put("status", 401);
            response.put("message", "Token tidak valid atau expired");
            return ResponseEntity.status(401).body(response);
        }

        Optional<Profile> profile = profileService.getProfileById(profileId); // Memanggil metode baru
        if (profile.isPresent()) {
            response.put("status", 200);
            response.put("profile", profile.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 404);
            response.put("message", "Profile not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestHeader(value = "Authorization") String authHeader,
                                                                 @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", 400);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(400).body(response);
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            response.put("status", 401);
            response.put("message", "Token tidak valid atau expired");
            return ResponseEntity.status(401).body(response);
        }

        Long userId;
        try {
            userId = Long.valueOf(body.get("userId"));
        } catch (NumberFormatException e) {
            response.put("status", 400);
            response.put("message", "Invalid userId format.");
            return ResponseEntity.status(400).body(response);
        }

        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String placeOfBirth = body.get("placeOfBirth");
        LocalDate dateOfBirth = null;
        if (body.containsKey("dateOfBirth") && body.get("dateOfBirth") != null) {
            dateOfBirth = LocalDate.parse(body.get("dateOfBirth"));
        }

        if (userId == null) {
            response.put("status", 400);
            response.put("message", "userId is required.");
            return ResponseEntity.status(400).body(response);
        }

        String message = profileService.updateProfile(userId, firstName, lastName, placeOfBirth, dateOfBirth);

        if (message.equals("Profile updated successfully")) {
            response.put("status", 200);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 404);
            response.put("message", message);
            return ResponseEntity.status(404).body(response);
        }
    }
}
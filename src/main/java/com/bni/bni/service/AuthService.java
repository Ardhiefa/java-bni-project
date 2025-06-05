package com.bni.bni.service;

import com.bni.bni.entity.User;
import com.bni.bni.repository.UserRepository;
import com.bni.bni.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

  public String register(String username, String password, String emailAddress) { 
        if (repo.existsByUsername(username)) {
            return "User already exists";
        }
        if (repo.existsByEmailAddress(emailAddress)) { 
            return "Email address already exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password)); 
        user.setCreatedAt(OffsetDateTime.now());
        user.setEmailAddress(emailAddress); 
        user.setIsActive(true); 
        user.setUpdatedAt(OffsetDateTime.now()); 
        repo.save(user);

        return "Registered successfully";
    }

    public String login(String username, String password) {
        Optional<User> user = repo.findByUsername(username);
        if (user.isPresent() && encoder.matches(password, user.get().getPassword())) {
            if (!user.get().getIsActive()) {
                return "User is not active";
            }
            return jwtUtil.generateToken(username, "USER"); 
        }

        return null;
    }
}
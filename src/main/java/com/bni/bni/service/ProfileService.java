package com.bni.bni.service;

import com.bni.bni.entity.Profile;
import com.bni.bni.entity.User;
import com.bni.bni.repository.ProfileRepository;
import com.bni.bni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public String createProfile(Long userId, String firstName, String lastName, String placeOfBirth, LocalDate dateOfBirth) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "User not found";
        }

        // Menggunakan existsByUser_Id
        if (profileRepository.existsByUser_Id(userId)) {
            return "Profile for this user already exists";
        }

        User user = userOptional.get();
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPlaceOfBirth(placeOfBirth);
        profile.setDateOfBirth(dateOfBirth);
        profile.setCreatedAt(OffsetDateTime.now());
        profile.setUpdatedAt(OffsetDateTime.now());

        profileRepository.save(profile);
        return "Profile created successfully";
    }

    public Optional<Profile> getProfileById(Long profileId) {
        return profileRepository.findById(profileId);
    }

    public String updateProfile(Long userId, String firstName, String lastName, String placeOfBirth, LocalDate dateOfBirth) {
        // Menggunakan findByUser_Id
        Optional<Profile> profileOptional = profileRepository.findByUser_Id(userId);
        if (profileOptional.isEmpty()) {
            return "Profile not found for this user";
        }

        Profile profile = profileOptional.get();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPlaceOfBirth(placeOfBirth);
        profile.setDateOfBirth(dateOfBirth);
        profile.setUpdatedAt(OffsetDateTime.now());

        profileRepository.save(profile);
        return "Profile updated successfully";
    }
}
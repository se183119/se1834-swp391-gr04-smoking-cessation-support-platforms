package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.SmokingProfile;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.SmokingProfileRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional
public class SmokingProfileService {

    @Autowired
    private SmokingProfileRepository smokingProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public SmokingProfile createOrUpdateProfile(String email, Map<String, Object> profileData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Get existing active profile or create new one
        SmokingProfile profile = smokingProfileRepository.findByUserAndIsActiveTrue(user)
            .orElse(new SmokingProfile());

        profile.setUser(user);

        // Update profile data
        if (profileData.containsKey("cigarettesPerDay")) {
            profile.setCigarettesPerDay((Integer) profileData.get("cigarettesPerDay"));
        }
        if (profileData.containsKey("brand")) {
            profile.setBrand((String) profileData.get("brand"));
        }
        if (profileData.containsKey("pricePerPack")) {
            profile.setPricePerPack(new BigDecimal(profileData.get("pricePerPack").toString()));
        }
        if (profileData.containsKey("cigarettesPerPack")) {
            profile.setCigarettesPerPack((Integer) profileData.get("cigarettesPerPack"));
        }
        if (profileData.containsKey("yearsOfSmoking")) {
            profile.setYearsOfSmoking((Integer) profileData.get("yearsOfSmoking"));
        }
        if (profileData.containsKey("previousQuitAttempts")) {
            profile.setPreviousQuitAttempts((Integer) profileData.get("previousQuitAttempts"));
        }
        if (profileData.containsKey("smokingTriggers")) {
            profile.setSmokingTriggers((String) profileData.get("smokingTriggers"));
        }
        if (profileData.containsKey("healthConcerns")) {
            profile.setHealthConcerns((String) profileData.get("healthConcerns"));
        }
        if (profileData.containsKey("motivations")) {
            profile.setMotivations((String) profileData.get("motivations"));
        }
        if (profileData.containsKey("currentWeight")) {
            profile.setCurrentWeight((Double) profileData.get("currentWeight"));
        }
        if (profileData.containsKey("bloodPressure")) {
            profile.setBloodPressure((String) profileData.get("bloodPressure"));
        }
        if (profileData.containsKey("stressLevel")) {
            profile.setStressLevel((Integer) profileData.get("stressLevel"));
        }

        profile.setIsActive(true);

        return smokingProfileRepository.save(profile);
    }

    public SmokingProfile getCurrentProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return smokingProfileRepository.findByUserAndIsActiveTrue(user)
            .orElseThrow(() -> new RuntimeException("No active smoking profile found"));
    }

    public SmokingProfile getActiveProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return smokingProfileRepository.findByUserAndIsActiveTrue(user)
            .orElse(null); // Return null if no active profile
    }

    public void deleteProfile(String email, Long profileId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        SmokingProfile profile = smokingProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (!profile.getUser().equals(user)) {
            throw new RuntimeException("Access denied: This profile belongs to another user");
        }

        smokingProfileRepository.delete(profile);
    }
} 
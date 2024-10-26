package com.hldprojects.tinyurlservice.keymanager.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@Getter
@Setter
public class Base64GenerationStrategy implements IkeyGenerationStrategy{

    public static final int KEY_LENGTH = 20;// Adjust the length as needed

    @Override
    public String generateKey() {
        // Generate random bytes
        byte[] randomBytes = new byte[KEY_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        // Encode to Base64
        return Base64.getEncoder().encodeToString(randomBytes);
    }

}

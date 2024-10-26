package com.hldprojects.tinyurlservice.keymanager.service;

import com.hldprojects.tinyurlservice.keymanager.model.IkeyGenerationStrategy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Getter
@Setter
public class KeyGeneratorService {

    @Autowired
    private List<IkeyGenerationStrategy> keyGenerationStrategies; // Inject all strategies

    @Value("${keygenerator.strategy}")
    private String selectedStrategy;

    @Value("${keygenerator.keylength}")
    private Integer keyLength;



    public Set<String> generateKeys(Integer totalKeysToGenerate){
        IkeyGenerationStrategy ikeyGenerationStrategy = keyGenerationStrategies.stream()
                .filter(strategy -> strategy.getClass().getSimpleName().equalsIgnoreCase(selectedStrategy))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for: " + selectedStrategy));

        Set<String> keysSet = new HashSet<>();
        for(int i = 0; i < totalKeysToGenerate; i += 1){
            String generatedKey = ikeyGenerationStrategy.generateKey();
            partitionKeyAndAddToSet(generatedKey, keysSet);
        }

        return keysSet;
    }

    private void partitionKeyAndAddToSet(String generatedKey, Set<String> keysSet) {
        int totalParts = generatedKey.length() / keyLength; //divide generated key into parts where each part is keyLength size

        for(int i = 0; i < totalParts; i += 1){
            int start = i * keyLength;
            String shortKey = generatedKey.substring(start, start + keyLength + 1);
            keysSet.add(shortKey);
        }
    }
}

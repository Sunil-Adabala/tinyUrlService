package com.hldprojects.tinyurlservice.keymanager.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Getter
@Setter
public class KeyManagementService {

    private final KeyGeneratorService keyGeneratorService;
    private final KeyStorageService keyStorageService;

    @Value("${keygenerator.generateKeys}")
    private boolean generateKeys;

    @Value("${keygenerator.generate}")
    private Integer totalKeysToGenerate;

    @Autowired
    public KeyManagementService(KeyGeneratorService keyGeneratorService, KeyStorageService keyStorageService) {
        this.keyGeneratorService = keyGeneratorService;
        this.keyStorageService = keyStorageService;
    }

    @PostConstruct
    public void generateAndStoreKey() {
        if(isGenerateKeys()){
            int batchSize = 1000;
            int totalIterations = totalKeysToGenerate / batchSize;
            int remainder  = totalKeysToGenerate % batchSize;
            for(int i = 0; i < totalIterations; i += 1){
                //get keys in set of size batchSize
                Set<String> generatedKeySet = keyGeneratorService.generateKeys(batchSize);
                keyStorageService.saveKey(generatedKeySet);
            }
            if(remainder  > 0){
                Set<String> generatedKeySet = keyGeneratorService.generateKeys(remainder);
                keyStorageService.saveKey(generatedKeySet);
            }

        }
    }

    public String getUniqueKey() {
        return keyStorageService.getUniqueKey();
    }
}

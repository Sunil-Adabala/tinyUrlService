package com.hldprojects.tinyurlservice.keymanager.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Getter
@Setter
public class KeyManagementService {
    Logger logger = LoggerFactory.getLogger(KeyManagementService.class);

    private final KeyGeneratorService keyGeneratorService;
    private final KeyStorageService keyStorageService;

    @Value("${keygenerator.generateKeys}")
    private boolean generateKeys;

    @Value("${keygenerator.generate}")
    private Integer totalKeysToGenerate;
    @Value("${initialkeycountinmemory}")
    private Integer initialKeyCountInMemory;

    private int batchSize = 1000;

    @Autowired
    public KeyManagementService(KeyGeneratorService keyGeneratorService, KeyStorageService keyStorageService) {
        this.keyGeneratorService = keyGeneratorService;
        this.keyStorageService = keyStorageService;
    }

    public void generateAndStoreKey() {
        if(isGenerateKeys()){
            logger.info("Initial Key Generation has started, keys will be generated and Saved in DB");
            int totalIterations = totalKeysToGenerate / batchSize;
            int remainder  = totalKeysToGenerate % batchSize;
            for(int i = 0; i < totalIterations; i += 1){
                //get keys in set of size batchSize
                Set<String> generatedKeyList = keyGeneratorService.generateKeys(batchSize);
                keyStorageService.saveKey(generatedKeyList);
            }
            if(remainder > 0){
                System.out.println("REMAINDER EXISTS ->"+remainder);
                Set<String> generatedKeyList = keyGeneratorService.generateKeys(remainder);
                keyStorageService.saveKey(generatedKeyList);
            }
        }
    }

    public String getUniqueKey() {
        return keyStorageService.getUniqueKey();
    }

    public void loadKeysIntoMemory(){
        Long startTime = System.currentTimeMillis();
        int availableKeysInDb = keyStorageService.getAvailableUnusedKeys();
        while (availableKeysInDb < (5 * initialKeyCountInMemory)){ //if db has less than 5x of initial limit, then generate new keys
            logger.debug("Total keys in Db is -> "+availableKeysInDb+" its less than 5 times of initial load limit ( "+ initialKeyCountInMemory +" ) so generating new keys and inserting into");
            Set<String> generatedKeysList = keyGeneratorService.generateKeys(batchSize);
            keyStorageService.saveKey(generatedKeysList);
            availableKeysInDb = keyStorageService.getAvailableUnusedKeys();
        }
        logger.info("Total keys in Db is -> "+availableKeysInDb);
        keyStorageService.loadKeysIntoMemory();
        Long endTime = System.currentTimeMillis();
        logger.info("Total time taken to load initial keys in memory -> "+((endTime - startTime)/1000)+" second(s)");

    }
}

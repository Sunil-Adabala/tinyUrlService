package com.hldprojects.tinyurlservice.keymanager.service;

import com.hldprojects.tinyurlservice.keymanager.dao.KeyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeyStorageService {
    Logger logger = LoggerFactory.getLogger(KeyStorageService.class);


    KeyDao keyDao;

    Set<String> inMemoryKeysSet;



    @Autowired
    public KeyStorageService(KeyDao keyDao){
        inMemoryKeysSet = new HashSet<>();
        this.keyDao = keyDao;
    }

    //load keys into memory during application startup
    public void loadKeysIntoMemory(){
        keyDao.processKeys(inMemoryKeysSet);
        logger.info("Loaded a Total of -> "+ inMemoryKeysSet.size()+" into memory");
    }

    public Integer getAvailableUnusedKeys(){
        return keyDao.getAvailableUnusedKeys();
    }

    public void saveKey(Set<String> generatedKeysSet) {
        keyDao.saveKeys(generatedKeysSet);
    }

    public String getUniqueKey() {
        /*
        TODO -
        1)If the count goes under minimum permissible count then start a new thread and load keys into memory
         */

        // Using an iterator to get a single value
        Iterator<String> iterator = inMemoryKeysSet.iterator();
        if (iterator.hasNext()) {
            String uniqueKey = iterator.next();
            inMemoryKeysSet.remove(uniqueKey);
            logger.info("Returning unique Key->"+uniqueKey+" this is also deleted from memory");
            return uniqueKey;
        }else{
            return null;
        }
    }
}

package com.hldprojects.tinyurlservice.keymanager.service;

import com.hldprojects.tinyurlservice.keymanager.dao.KeyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KeyStorageService {

    @Autowired
    KeyDao keyDao;

    public void saveKey(Set<String> generatedKeySet) {
        keyDao.saveKeys(generatedKeySet);

    }

    public String getUniqueKey() {
        return keyDao.getKey();
    }
}

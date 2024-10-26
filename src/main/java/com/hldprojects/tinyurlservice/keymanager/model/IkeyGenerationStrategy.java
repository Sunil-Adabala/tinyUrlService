package com.hldprojects.tinyurlservice.keymanager.model;

import org.springframework.beans.factory.annotation.Value;

public interface IkeyGenerationStrategy {
    String generateKey();

//    Integer getTotalKeysToGenerate();
//
//    boolean isGenerateKeys();

}

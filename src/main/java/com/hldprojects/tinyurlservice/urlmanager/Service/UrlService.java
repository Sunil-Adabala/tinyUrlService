package com.hldprojects.tinyurlservice.urlmanager.Service;

import com.hldprojects.tinyurlservice.keymanager.service.KeyManagementService;
import com.hldprojects.tinyurlservice.urlmanager.Model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    @Autowired
    private Url url;

    @Autowired
    private KeyManagementService keyManagementService;

    public String getTinyUrl(String actualUrl){
        return keyManagementService.getUniqueKey();
    }
}

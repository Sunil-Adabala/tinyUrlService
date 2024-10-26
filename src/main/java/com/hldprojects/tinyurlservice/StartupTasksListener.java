package com.hldprojects.tinyurlservice;

import com.hldprojects.tinyurlservice.keymanager.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTasksListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private KeyManagementService keyManagementService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        keyManagementService.generateAndStoreKey();
        keyManagementService.loadKeysIntoMemory();
    }

}

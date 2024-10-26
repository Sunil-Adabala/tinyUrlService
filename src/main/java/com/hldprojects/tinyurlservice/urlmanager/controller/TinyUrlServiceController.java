package com.hldprojects.tinyurlservice.urlmanager.controller;

import com.hldprojects.tinyurlservice.urlmanager.Model.Url;
import com.hldprojects.tinyurlservice.urlmanager.Model.UrlResponse;
import com.hldprojects.tinyurlservice.urlmanager.Service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TinyUrlServiceController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlResponse> getTinyUrl(@RequestBody Url url){
        String urlFromUser = url.getUrl();

        String tinyUrl = urlService.getTinyUrl(urlFromUser);
        if(tinyUrl != null){
            System.out.println(tinyUrl+" returned this Tiny Url");
            return ResponseEntity.ok(new UrlResponse(tinyUrl)); // 200 OK response
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UrlResponse("Internal Server Error, unable to get unique Url"));
    }
}

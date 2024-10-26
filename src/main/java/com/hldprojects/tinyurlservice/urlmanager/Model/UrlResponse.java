package com.hldprojects.tinyurlservice.urlmanager.Model;

import lombok.Getter;
import lombok.Setter;

public class UrlResponse {
    @Getter
    @Setter
    private String tinyUrl;

    public UrlResponse(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }
}

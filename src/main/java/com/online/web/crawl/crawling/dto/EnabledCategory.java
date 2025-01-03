package com.online.web.crawl.crawling.dto;


import lombok.Getter;

@Getter
public class EnabledCategory {

    private EnabledCategory() {

    }

    public EnabledCategory(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    private String key;
    private String displayName;
}

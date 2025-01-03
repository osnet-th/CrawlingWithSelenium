package com.online.web.crawl.crawling.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsedItem {

    private UsedItem () {

    }

    public UsedItem(String title, String url, String price, String uploadTime) {
        this.title = title;
        this.url = url;
        this.price = price;
        this.uploadTime = uploadTime;
    }

    private String title;
    private String url;
    private String price;
    private String uploadTime;
}

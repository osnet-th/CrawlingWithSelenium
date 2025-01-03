package com.online.web.crawl.crawling.service;

import com.online.web.crawl.crawling.dto.EnabledCategory;
import com.online.web.crawl.crawling.dto.UsedItem;

import java.util.List;

public interface CrawlingService {
    public List<UsedItem> requestCrawlingData(String keyword);

    public EnabledCategory getEnableCrawlingCategory();

}

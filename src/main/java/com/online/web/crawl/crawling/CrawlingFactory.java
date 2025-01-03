package com.online.web.crawl.crawling;


import com.online.web.crawl.crawling.service.CrawlingService;
import com.online.web.crawl.crawling.dto.EnabledCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CrawlingFactory {

    private final Map<String, CrawlingService> crawlingServiceMap;

    @Autowired
    public CrawlingFactory(Map<String, CrawlingService> crawlingServiceMap) {
        this.crawlingServiceMap = crawlingServiceMap;
    }

    public CrawlingService getCategoryService(String key) {
        return this.crawlingServiceMap.get(toLowerFirstChar(key));
    }

    public List<EnabledCategory> getEnabledCategory() {
        return crawlingServiceMap.keySet().stream().map(key -> crawlingServiceMap.get(key).getEnableCrawlingCategory()).toList();
    }

    private static String toLowerFirstChar(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }
}

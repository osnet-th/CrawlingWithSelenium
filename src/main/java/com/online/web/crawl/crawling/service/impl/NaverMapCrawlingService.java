package com.online.web.crawl.crawling.service.impl;

import com.online.web.crawl.crawling.dto.EnabledCategory;
import com.online.web.crawl.crawling.dto.UsedItem;
import com.online.web.crawl.crawling.service.CrawlingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class NaverMapCrawlingService implements CrawlingService {
    @Override
    public List<UsedItem> requestCrawlingData(String keyword) {
        log.info("아직 미구현된 기능입니다.");
        return new ArrayList<>();
    }

    @Override
    public EnabledCategory getEnableCrawlingCategory() {
        return new EnabledCategory(NaverMapCrawlingService.class.getSimpleName(),"네이버 지도");
    }

}

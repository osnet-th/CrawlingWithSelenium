package com.online.web.crawl.crawling.controller;


import com.online.web.crawl.crawling.dto.UsedItem;
import com.online.web.crawl.crawling.CrawlingFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingFactory crawlingFactory;


    @GetMapping("")
    public String defaultPath() {
        return "redirect:/crawling-data";
    }

    @GetMapping("/crawling-data")
    public String crawlingHome(@RequestParam(defaultValue = "", required = false) String key,
                               @RequestParam(defaultValue = "", required = false) String keyword,
                               Model model) {
        List<UsedItem> items = new ArrayList<>();
        if(!key.isEmpty() && !keyword.isEmpty()) {
             items = crawlingFactory.getCategoryService(key).requestCrawlingData(keyword);
        }

        model.addAttribute("categories", crawlingFactory.getEnabledCategory());
        model.addAttribute("items", items);
        model.addAttribute("size", items.size());

        return "default_crawling_layout";
    }
}

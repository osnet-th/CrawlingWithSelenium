package com.online.web.crawl.crawling;

public class JoongnaCSSValueContainer {

    private final String searchBoxCss = "search-box";

    private final String currentPageCss = "[class='w-10 h-10 rounded-md shrink-0 bg-jngreen/80 text-white']";
    private final String pageAnchorCss = ".block.leading-10";
    private final String priceContainerCss = ".w-full.overflow-hidden.p-2";
    private final String itemPriceTagCss = ".font-semibold";
    private final String itemPostCss = "//img[contains(@alt, '{keyword}')]/ancestor::a";
    private final String itemUploadTextCss = "span.text-sm.text-gray-400";


    public String getSearchBoxCss() {
        return searchBoxCss;
    }

    public String getCurrentPageCss() {
        return currentPageCss;
    }

    public String getPageAnchorCss() {
        return pageAnchorCss;
    }

    public String getPriceContainerCss() {
        return priceContainerCss;
    }

    public String getItemPriceTagCss() {
        return itemPriceTagCss;
    }

    public String getItemPostCssWithReplaceKeyword(String keyword) {
        return itemPostCss.replace("{keyword}", keyword);
    }

    public String getItemUploadTextCss() {
        return itemUploadTextCss;
    }
}

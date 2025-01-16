package com.online.web.crawl.crawling.service.impl;


import com.online.web.crawl.crawling.JoongnaCSSValueContainer;
import com.online.web.crawl.crawling.config.SeleniumDriverConfig;
import com.online.web.crawl.crawling.dto.EnabledCategory;
import com.online.web.crawl.crawling.dto.UsedItem;
import com.online.web.crawl.crawling.service.CrawlingService;
import com.online.web.crawl.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoongnaCrawlingService implements CrawlingService, ScheduleService {
    private WebDriver driver;
    private WebDriverWait waitDrvier;
    private String keyword;

    private final SeleniumDriverConfig driverConfig;
    private final String jongnaUrl = "https://web.joongna.com";
    private final AtomicInteger pageCount = new AtomicInteger(1);
    private final JoongnaCSSValueContainer cssValueContainer = new JoongnaCSSValueContainer();
    private final Map<Integer, List<UsedItem>> pageItems = new HashMap<>();
    private final int MAX_PAGE = 0;

    @Override
    public EnabledCategory getEnableCrawlingCategory() {
        return new EnabledCategory(JoongnaCrawlingService.class.getSimpleName(), "중고 나라");
    }


    @Override
    public List<UsedItem> requestCrawlingData(String keyword) {
        log.info("크롤링을 진행합니다. 요청 URL (https://web.joongna.com) ");
        try {
            setUpKeyword(keyword);
            crawlingProcess();
        } catch (Exception e) {
            return error(e);
        } finally {
            pageCount.set(1);
            driver.quit();
            return pageItems.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

    private void setUpKeyword(String keyword) {
        this.keyword = keyword;
    }

    private List<UsedItem> error(Exception e) {

        log.error("크롤링 진행 중 에러가 발생했습니다. {}", e.getMessage());
        return new ArrayList<>();
    }

    private void crawlingProcess() throws IOException {
        setUpCrawlingPage();
        searchKeywordAndMovePage();
        getTotalPageCrawling();
    }
    private void setUpCrawlingPage() throws IOException {
        initDriver();
        connectionWebUrl();
    }

    private void initDriver() throws IOException {
        driver = driverConfig.getChromeDriver();
    }

    private void connectionWebUrl() {
        // Google 검색 페이지로 이동
        driver.get(jongnaUrl);
    }

    private void searchKeywordAndMovePage() {
        // 검색 입력 상자 찾기
        WebElement searchBox = driver.findElement(By.id(cssValueContainer.getSearchBoxCss()));
        // 검색어 입력
        searchBox.sendKeys(keyword);
        // 검색 버튼 클릭
        searchBox.submit();
    }

    private void getTotalPageCrawling() {

        do {
            try {
            pageItems.put(pageCount.get(), crawling());
            incrementPageCount();
            WebElement nextPageAnchor = findAndMakeNextPageAnchorTag();
            nextPageClick(nextPageAnchor);
            } catch(NoSuchElementException e) {
                log.info("다음 페이지가 없습니다. 크롤링을 종료합니다.");
                break;
            } catch(StaleElementReferenceException e) {
                log.info("실행중 에러 - 재시도 합니다. 현재 페이지 {}", pageCount);
            }
        } while (isWhileCondition());

    }

    private boolean isWhileCondition() {
        return isMaxPageZero() ? true : isPageCountEqualMaxPage();
    }

    private boolean isPageCountEqualMaxPage() {
        return pageCount.get() <= MAX_PAGE;
    }

    private boolean isMaxPageZero() {
        return MAX_PAGE == 0;
    }

    private List<UsedItem> crawling() {
        createWebDriverWait();
        List<WebElement> elements = crawlingAndWaitForLoadingComplete();
        return createCrawlingData(elements);
    }

    private List<WebElement> crawlingAndWaitForLoadingComplete() {
        return waitDrvier.until(d -> waitForLoadingElement(d.findElements(By.xpath(cssValueContainer.getItemPostCssWithReplaceKeyword(keyword)))));
    }

    private List<WebElement> waitForLoadingElement(List<WebElement> itemPostElements) {
        try {
            // 모든 요소가 화면에 표시될 때까지 기다립니다.
            return (!itemPostElements.isEmpty() && itemPostElements.stream().allMatch(WebElement::isDisplayed)) ? itemPostElements : null;
        } catch (StaleElementReferenceException e) {
            return crawlingAndWaitForLoadingComplete();
        }
    }

    private void nextPageClick(WebElement nextPageAnchor) {
        log.info(">>> 다음 페이지 크롤링 - {} 페이지", nextPageAnchor.getText());
        nextPageAnchor.click(); // 화면이 작은 경우 Click 이 안되는 경우가 있음
    }


    private List<UsedItem> createCrawlingData(List<WebElement> elements) {
        if (isEmptyElements(elements))
            return new ArrayList<>();
        return consolidateCrawlingData(elements);
    }


    private boolean isEmptyElements(List<WebElement> elements) {
        return Objects.isNull(elements) || elements.isEmpty();
    }
    private List<UsedItem> consolidateCrawlingData(List<WebElement> elements) {
        return elements.stream().map(this::createJoongnaItemList).filter(Objects::nonNull).toList();
    }

    private UsedItem createJoongnaItemList(WebElement element) {
        try {
            return getItem(element);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private UsedItem getItem(WebElement element) {
        String url = getItemLink(element);
        String title = getItemTitle(element);
        String[] priceAndUploadTime = getItemPriceAndUploadTime(element);
        return new UsedItem(title, url, priceAndUploadTime[0], priceAndUploadTime[1]);
    }

    private String getItemLink(WebElement element) {
        return element.getAttribute("href");
    }

    private String getItemTitle(WebElement element) {
        WebElement contentTag = element.findElement(By.tagName("img"));
        return contentTag.getAttribute("alt");
    }

    private String[] getItemPriceAndUploadTime(WebElement element) {
        WebElement content = element.findElement(By.cssSelector(cssValueContainer.getPriceContainerCss()));
        return new String[]{getItemPrice(content), getItemUploadTime(content)};
    }

    private String getItemPrice(WebElement content) {
        return content.findElement(By.cssSelector(cssValueContainer.getItemPriceTagCss())).getText();
    }

    private String getItemUploadTime(WebElement content) {
        List<WebElement> elements = content.findElements(By.cssSelector(cssValueContainer.getItemUploadTextCss()));
        String uploadTime = elements.get(elements.size() - 1).getText();
        isAdvertisement(uploadTime);
        return uploadTime;
    }

    private void isAdvertisement(String text) {
        if (text.equals("광고")) {
            log.info("광고 게시글은 제외합니다.");
            throw new NoSuchElementException("광고 게시글은 제외합니다.");
        }
    }
    private WebElement findAndMakeNextPageAnchorTag() {
        WebElement currentAnchorTagElement = findCurrentPageAnchorTag(findCurrentPageContainer());
        String nextPageAnchorTag = replaceNextPageHref(currentAnchorTagElement.getAttribute("href")); // null 아님
        return findNextPageAnchor(nextPageAnchorTag);
    }

    private WebElement findNextPageAnchor(String nextPageAnchorTag) {
        return driver.findElement(By.cssSelector("a[href='" + nextPageAnchorTag + "']"));
    }

    private void incrementPageCount() {
        pageCount.incrementAndGet();
    }
    private void createWebDriverWait() {
        waitDrvier = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // .w-10.h-10.rounded-md.shrink-0 클래스가 붙은 요소는 페이징 요소를 의미함
    private WebElement findCurrentPageContainer() {
        return driver.findElement(By.cssSelector(cssValueContainer.getCurrentPageCss()));
    }

    // .block.leading-10 클래스를 가지는 요소는 <a> 태그
    private WebElement findCurrentPageAnchorTag(WebElement element) {
        return element.findElement(By.cssSelector(cssValueContainer.getPageAnchorCss()));
    }


    private String replaceNextPageHref(String href) {
        String regex = "=(\\d+)$";
        return href.replaceAll(regex, "=" + pageCount.get()).replace(jongnaUrl,"");
    }

    @Override
    public Runnable scheduleTask() {
        return () -> {
            log.info("중나 스케줄러 작업");
        };
    }
}

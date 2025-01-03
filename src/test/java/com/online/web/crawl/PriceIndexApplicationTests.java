package com.online.web.crawl;

import com.online.web.crawl.crawling.config.SeleniumDriverConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PriceIndexApplicationTests {

    SeleniumDriverConfig driverConfig = new SeleniumDriverConfig();
    WebDriver driver;
    WebDriverWait waitDrvier;
    AtomicInteger currentpage = new AtomicInteger(1);

    private final String jongnaUrl = "https://web.joongna.com";
    private final String searchBoxCss = "search-box";

    private final String currentPageCss = "[class='w-10 h-10 rounded-md shrink-0 bg-jngreen/80 text-white']";
    private final String anchorCss = ".block.leading-10";
    private final String keyword = "키보드";

    private final String priceContainerCss = ".w-full.overflow-hidden.p-2";
    private final String priceTagCss = ".font-semibold";
    private final String itemPostCss = "//img[contains(@alt, '" + keyword + "')]/ancestor::a";


    @BeforeEach
    public void initDriver() throws IOException {
        driver = getDriver();
    }

    @Test
    public void seleniumCrawling() throws IOException {
        setUpCrawlingPage();
        try {
            totalPageCrawling();
        } catch(NoSuchElementException e) {
            System.out.println("다음 페이지가 없습니다. 크롤링을 종료합니다.");
        } finally {
            currentpage.set(1);
            driverConfig.quit(driver);
        }
    }

    private void setUpCrawlingPage() {
        connectionWebUrl();
        searchKeyword();
    }

    private void totalPageCrawling() {
        // 처음 크롤링은 그냥 진행
        crawling();
        while(true) {
            incrementCurrentPage();
            WebElement nextPageAnchor = findAndCalcNextPageAnchorTag();
            nextPageClickAndCrawling(nextPageAnchor);
        }

    }

    private WebElement findAndCalcNextPageAnchorTag() {
        WebElement currentAnchorTagElement = getCurrentPageAnchorTag(getCurrentPageContainer());
        String nextPageAnchorTag = makeNextPageCss(currentAnchorTagElement.getAttribute("href"));
        return getNextPageAnchor(nextPageAnchorTag);
    }

    private WebElement getNextPageAnchor(String nextPageAnchorTag) {
        return driver.findElement(By.cssSelector("a[href='" + nextPageAnchorTag + "']"));
    }

    private void incrementCurrentPage() {
        currentpage.incrementAndGet();
    }

    private void nextPageClickAndCrawling(WebElement nextPageAnchor) {
        nextPageAnchor.click();
        System.out.println(">>> 다음 페이지 크롤링 - " + nextPageAnchor.getText() + "페이지");
        crawling();
    }

    private void crawling() {
        createWebDriverWait();
        List<WebElement> elements = crawlingAndWaitForLoadingComplete();
        printCrawlingData(elements);
    }


    // .w-10.h-10.rounded-md.shrink-0 클래스가 붙은 요소는 페이징 요소를 의미함
    private WebElement getCurrentPageContainer() {
        return driver.findElement(By.cssSelector(currentPageCss));
    }

    // .block.leading-10 클래스를 가지는 요소는 <a> 태그
    private WebElement getCurrentPageAnchorTag(WebElement element) {
        return element.findElement(By.cssSelector(anchorCss));
    }


    private String makeNextPageCss(String href) {
        String regex = "=(\\d+)$";
        return href.replaceAll(regex, "=" + currentpage.get()).replace(jongnaUrl,"");

    }
    private void connectionWebUrl() {
        // Google 검색 페이지로 이동
        driver.get(jongnaUrl);
    }

    private void searchKeyword() {
        // 검색 입력 상자 찾기
        WebElement searchBox = driver.findElement(By.id(searchBoxCss));
        // 검색어 입력
        searchBox.sendKeys(keyword);
        // 검색 버튼 클릭
        searchBox.submit();
    }

    // WebDriverWait 객체 생성
    private void createWebDriverWait() {
        waitDrvier = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 요소 출력
    private void printCrawlingData(List<WebElement> elements) {
        for (WebElement element : elements) {
            try {
                WebElement imgTag = element.findElement(By.tagName("img"));
                System.out.println("TITLE- " + imgTag.getAttribute("alt"));
                System.out.println("ITEM  URL- " + imgTag.getAttribute("src"));
                WebElement content = element.findElement(By.cssSelector(priceContainerCss));
                System.out.println("PRICE- " + content.findElement(By.cssSelector(priceTagCss)).getText());
            } catch (NoSuchElementException e) {
                System.out.println("해당 element 못찾음");
            }
        }
    }


    // 요소 로딩 완료까지 대기
    private List<WebElement> crawlingAndWaitForLoadingComplete() {
        return waitDrvier.until(d -> waitForLoadingElement(d.findElements(By.xpath(itemPostCss))));
    }

    private List<WebElement> waitForLoadingElement(List<WebElement> itemPostElements) {
        try {
            // 모든 요소가 화면에 표시될 때까지 기다립니다.
            return (!itemPostElements.isEmpty() && itemPostElements.stream().allMatch(WebElement::isDisplayed)) ? itemPostElements : null;
        } catch (StaleElementReferenceException e) {
            System.out.println("해당 element 새로고침 필요");
            return crawlingAndWaitForLoadingComplete();
        }
    }

    // 드라이버 생성
    private WebDriver getDriver() throws IOException {
        return driverConfig.getChromeDriver();
    }

}

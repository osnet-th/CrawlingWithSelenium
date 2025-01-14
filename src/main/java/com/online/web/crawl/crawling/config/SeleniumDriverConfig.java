package com.online.web.crawl.crawling.config;


import com.online.web.crawl.crawling.util.ComputerOs;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
public class SeleniumDriverConfig {
    public WebDriver getChromeDriver() throws IOException {
        setProperty();
        return createDriver();
    }

    private WebDriver createDriver() {
        WebDriver driver = new ChromeDriver(getChromeOptions());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }

    private void setProperty() throws IOException{
        System.setProperty("webdriver.chrome.driver", getChromeDriverExe().getFile().getAbsolutePath());
    }
    private Resource getChromeDriverExe() {
        return new ClassPathResource(new ComputerOs().getOsChromeDriverPath());
    }

    // webDriver 옵션 설정
    private ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-extensions");
        chromeOptions.addArguments("no-sandbox");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("headless");

        return chromeOptions;
    }

    // 모든 창을 닫고 WebDriver 세션을 종료합니다.
    public void quit(WebDriver driver) {
        if (!ObjectUtils.isEmpty(driver)) {
            driver.quit();
        }
    }

    // 현재 활성화된 창만 닫습니다.
    public void close(WebDriver driver) {
        if (!ObjectUtils.isEmpty(driver)) {
            driver.close();
        }
    }


}

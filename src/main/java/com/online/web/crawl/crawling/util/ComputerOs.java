package com.online.web.crawl.crawling.util;

public class ComputerOs {
    public String getOsChromeDriverPath() {
        String os = getComputerOs();
        if(isMacOs(os))
            return getMacOsChromeDriverPath();
        if(isWindowsOs(os))
            return getWindowsOsChromeDriverPath();
        if(isUbuntuOs(os))
            return getUbuntuOsChromeDriverPath();

        throw new IllegalArgumentException("지원하지 않는 OS 입니다.");
    }

    private String getComputerOs() {
        return System.getProperty("os.name").toLowerCase();
    }

    private boolean isMacOs(String os) {
        return os.contains("mac");
    }

    private boolean isWindowsOs(String os) {
        return os.contains("win");
    }

    private boolean isUbuntuOs(String os) {
        return os.contains("nix") || os.contains("nux") || os.contains("mac os x");
    }

    private String getMacOsChromeDriverPath() {
        return "driver/chromedriver";
    }
    private String getWindowsOsChromeDriverPath() {
        return "driver/chromedriver.exe";
    }
    private String getUbuntuOsChromeDriverPath() {
        return "driver/chromedriver.exe";
    }

}

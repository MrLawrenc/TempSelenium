package com.swust.utils;

import com.swust.SeleniumApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

/**
 * @author : MrLawrenc
 * date  2020/9/19 22:01
 * selenium命令解析器
 */@Slf4j
public final class SeleniumCmdParser {
    private final WebDriver webDriver;


    private SeleniumCmdParser(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public static SeleniumCmdParser newParser(WebDriver webDriver) {
        return new SeleniumCmdParser(webDriver);
    }

    public void parse(String xpath, String method, String... values) {
        if (StringUtils.isEmpty(method)) {
            throw new RuntimeException("method must not be null");
        }
        By by = By.xpath(xpath);
        if (SeleniumApp.exist(webDriver, by)) {
            WebElement element = webDriver.findElement(by);
            log.info("current exec : [{}] value : [{}]",method,values);
            switch (method) {
                case "click":
                    element.click();
                    break;
                case "sendKeys":
                    element.sendKeys(values);
                    break;
                case "sleep":
                    try {
                        TimeUnit.MILLISECONDS.sleep(Long.parseLong(values[0]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new RuntimeException("this command is temporarily not supported");
            }
        }
    }
}
package com.swust.utils;

import com.swust.SeleniumApp;
import com.swust.entity.PreCheckConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author : MrLawrenc
 * date  2020/9/19 22:01
 * selenium命令解析器
 */
@Slf4j
public final class SeleniumCmdParser {
    private final WebDriver webDriver;


    private SeleniumCmdParser(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public static SeleniumCmdParser newParser(WebDriver webDriver) {
        return new SeleniumCmdParser(webDriver);
    }

    /**
     * 配置参数解析并执行
     *
     * @param preCheckConfig 配置
     */
    public void parseExec(PreCheckConfig preCheckConfig) {
        String xpath = preCheckConfig.getLocation();
        String method = preCheckConfig.getAction();
        String[] values = preCheckConfig.getValues().split("#");


        if (StringUtils.isEmpty(method)) {
            log.error("method must not be null");
        }
        log.info("current exec : [{}] value : {}", method, values);

        if (notActionCmd(method, values)) {
            return;
        }

        By by = By.xpath(xpath);

        long wait = parseWait(preCheckConfig);

        if (SeleniumApp.exist(webDriver, by, Duration.ofMillis(wait))) {
            actionCmd(by, method, values);
        } else {
            log.error("could not locate element --> {}", xpath);
        }
    }

    /**
     * 执行非action事件，目前支持如下事件
     * <pre>
     *     1. sleep 睡眠
     *     2. switch 切换窗口
     *     3. get 在当前标签页，根据url打开一个窗口,要开新标签页参加{@link SeleniumApp#newPage()}
     * </pre>
     *
     * @return true则执行了该事件
     */
    private boolean notActionCmd(String method, String... values) {
        try {
            switch (method) {
                case "get":
                    try {
                        webDriver.get(values[0]);
                        log.info("open {} success", values[0]);
                    } catch (Exception e) {
                        log.error("open {} fail", values[0]);
                    }
                    return true;
                case "sleep":
                    try {
                        TimeUnit.MILLISECONDS.sleep(Long.parseLong(values[0]));
                    } catch (InterruptedException ignore) {
                    }
                    return true;
                case "switch":
                    for (String windowHandle : webDriver.getWindowHandles()) {
                        webDriver.switchTo().window(windowHandle);
                        if (webDriver.getTitle().contains(values[0])) {
                            log.info("will switch handle({}) title({})", windowHandle, webDriver.getTitle());
                            return true;
                        }
                    }
                default:
                    log.info("the current is an action event");
                    return false;
            }
        } catch (NumberFormatException e) {
            log.error("noAction task is error", e);
            return true;
        }
    }

    /**
     * 执行action事件，目前支持如下事件
     * <pre>
     *     1. click 点击事件
     *     2. sendKeys 填充事件
     *     3. board 键盘事件
     * </pre>
     */
    private void actionCmd(By by, String method, String... values) {
        WebElement element = webDriver.findElement(by);
        switch (method) {
            case "click":
                element.click();
                break;
            case "sendKeys":
                element.sendKeys(values);
                break;
            case "board":
                Keys[] keys = Arrays.stream(values).map(Keys::valueOf).toArray(Keys[]::new);
                element.sendKeys(keys);
                break;
            default:
                throw new RuntimeException("this command is temporarily not supported");
        }
    }

    /**
     * 根据{@link PreCheckConfig}解析本次操作的最大隐式等待时间
     *
     * @return 最大隐式等待时间 单位毫秒
     */
    private long parseWait(PreCheckConfig preCheckConfig) {
        long wait = 0;
        if (StringUtils.isNotEmpty(preCheckConfig.getWait())) {
            try {
                wait = Long.parseLong(preCheckConfig.getWait());
            } catch (Exception ignore) {
            }
        }
        return wait;
    }
}
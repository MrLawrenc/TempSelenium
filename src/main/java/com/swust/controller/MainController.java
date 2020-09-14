package com.swust.controller;

import com.swust.SeleniumTest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : LiuMingyao
 * 2019/12/8 19:55
 */
@Slf4j
public class MainController implements Initializable {
    @FXML
    private Button openBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Button fullCheckBtn;


    private SeleniumTest seleniumTest;
    /**
     * 标记浏览器是否打开
     */
    private final AtomicBoolean opened = new AtomicBoolean(false);
    /**
     * 核保url
     */
    private static final String CHECK_URL = "https://cps.qixin18.com/apps/cps/lxr1000014/product/insure?prodId=121482&planId=122830&cuid=213567b7-4c1c-48b9-9e85-4eda6e0448d2&aid=&encryptInsureNum=aKCN_w73h-TEOqKSb6dBCQ&notifyAnswerId=3526018&isHealthSuccess=true";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.seleniumTest = new SeleniumTest();
        CompletableFuture.runAsync(seleniumTest::initDriver);
        bindEvent();
    }

    /**
     * 资源销毁工作
     */
    public void destroy() {
        seleniumTest.quitBrowser();
    }

    /**
     * 事件绑定
     */
    private void bindEvent() {
        //开
        openBtn.setOnAction(event -> {
            openBtn.setDisable(true);
            CompletableFuture.runAsync(() -> seleniumTest.openBrowser(CHECK_URL)).whenComplete((r, t) -> {
                if (t == null) {
                    opened.set(true);
                } else {
                    log.error("浏览器开启失败", t);
                    seleniumTest.quitBrowser();
                    openBtn.setDisable(true);
                }
            });
        });
        //关
        closeBtn.setOnAction(event -> {
            if (opened.get()) {
                CompletableFuture.runAsync(() -> seleniumTest.quitBrowser());
            } else {
                log.warn("浏览器未开启");
            }
        });

        //自动填充
        fullCheckBtn.setOnAction(event -> seleniumTest.fullCheckInfo());
    }


}
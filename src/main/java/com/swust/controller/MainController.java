package com.swust.controller;

import com.swust.SeleniumTest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
    @FXML
    private TextField targetUrl;
    @FXML
    private Button preCheckConfig;
    @FXML
    private TableView<PreCheckConfig> preCheckConfigList;


    @FXML
    private TableColumn<PreCheckConfig, String> actionColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> xpathColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> valueColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> scriptColumn;


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

        targetUrl.setText("url");


        initTableView();
    }

    private void initTableView() {
        //绑定属性
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        xpathColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        scriptColumn.setCellValueFactory(new PropertyValueFactory<>("script"));

        //可编辑
        actionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scriptColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        xpathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        //空表
        ObservableList<PreCheckConfig> cellData = FXCollections.observableArrayList();

        ObservableList<PreCheckConfig> data = FXCollections.observableArrayList(
                new PreCheckConfig("Jacob", "Smith", "jacob.smith@example.com", "java脚本"),
                new PreCheckConfig("Isabella", "Johnson", "isabella.johnson@example.com", "java脚本"),
                new PreCheckConfig("Ethan", "Williams", "ethan.williams@example.com", "java脚本"),
                new PreCheckConfig("Emma", "Jones", "emma.jones@example.com", "java脚本"),
                new PreCheckConfig("Michael", "Brown", "michael.brown@example.com", "java脚本")
        );
        preCheckConfigList.setItems(data);

        preCheckConfigList.editingCellProperty().addListener(new ChangeListener<TablePosition<PreCheckConfig, ?>>() {
            @Override
            public void changed(ObservableValue<? extends TablePosition<PreCheckConfig, ?>> observable, TablePosition<PreCheckConfig, ?> oldValue, TablePosition<PreCheckConfig, ?> newValue) {
                System.out.println("sssssssssss");
            }
        });
        // 可以实时触发
        // preCheckConfigList.getItems().remove(1,3);
    }

    public void openBrowser() {
        //openBtn.setDisable(true);
        CompletableFuture.runAsync(() -> seleniumTest.openBrowser(targetUrl.getText())).whenComplete((r, t) -> {
            if (t == null) {
                opened.set(true);
            } else {
                log.error("浏览器开启失败", t);
                seleniumTest.quitBrowser();
                //openBtn.setDisable(true);
            }
        });
    }

    public void quitBrowser() {
        if (opened.get()) {
            CompletableFuture.runAsync(() -> seleniumTest.quitBrowser());
        } else {
            log.warn("浏览器未开启");
        }
    }

    /**
     * 自动填充
     */
    public void fullCheckInfo() {
        seleniumTest.fullCheckInfo();
    }

    /**
     * 资源销毁工作
     */
    public void destroy() {
        seleniumTest.quitBrowser();
    }
}
package com.swust.controller;

import com.swust.ConfigUtil;
import com.swust.SeleniumTest;
import com.swust.entity.ProductConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : LiuMingyao
 * 2019/12/8 19:55
 */
@Slf4j
@Getter
public class MainController implements Initializable {
    @FXML
    private Button openBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Button fullCheckBtn;
    /**
     * fix 应更改为下拉框
     */
    @FXML
    private TextField targetUrl;
    @FXML
    private Button preCheckConfig;

    /**
     * 核保之前的配置表
     */
    @FXML
    private TableView<PreCheckConfig> preCheckConfigList;


    /**
     * 每一列
     */
    @FXML
    private TableColumn<PreCheckConfig, String> actionColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> xpathColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> valueColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> scriptColumn;

    /**
     * 公司和产品的下拉框
     */
    @FXML
    private ComboBox<Integer> companyIdBox;
    @FXML
    private ComboBox<Integer> productIdBox;

    /**
     * 样例名字
     */
    @FXML
    private Text caseName;


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
        //this.seleniumTest = new SeleniumTest();
        //CompletableFuture.runAsync(seleniumTest::initDriver);


        ConfigUtil.initConfig();

        ConfigUtil.loadCompanyAndProduct(this);

        //整合所有列，并初始化为一个表
        List<TableColumn<PreCheckConfig, String>> columnList = new ArrayList<>();
        columnList.add(xpathColumn);
        columnList.add(actionColumn);
        columnList.add(valueColumn);
        columnList.add(scriptColumn);
        initTableView(columnList);
    }


    private void initTableView(List<TableColumn<PreCheckConfig, String>> columnList) {
        //绑定属性
        Field[] fields = PreCheckConfig.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            //将每个字段名作为列名
            columnList.get(i).setCellValueFactory(new PropertyValueFactory<>(fields[i].getName()));

            //可编辑
            columnList.get(i).setCellFactory(TextFieldTableCell.forTableColumn());
        }

        ObservableList<PreCheckConfig> data = FXCollections.observableArrayList(
                new PreCheckConfig("Jacob", "Smith", "jacob.smith@example.com", "java脚本"),
                new PreCheckConfig("Isabella", "Johnson", "isabella.johnson@example.com", "java脚本")
        );
        preCheckConfigList.setItems(data);
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

    /**
     * preCheckConfigList配置表更改
     */
    public void preConfigCommit(TableColumn.CellEditEvent<String, String> editEvent) {
        String newValue = editEvent.getNewValue();

        //需要更改的目标字段
        String targetField = editEvent.getTableColumn().getText();

        log.info("will update {} to {}", targetField, newValue);
        ProductConfig productConfig = ConfigUtil.configTable.get(companyIdBox.getSelectionModel().getSelectedItem(), productIdBox.getSelectionModel().getSelectedItem());
        if (Objects.nonNull(productConfig)) {
            PreCheckConfig preCheckConfig = productConfig.getPreCheckConfig();
            try {
                Field declaredField = PreCheckConfig.class.getDeclaredField(targetField);
                declaredField.setAccessible(true);
                declaredField.set(preCheckConfig, newValue);
            } catch (Exception e) {
                log.error("update {} value to {} fail", targetField, newValue, e);
            }
        }
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
        if (Objects.nonNull(seleniumTest)) {
            seleniumTest.quitBrowser();
        }
    }
}
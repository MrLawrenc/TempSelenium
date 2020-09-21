package com.swust.controller;

import com.alibaba.fastjson.JSON;
import com.swust.SeleniumApp;
import com.swust.entity.PreCheckConfig;
import com.swust.entity.ProductConfig;
import com.swust.utils.ConfigUtil;
import com.swust.utils.SeleniumCmdParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private Button closeBtn;
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
    private TableView<PreCheckConfig> preCheckConfigTable;


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
    @FXML
    private TableColumn<PreCheckConfig, String> waitColumn;
    @FXML
    private TableColumn<PreCheckConfig, String> descColumn;


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


    private SeleniumApp seleniumApp;
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
        this.seleniumApp = new SeleniumApp();
        CompletableFuture.runAsync(seleniumApp::initDriver);

        ConfigUtil.initConfig();

        ConfigUtil.loadCompanyAndProductByConfig(this);

        //整合所有列，并初始化为一个表
        initTableView(preCheckConfigTable.getColumns());
    }

    /**
     * 配置表结构初始化
     */
    private void initTableView(ObservableList<TableColumn<PreCheckConfig, ?>> columnList) {
        //绑定属性
        Field[] fields = PreCheckConfig.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            @SuppressWarnings("unchecked")
            TableColumn<PreCheckConfig, String> column = (TableColumn<PreCheckConfig, String>) columnList.get(i);
            column.setText(fields[i].getName());

            //将PreCheckConfig字段名和每一列取值进行绑定（每一列都根据字段名来取相应的值）
            column.setCellValueFactory(new PropertyValueFactory<>(fields[i].getName()));

            //设为可编辑
            column.setCellFactory(TextFieldTableCell.forTableColumn());
        }

        ObservableList<PreCheckConfig> data = FXCollections.observableArrayList(
                new PreCheckConfig("操作位置，目前是xpath定位", "操作函数", "jacob.smith@example.com", "java脚本", "最大等待时间，单位毫秒", "示例一"),
                new PreCheckConfig("操作位置，目前是xpath定位", "操作函数", "isabella.johnson@example.com", "java脚本", "", "示例二")
        );
        preCheckConfigTable.setItems(data);
    }

    /**
     * preCheckConfigList配置表更改
     */
    public void preConfigCommit(TableColumn.CellEditEvent<String, String> editEvent) {
        String newValue = editEvent.getNewValue();

        //需要更改的目标字段
        String targetField = editEvent.getTableColumn().getText();


        ProductConfig productConfig = ConfigUtil.configTable.get(companyIdBox.getSelectionModel().getSelectedItem(), productIdBox.getSelectionModel().getSelectedItem());
        if (Objects.nonNull(productConfig)) {
            int selectIdx = preCheckConfigTable.getSelectionModel().getFocusedIndex();
            log.info("will update {} to {} , in the {}th row", targetField, newValue, selectIdx + 1);
            try {
                Field declaredField = PreCheckConfig.class.getDeclaredField(targetField);
                declaredField.setAccessible(true);
                declaredField.set(preCheckConfigTable.getItems().get(selectIdx), newValue);
            } catch (Exception e) {
                log.error("update {} value to {} fail", targetField, newValue, e);
            }
        }
    }

    public void quitBrowser() {
        if (opened.get()) {
            CompletableFuture.runAsync(() -> seleniumApp.quitBrowser());
        } else {
            log.warn("浏览器未开启");
        }
    }

    /**
     * 自动填充核保数据
     */
    public void fullCheckInfo(ActionEvent event) {
        seleniumApp.fullCheckInfo();
    }

    /**
     * 资源销毁工作
     */
    public void destroy() {
        if (Objects.nonNull(seleniumApp)) {
            seleniumApp.quitBrowser();
        }
    }

    public void newPage(ActionEvent event) {
        Button button = (Button) event.getSource();
        System.out.println(button.getText());
        seleniumApp.newPage(targetUrl.getText());
    }

    /**
     * 添加一个 {@link PreCheckConfig}
     */
    public void addPreConfig() {
        preCheckConfigTable.getItems().add(new PreCheckConfig());
    }

    public void savePreConfig() {
        List<PreCheckConfig> preCheckConfigList = new ArrayList<>(preCheckConfigTable.getItems());

        ConfigUtil.saveConfig(caseName.getText(), companyIdBox.getSelectionModel().getSelectedItem(),
                productIdBox.getSelectionModel().getSelectedItem(), preCheckConfigList);
    }

    /**
     * 执行样例
     */
    public void execConfig() {
        log.info("current exec config --> {}", JSON.toJSONString(ConfigUtil.currentConfig));
        List<PreCheckConfig> preCheckConfigList = ConfigUtil.currentConfig.getPreCheckConfigList();

        SeleniumCmdParser seleniumCmdParser = SeleniumCmdParser.newParser(seleniumApp.getDriver());

        CompletableFuture.runAsync(() -> preCheckConfigList.forEach(seleniumCmdParser::parseExec));
    }
}
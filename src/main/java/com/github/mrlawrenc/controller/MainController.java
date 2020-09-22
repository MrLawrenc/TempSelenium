package com.github.mrlawrenc.controller;

import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.entity.ProductConfig;
import com.github.mrlawrenc.entity.conf.StepCommand;
import com.github.mrlawrenc.utils.ConfigParser;
import com.github.mrlawrenc.utils.SeleniumApp;
import com.github.mrlawrenc.utils.SeleniumCmdParser;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

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
 * <p>
 * fxml主控制器
 */
@Slf4j
@Getter
@Component
public class MainController implements Initializable, DisposableBean {
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
    private TableView<StepCommand> preCheckConfigTable;


    /**
     * 每一列
     */
    @FXML
    private TableColumn<StepCommand, String> actionColumn;
    @FXML
    private TableColumn<StepCommand, String> xpathColumn;
    @FXML
    private TableColumn<StepCommand, String> valueColumn;
    @FXML
    private TableColumn<StepCommand, String> scriptColumn;
    @FXML
    private TableColumn<StepCommand, String> waitColumn;
    @FXML
    private TableColumn<StepCommand, String> descColumn;


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

    @Override
    public void destroy() {
        log.info("destroy controller");
        if (Objects.nonNull(seleniumApp)) {
            seleniumApp.quitBrowser();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.seleniumApp = new SeleniumApp();
        CompletableFuture.runAsync(seleniumApp::initDriver);

        ConfigParser.initConfig();

        ConfigParser.loadCompanyAndProductByConfig(this);

        //整合所有列，并初始化为一个表
        initTableView(preCheckConfigTable.getColumns());
    }

    /**
     * 配置表结构初始化
     */
    private void initTableView(ObservableList<TableColumn<StepCommand, ?>> columnList) {
        //绑定属性
        Field[] fields = StepCommand.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            @SuppressWarnings("unchecked")
            TableColumn<StepCommand, String> column = (TableColumn<StepCommand, String>) columnList.get(i);
            column.setText(fields[i].getName());

            //将StepCommand字段名和每一列取值进行绑定（每一列都根据字段名来取相应的值）
            column.setCellValueFactory(new PropertyValueFactory<>(fields[i].getName()));

            //设为可编辑
            column.setCellFactory(TextFieldTableCell.forTableColumn());
        }

        ObservableList<StepCommand> data = FXCollections.observableArrayList(
                new StepCommand("操作位置，目前是xpath定位", "操作函数", "jacob.smith@example.com", "java脚本", "最大等待时间，单位毫秒", "示例一"),
                new StepCommand("操作位置，目前是xpath定位", "操作函数", "isabella.johnson@example.com", "java脚本", "", "示例二")
        );
        preCheckConfigTable.setItems(data);
    }

    /**
     * StepCommandList配置表更改
     */
    public void preConfigCommit(TableColumn.CellEditEvent<String, String> editEvent) {
        String newValue = editEvent.getNewValue();

        //需要更改的目标字段
        String targetField = editEvent.getTableColumn().getText();


        ProductConfig productConfig = ConfigParser.configTable.get(companyIdBox.getSelectionModel().getSelectedItem(), productIdBox.getSelectionModel().getSelectedItem());
        if (Objects.nonNull(productConfig)) {
            int selectIdx = preCheckConfigTable.getSelectionModel().getFocusedIndex();
            log.info("will update {} to {} , in the {}th row", targetField, newValue, selectIdx + 1);
            try {
                Field declaredField = StepCommand.class.getDeclaredField(targetField);
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


    public void newPage(ActionEvent event) {
        Button button = (Button) event.getSource();
        System.out.println(button.getText());
        seleniumApp.newPage(targetUrl.getText());
    }

    /**
     * 添加一个 {@link StepCommand}
     */
    public void addPreConfig() {
        preCheckConfigTable.getItems().add(new StepCommand());
    }

    public void savePreConfig() {
        List<StepCommand> StepCommandList = new ArrayList<>(preCheckConfigTable.getItems());

        ConfigParser.saveConfig(caseName.getText(), companyIdBox.getSelectionModel().getSelectedItem(),
                productIdBox.getSelectionModel().getSelectedItem(), StepCommandList);
    }

    /**
     * 执行样例
     */
    public void execConfig() {
        log.info("current exec config --> {}", JSON.toJSONString(ConfigParser.currentConfig));
        List<StepCommand> stepCommandList = ConfigParser.currentConfig.getPreCheckConfigList();

        SeleniumCmdParser seleniumCmdParser = SeleniumCmdParser.newParser(seleniumApp.getDriver());

        CompletableFuture.runAsync(() -> stepCommandList.forEach(seleniumCmdParser::parseExec));
    }
}
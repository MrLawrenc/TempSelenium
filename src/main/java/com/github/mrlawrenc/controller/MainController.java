package com.github.mrlawrenc.controller;

import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.config.JfxConfiguration;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import com.github.mrlawrenc.entity.conf.StepCommand;
import com.github.mrlawrenc.storage.AbstractJfxStorage;
import com.github.mrlawrenc.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
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
    @Setter
    private Stage stage;
    @FXML
    private Button closeBtn;
    /**
     * fix 应更改为下拉框
     */
    @FXML
    private TextField valueGeneratorPath;
    @FXML
    private TextField scriptName;


    /**
     * 核保之前的配置表
     */
    @FXML
    private TableView<StepCommand> commandTable;


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
     * 样例下拉框
     */
    @FXML
    private ComboBox<String> caseBox;

    /**
     * 样例名字
     */
    @FXML
    private Text caseName;

    private SeleniumApp seleniumApp;


    @Autowired
    private ConfigParser configParser;
    @Autowired
    private JfxConfiguration jfxConfiguration;
    @Autowired
    private AbstractJfxStorage jfxStorage;
    /**
     * 标记浏览器是否打开
     */
    private final AtomicBoolean opened = new AtomicBoolean(false);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (jfxConfiguration.isOpenBrowser()) {
            this.seleniumApp = new SeleniumApp();
            CompletableFuture.runAsync(seleniumApp::initDriver);
        }
        configParser.loadAllCase(this);

        //整合所有列，并初始化为一个表
        initTableView(commandTable.getColumns());

        caseName.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                log.info("暂不支持重命名");
            }
        });
    }

    @Override
    public void destroy() {
        log.info("destroy controller");
        if (Objects.nonNull(seleniumApp)) {
            seleniumApp.quitBrowser();
        }
    }

    /**
     * 配置表结构初始化
     */
    @SuppressWarnings("unchecked")
    private void initTableView(ObservableList<TableColumn<StepCommand, ?>> columnList) {
        //绑定属性
        Field[] fields = StepCommand.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            TableColumn<StepCommand, String> column = (TableColumn<StepCommand, String>) columnList.get(i);
            column.setText(fields[i].getName());

            column.setSortable(false);
            //将StepCommand字段名和每一列取值进行绑定（每一列都根据字段名来取相应的值）
            column.setCellValueFactory(new PropertyValueFactory<>(fields[i].getName()));

            //扩展自TextFieldTableCell.forTableColumn()中的TextFieldTableCell
            //column.setCellFactory(RealTimeEditTextFieldCell::new);

            column.setCellFactory(c -> new RealTimeEditTextFieldCell(c, column.getText().equals("location")));
        }

        ObservableList<StepCommand> data = FXCollections.observableArrayList(
                new StepCommand("操作位置，目前是xpath定位", "操作函数", "jacob.smith@example.com", "java脚本", "最大等待时间，单位毫秒", "示例一"),
                new StepCommand("操作位置，目前是xpath定位", "操作函数", "isabella.johnson@example.com", "java脚本", "", "示例二")
        );
        commandTable.setItems(data);

        //行格式设置
        commandTable.setRowFactory(new Callback<TableView<StepCommand>, TableRow<StepCommand>>() {
            @Override
            public TableRow<StepCommand> call(TableView<StepCommand> stepCommandTableView) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(StepCommand stepCommand, boolean empty) {
                        super.updateItem(stepCommand, empty);
/*
                        if (Objects.nonNull(stepCommand) && StringUtils.isEmpty(stepCommand.getLocation())) {
                            this.setStyle("-fx-background-color: aqua");
                        }else {
                            this.setStyle("-fx-background-color: brown");
                        }*/

                    }
                };
            }
        });
    }

    /**
     * 配置表更改
     */
    public void editCommit(TableColumn.CellEditEvent<String, String> editEvent) {
        String newValue = editEvent.getNewValue();

        //需要更改的目标字段
        String targetField = editEvent.getTableColumn().getText();

        int selectIdx = commandTable.getSelectionModel().getFocusedIndex();
        log.info("will update {} to {} , in the {}th row", targetField, newValue, selectIdx + 1);
        try {
            Field declaredField = StepCommand.class.getDeclaredField(targetField);
            declaredField.setAccessible(true);
            declaredField.set(commandTable.getItems().get(selectIdx), newValue);
        } catch (Exception e) {
            log.error("update {} value to {} fail", targetField, newValue, e);
        }
    }

    public void quitBrowser() {
        if (opened.get()) {
            CompletableFuture.runAsync(() -> seleniumApp.quitBrowser());
        } else {
            log.warn("浏览器未开启");
        }
    }


    public void selectFile(ActionEvent event) {
        Button button = (Button) event.getSource();
        System.out.println(button.getText());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导入值生成器脚本");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All File", "*.*"),
                new FileChooser.ExtensionFilter("CLASS ", "*.class"),
                new FileChooser.ExtensionFilter("JAVA", "*.java"),
                new FileChooser.ExtensionFilter("JAR", "*.jar"),

                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png")

        );
        File file = fileChooser.showOpenDialog(stage);
        if (Objects.nonNull(file)) {
            valueGeneratorPath.setText(file.getAbsolutePath());
        }
    }


    /**
     * 创建一个新用例
     */
    public void createCase() throws Exception {
        TextInputDialog dialog = new TextInputDialog(UUID.randomUUID().toString());

        dialog.getDialogPane().setGraphic(new ImageView(jfxConfiguration.getTitleResource().getURL().toString()));

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear();

        dialog.setTitle("创建一个新的用例");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入你的用例名:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(r -> {
            CaseConfig caseConfig = new CaseConfig();
            caseConfig.setCaseName(r);
            caseConfig.setCreateTime(LocalDateTime.now());
            try {
                boolean success = jfxStorage.save(caseConfig);
                log.info("create new case({}) {}", r, success ? "success" : "fail");
                configParser.refresh(this);
            } catch (Exception exception) {
                log.error("create new case({}) fail", r);
            }
        });

    }

    public void importGenerator() {
        log.info("start import {} script,path : {}", scriptName.getText(), valueGeneratorPath.getText());
        //todo load file
        ValueGeneratorParser.parse(new File(valueGeneratorPath.getText()));
    }

    /**
     * 添加一个 {@link StepCommand}
     */
    public void addCommand() {
        StepCommand stepCommand = new StepCommand();
        stepCommand.setAction("ignore");
        commandTable.getItems().add(stepCommand);

        commandTable.getSelectionModel().select(commandTable.getItems().size() - 1);
    }

    public void savePreConfig() {
        configParser.saveConfig(caseName.getText(), new ArrayList<>(commandTable.getItems()));
    }

    /**
     * 执行样例
     */
    public void execConfig() {
        List<StepCommand> stepCommandList = new ArrayList<>(commandTable.getItems());
        log.info("current exec config --> {}", JSON.toJSONString(stepCommandList));


        SeleniumCmdParser seleniumCmdParser = SeleniumCmdParser.newParser(seleniumApp.getDriver());

        CompletableFuture.runAsync(() -> stepCommandList.forEach(seleniumCmdParser::parseExec));
    }

}
package com.github.mrlawrenc.utils;

import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.controller.MainController;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import com.github.mrlawrenc.entity.conf.StepCommand;
import com.github.mrlawrenc.storage.AbstractJfxStorage;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toMap;

/**
 * @author hz20035009-逍遥
 * date   2020/9/18 17:05
 */
@Slf4j
@Component
public class ConfigParser {


    @Autowired
    private AbstractJfxStorage<CaseConfig> jfxStorage;

    //public static List<ProductConfig> configList;
    //public static ProductConfig currentConfig;
    //public static Table<Integer, Integer, ProductConfig> configTable = HashBasedTable.create();

    /**
     * 当前配置是否已初始化
     */
    public static final AtomicBoolean INIT = new AtomicBoolean(false);


    private Map<String, CaseConfig> configMap;

    @PostConstruct
    public void init() {
        List<CaseConfig> caseConfigList = jfxStorage.list();
        configMap = caseConfigList.stream().collect(toMap(CaseConfig::getCaseName, c -> c, (caseConfig1, caseConfig2) -> {
            log.error("find same case will cover {}", JSON.toJSONString(caseConfig1));
            return caseConfig2;
        }));
    }

    public void loadAllCase(MainController controller) {
        ComboBox<String> idBox = controller.getCaseBox();
        idBox.setItems(FXCollections.observableArrayList(configMap.keySet()));

        idBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isEmpty(newValue)) {
                return;
            }
            CaseConfig caseConfig = configMap.get(newValue);
            //更新配置
            controller.getCommandTable().setItems(FXCollections.observableArrayList(caseConfig.getStepCommand()));

            //更新标题
            controller.getCaseName().setText(caseConfig.getCaseName());
        });
    }

    public void refresh(MainController controller) {
        init();
        ComboBox<String> idBox = controller.getCaseBox();
        idBox.setItems(FXCollections.observableArrayList(configMap.keySet()));
    }

    /**
     * 保存配置
     */
    public void saveConfig(String caseName, List<StepCommand> commandList) {
        //更新缓存
        CaseConfig caseConfig = configMap.get(caseName);

        if (Objects.isNull(caseConfig)) {
            log.error("find config fail by caseName({})", caseName);
            return;
        }

        caseConfig.setStepCommand(commandList);

        try {
            jfxStorage.update(caseConfig);
        } catch (Exception e) {
            log.error("update case({}) fail", caseName);
        }

    }

    public static String getCaseFileName(String caseName, Integer companyId, Integer productId) {
        return caseName + "#" + companyId + "_" + productId + ".jfx";

    }
}
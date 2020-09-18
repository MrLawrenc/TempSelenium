package com.swust;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author hz20035009-逍遥
 * date   2020/9/18 17:05
 */
@Slf4j
public class ConfigUtil {


    public static void initConfig() {
        File[] allFile = new File("./").listFiles();
        if (null==allFile||allFile.length==0){
            log.warn("not find config");
        }
        List<File> fileList = Arrays.stream(allFile)
                .filter(f -> f.getName().endsWith(".jfx"))
                .peek(f -> log.info("find config file --> {}", f.getName()))
                .collect(toList());
    }


    public static void loadCompanyAndProduct(ComboBox<Integer> companyIdBox, ComboBox<Integer> productIdBox) {
        ObservableList<Integer> companyIds = FXCollections.observableArrayList();
        companyIdBox.setItems(FXCollections.observableArrayList(12, 13));
        productIdBox.setItems(FXCollections.observableArrayList(12110, 13241));
    }
}
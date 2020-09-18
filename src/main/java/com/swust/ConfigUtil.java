package com.swust;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.swust.controller.PreCheckConfig;
import com.swust.entity.ProductConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;

/**
 * @author hz20035009-逍遥
 * date   2020/9/18 17:05
 */
@Slf4j
public class ConfigUtil {
    public static List<ProductConfig> configList;

    /**
     * 当前配置是否已初始化
     */
    public static final AtomicBoolean INIT = new AtomicBoolean(false);

    /**
     * 初始化配置，当且仅当被调用一次
     */
    public static void initConfig() {
        if (INIT.get()) {
            log.warn("current config loaded");
            return;
        }

        File[] allFile = new File("./").listFiles();
        if (null == allFile || allFile.length == 0) {
            log.warn("not find config");
            configList = Lists.newArrayList();
            return;
        }
        List<File> fileList = Arrays.stream(allFile)
                .filter(f -> f.getName().endsWith(".jfx"))
                .peek(f -> log.info("find config file --> {}", f.getName()))
                .collect(toList());

        configList = fileList.stream().map(f -> {
            ProductConfig productConfig = new ProductConfig();
            String[] companyAndProduct = f.getName().split("\\.")[0].split("_");
            productConfig.setCompanyId(Integer.parseInt(companyAndProduct[0]));
            productConfig.setProductId(Integer.parseInt(companyAndProduct[1]));


            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                String first = reader.readLine();
                String second = reader.readLine();
                productConfig.setPreviewUrl(first);
                productConfig.setProductionUrl(second);

                StringBuilder sb = new StringBuilder();
                String s = reader.readLine();
                while (!StringUtils.isEmpty(s)) {
                    sb.append(s);
                    s = reader.readLine();
                }
                PreCheckConfig preCheckConfig = JSON.parseObject(sb.toString(), PreCheckConfig.class);
                productConfig.setPreCheckConfig(preCheckConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }

            INIT.compareAndSet(false, true);
            return productConfig;
        }).collect(toList());

    }


    public static void loadCompanyAndProduct(ComboBox<Integer> companyIdBox, ComboBox<Integer> productIdBox, TableView<PreCheckConfig> preCheckConfigList) {
        ObservableList<Integer> companyIds = FXCollections.observableArrayList();
        //优先加载所有公司id
        configList.forEach(conf -> companyIds.add(conf.getCompanyId()));
        companyIdBox.setItems(companyIds);


        //绑定公司id，使得产品id实时刷新
        companyIdBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<Integer> productResult = configList.stream().filter(c -> c.getCompanyId().equals(newValue))
                    .map(ProductConfig::getProductId).collect(toList());
            ObservableList<Integer> productIds = FXCollections.observableArrayList();
            productIds.addAll(productResult);
            productIdBox.setItems(productIds);
        });

        //绑定产品id，使得配置实时刷新
        productIdBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> ConfigUtil.loadPreCheckConfig(companyIdBox.getValue(), newValue, preCheckConfigList));
    }

    public static void loadPreCheckConfig(Integer companyId, Integer productId, TableView<PreCheckConfig> preCheckConfigList) {
        log.info("load companyId:{} productId:{} config", companyId, productId);
        // 可以实时触发
        preCheckConfigList.getItems().remove(0, preCheckConfigList.getItems().size());

        for (ProductConfig config : configList) {
            if (config.getCompanyId().equals(companyId) && config.getProductId().equals(productId)) {
                preCheckConfigList.getItems().add(config.getPreCheckConfig());
            }
        }
    }
}
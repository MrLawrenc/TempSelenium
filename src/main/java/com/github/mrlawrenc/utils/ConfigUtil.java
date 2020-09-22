package com.github.mrlawrenc.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.controller.MainController;
import com.github.mrlawrenc.entity.PreCheckConfig;
import com.github.mrlawrenc.entity.ProductConfig;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import com.github.mrlawrenc.storage.AbstractJfxStorage;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;

/**
 * @author hz20035009-逍遥
 * date   2020/9/18 17:05
 */
@Slf4j
@Component
public class ConfigUtil {

    @Autowired
    private AbstractJfxStorage<ProductConfig, CaseConfig> jfxStorage;

    public static List<ProductConfig> configList;
    public static ProductConfig currentConfig;
    public static Table<Integer, Integer, ProductConfig> configTable = HashBasedTable.create();

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

        List<File> fileList = allCaseFile(null);
        if (CollectionUtil.isEmpty(fileList)) {
            return;
        }

        //读取所有配置
        configList = fileList.stream().map(f -> {
            ProductConfig productConfig = new ProductConfig();
            String[] firstSplit = f.getName().split("#");
            productConfig.setCaseName(firstSplit[0]);

            String[] companyAndProduct = firstSplit[1].split("\\.")[0].split("_");
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
                List<PreCheckConfig> preCheckConfigList = JSON.parseArray(sb.toString(), PreCheckConfig.class);
                productConfig.setPreCheckConfigList(preCheckConfigList);
            } catch (Exception e) {
                log.error("read config fail", e);
            }

            INIT.compareAndSet(false, true);
            return productConfig;
        }).collect(toList());

        configList.forEach(c -> configTable.put(c.getCompanyId(), c.getProductId(), c));
    }


    /**
     * 筛选出符合 containsNameFilter和 以.jfx结尾的文件
     *
     * @param containsNameFilter 文件名包含过滤器
     * @return 结果文件集合
     */
    public static List<File> allCaseFile(String containsNameFilter) {
        File[] allFile = new File("./").listFiles();
        if (null == allFile || allFile.length == 0) {
            log.warn("not find config");
            configList = Lists.newArrayList();
            return null;
        }
        return Arrays.stream(allFile)
                .filter(f -> f.getName().endsWith(".jfx"))
                .filter(f -> {
                    if (StringUtils.isNotEmpty(containsNameFilter)) {
                        return f.getName().contains(containsNameFilter);
                    }
                    return true;
                })
                .peek(f -> log.info("find config file --> {}", f.getName()))
                .collect(toList());
    }

    public static void loadCompanyAndProductByConfig(MainController controller) {
        ComboBox<Integer> companyIdBox = controller.getCompanyIdBox();
        ComboBox<Integer> productIdBox = controller.getProductIdBox();
        Set<Integer> companyIds = new HashSet<>();
        //优先加载所有公司id
        configList.forEach(conf -> companyIds.add(conf.getCompanyId()));
        companyIdBox.setItems(FXCollections.observableArrayList(companyIds));


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
                (observable, oldValue, newValue) -> ConfigUtil.loadPreCheckConfig(companyIdBox.getValue(), newValue,
                        controller.getPreCheckConfigTable(), controller.getCaseName(), controller.getTargetUrl()));
    }

    public static void loadPreCheckConfig(Integer companyId, Integer productId, TableView<PreCheckConfig> preCheckConfigList
            , Text caseName, TextField targetUrl) {
        if (Objects.isNull(companyId)) {
            log.error("companyId must not be empty");
            return;
        }
        if (Objects.isNull(productId)) {
            return;
        }
        log.info("load companyId:{} productId:{} config", companyId, productId);
        // 可以实时触发
        preCheckConfigList.getItems().remove(0, preCheckConfigList.getItems().size());

        for (ProductConfig config : configList) {
            if (config.getCompanyId().equals(companyId) && config.getProductId().equals(productId)) {

                currentConfig = config;

                preCheckConfigList.getItems().addAll(config.getPreCheckConfigList());

                //加载测试用例标题
                caseName.setText(config.getCaseName());
                //加载url
                targetUrl.setText(config.getPreviewUrl());
            }
        }
    }

    /**
     * 保存配置
     */
    public static void saveConfig(String caseName, Integer companyId, Integer productId, List<PreCheckConfig> preCheckConfigList) {
        if (Objects.isNull(companyId) || Objects.isNull(productId)) {
            return;
        }

        String caseFileName = getCaseFileName(caseName, companyId, productId);

        configList.forEach(c -> {
            if (companyId.intValue() == c.getCompanyId() && productId.intValue() == c.getProductId()) {
                c.setPreCheckConfigList(preCheckConfigList);
                Optional.ofNullable(allCaseFile(caseFileName))
                        .flatMap(l -> l.stream().filter(file -> file.getName().equals(caseFileName)).findAny())
                        .ifPresent(targetFile -> {
                            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                                //构件文件内容
                                String targetInfo = c.getPreviewUrl() +
                                        "\n" +
                                        c.getProductionUrl() +
                                        "\n" +
                                        JSON.toJSONString(preCheckConfigList);
                                fos.write(targetInfo.getBytes(StandardCharsets.UTF_8));
                                log.info("update file {} success", caseFileName);
                            } catch (Exception e) {
                                log.error("update file {} fail", caseFileName);
                            }
                        })
                ;
            }
        });
    }

    public static String getCaseFileName(String caseName, Integer companyId, Integer productId) {
        return caseName + "#" + companyId + "_" + productId + ".jfx";

    }
}
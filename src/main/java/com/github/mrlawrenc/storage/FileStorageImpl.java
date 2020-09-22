package com.github.mrlawrenc.storage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.entity.ProductConfig;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:42
 * <p>
 * 文件存储
 */
@Slf4j
public class FileStorageImpl extends AbstractJfxStorage<ProductConfig, CaseConfig> {
    private final static String SUFFIX = ".case";

    @Override
    public boolean save(ProductConfig source) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(new File("./" + source.getCaseName() + SUFFIX))) {
            fos.write(JSON.toJSONString(source).getBytes(StandardCharsets.UTF_8));
            return true;
        }
    }

    @Override
    public List<CaseConfig> list() {
        File[] files = new File("./").listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            return null;
        }

        return Arrays.stream(files)
                .filter(f -> f.getName().endsWith(SUFFIX))
//                .filter(f -> {
//                    if (StringUtils.isNotEmpty(containsNameFilter)) {
//                        return f.getName().contains(containsNameFilter);
//                    }
//                    return true;
//                })
                .peek(f -> log.info("find config file --> {}", f.getName()))
                .map(f -> {
                    try (FileInputStream fio = new FileInputStream(f)) {
                        String configJson = new String(fio.readAllBytes(), StandardCharsets.UTF_8);
                        return JSON.parseObject(configJson, CaseConfig.class);
                    } catch (Exception e) {
                        log.error("parse file({}) fail!", f.getName(), e);
                        return null;
                    }
                })
                .collect(toList());

    }

    @Override
    public CaseConfig byCaseName(String caseName) throws Exception {
        File file = new File("./" + caseName + SUFFIX);
        if (file.exists()) {
            try (FileInputStream fio = new FileInputStream(file)) {
                String configJson = new String(fio.readAllBytes(), StandardCharsets.UTF_8);
                return JSON.parseObject(configJson, CaseConfig.class);
            } catch (Exception e) {
                log.error("parse file({}) fail!", file.getName(), e);
                throw e;
            }
        }
        return null;
    }

    @Override
    public CaseConfig update(CaseConfig caseConfig) throws Exception {
        if (Objects.nonNull(caseConfig.getCaseName())) {
            CaseConfig targetBean = byCaseName(caseConfig.getCaseName());
            if (Objects.isNull(targetBean)) {
                throw new RuntimeException("not find file by name " + caseConfig.getCaseName());
            }
            BeanUtil.copyProperties(caseConfig, targetBean, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));


            File file = new File("./" + caseConfig.getCaseName() + SUFFIX);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(JSON.toJSONString(targetBean).getBytes(StandardCharsets.UTF_8));
                return targetBean;
            } catch (Exception e) {
                log.error("update file({}) fail!", file.getName(), e);
                throw e;
            }
        }
        return null;
    }
}
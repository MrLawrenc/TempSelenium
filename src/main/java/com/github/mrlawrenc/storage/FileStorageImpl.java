package com.github.mrlawrenc.storage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:42
 * <p>
 * 文件存储
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class FileStorageImpl extends AbstractJfxStorage<CaseConfig> {
    private final static String SUFFIX = ".case";

    /**
     * 过滤器
     */
    private Supplier<Boolean> filter;

    @Override
    public boolean save(CaseConfig source) throws Exception {
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
                .filter(f -> {
                    if (Objects.nonNull(filter)) {
                        return filter.get();
                    }
                    return true;
                })
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
            log.debug("old source:{}", JSON.toJSONString(targetBean));
            if (Objects.isNull(targetBean)) {
                throw new RuntimeException("not find file by name " + caseConfig.getCaseName());
            }
            BeanUtil.copyProperties(caseConfig, targetBean, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

            targetBean.setUpdateTime(LocalDateTime.now());

            File file = new File("./" + caseConfig.getCaseName() + SUFFIX);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(JSON.toJSONString(targetBean).getBytes(StandardCharsets.UTF_8));
                log.info("update config success,data:{}", JSON.toJSONString(caseConfig));
                return targetBean;
            } catch (Exception e) {
                log.error("update file({}) fail!", file.getName(), e);
                throw e;
            }
        }
        return null;
    }
}
package com.github.mrlawrenc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author hz20035009-逍遥
 * date   2020/9/25 10:06
 */
@Slf4j
public class ValueGeneratorParser {


    public static void parse(File file) {
        String name = file.getName();
        String extensionName = name.substring(name.lastIndexOf(".") + 1);
        if (StringUtils.isEmpty(extensionName)) {
            return;
        }

        switch (extensionName) {
            case "java":
                loadJavaFile(file);
                break;
            case "class":
                loadClassFile(file);
                break;
            case "jar":
                loadJarFile();
                break;
            default:
                log.error("unknown file type,will ignore file:{}", file.getName());
        }
    }

    private static void loadJavaFile(File file) {

    }

    private static void loadJarFile() {

    }

    private static void loadClassFile(File file) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
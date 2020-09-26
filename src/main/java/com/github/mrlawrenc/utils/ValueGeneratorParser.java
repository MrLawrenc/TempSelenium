package com.github.mrlawrenc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author hz20035009-逍遥
 * date   2020/9/25 10:06
 */
@Slf4j
public class ValueGeneratorParser {

    /**blog-content-box
     * p 是页数，一个搜索结果一般20页
     * https://so.csdn.net/so/search/s.do?q=java&t=all&platform=pc&p=40&s=&tm=&v=&l=&u=&ft=
     */
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://blog.csdn.net/qq_40695278/article/details/89073220"))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        System.out.println(response.body());
    }

    public static void parse(File file, ScriptLoader loader) {
        String name = file.getName();
        String extensionName = name.substring(name.lastIndexOf(".") + 1);
        if (StringUtils.isEmpty(extensionName)) {
            return;
        }

        switch (extensionName) {
            case "java":
                loadJavaFile(file, loader);
                break;
            case "class":
                loadClassFile(file, loader);
                break;
            case "jar":
                loadJarFile();
                break;
            default:
                log.error("unknown file type,will ignore file:{}", file.getName());
        }
    }

    private static void loadJavaFile(File file, ScriptLoader loader) {
        loader.loadJavaFile(file);
    }

    private static void loadJarFile() {

    }

    private static void loadClassFile(File file, ScriptLoader loader) {
        try {
            loader.load("", new byte[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
package com.github.mrlawrenc;

import com.github.mrlawrenc.boothelp.JfxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : MrLawrenc
 * date  2020/9/21 21:18
 * <p>JCTools库
 * spring boot 辅助启动类 jfx
 * <pre>
 *     1. 先启动jfx的App
 *     2. 手动启动spring容器
 *     3. 容器启动完成之后开始加载用户的jfx逻辑
 * </pre>
 */
@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        Application.launch(JfxApplication.class, args);
    }
}
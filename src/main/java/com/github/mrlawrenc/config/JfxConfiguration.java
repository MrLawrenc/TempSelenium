package com.github.mrlawrenc.config;

import com.github.mrlawrenc.entity.ProductConfig;
import com.github.mrlawrenc.entity.conf.CaseConfig;
import com.github.mrlawrenc.storage.AbstractJfxStorage;
import com.github.mrlawrenc.storage.FileStorageImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:40
 * <p>
 * jfx配置类
 */
@Configuration
@Component
public class JfxConfiguration {

    @Bean
    public AbstractJfxStorage<ProductConfig, CaseConfig> storage() {
        return new FileStorageImpl();
    }
}
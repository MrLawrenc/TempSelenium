package com.github.mrlawrenc.storage;

import com.github.mrlawrenc.entity.conf.CaseConfig;
import com.github.mrlawrenc.entity.ProductConfig;

import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:42
 * <p>
 * 文件存储
 */
public class FileStorageImpl extends AbstractJfxStorage<ProductConfig, CaseConfig> {
    @Override
    public boolean save(ProductConfig source) {
        return false;
    }

    @Override
    public List<CaseConfig> list() {
        return null;
    }
}
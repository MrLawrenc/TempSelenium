package com.github.mrlawrenc.storage;

import com.github.mrlawrenc.entity.conf.CaseConfig;

import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:42
 * <p>
 * PgSql存储
 */
public class PgStorageImpl extends AbstractJfxStorage<CaseConfig> {
    @Override
    public boolean save(CaseConfig source) throws Exception {
        return false;
    }

    @Override
    public List<CaseConfig> list() {
        return null;
    }

    @Override
    public CaseConfig byCaseName(String caseName) throws Exception {
        return null;
    }

    @Override
    public CaseConfig update(CaseConfig caseConfig) throws Exception {
        return null;
    }
}
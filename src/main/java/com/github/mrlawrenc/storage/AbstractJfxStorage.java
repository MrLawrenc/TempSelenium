package com.github.mrlawrenc.storage;

import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:46
 * <p>
 * jfx存储
 */
public abstract class AbstractJfxStorage<R> implements IStorage<R> {

    public abstract List<R> list();

    public abstract R byCaseName(String caseName) throws Exception;

    public abstract R update(R r) throws Exception;
}
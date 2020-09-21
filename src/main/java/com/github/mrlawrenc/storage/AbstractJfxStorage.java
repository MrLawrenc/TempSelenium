package com.github.mrlawrenc.storage;

import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:46
 * <p>
 * jfx存储
 */
public abstract class AbstractJfxStorage<T,R> implements IStorage<T> {

    public abstract List<R> list();
}
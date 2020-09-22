package com.github.mrlawrenc.storage;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:42
 * <p>
 * 存储
 */
public interface IStorage<T> {
    /**
     * 存储方法
     *
     * @param source 被存储资源
     * @return true 存储成功
     */
    boolean save(T source) throws Exception;
}
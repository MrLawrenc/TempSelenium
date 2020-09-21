package com.github.mrlawrenc.handler;

import com.github.mrlawrenc.entity.conf.CaseConfig;

/**
 * @author hz20035009-逍遥
 * date   2020/9/14 20:18
 * <p>
 * 值生成器,可以借助spi机制自由扩展
 */
public interface AbstractGenerateValueHandler {

    /**
     * 生成目标值
     * <pre>
     *      1. AbstractGenerateValueHandler#param1,param2,param3
     * </pre>
     *
     * @param caseConfig 当前使用的测试用例
     * @param args       其余传入值生成器的扩展参数
     * @return 值
     */
    String generate(CaseConfig caseConfig, String... args);
}
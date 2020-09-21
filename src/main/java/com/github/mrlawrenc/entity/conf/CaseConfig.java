package com.github.mrlawrenc.entity.conf;

import lombok.Data;

import java.util.List;

/**
 * @author : MrLawrenc
 * date  2020/9/21 22:54
 * <p>
 * 每一个测试用例对应的对象
 */
@Data
public class CaseConfig {

    private Integer id;
    /**
     * 用例名
     */
    private String caseName;

    /**
     * 用例命令合集
     */
    private List<StepCommand> stepCommand;

    /**
     * 扩展字段
     */
    private String expandField;
}
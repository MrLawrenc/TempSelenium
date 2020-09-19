package com.swust.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MrLawrenc
 * date  2020/9/17 21:54
 * 核保前配置
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PreCheckConfig {

    /**
     * 元素位置,目前只支持xpath定位
     */
    private String location;
    /**
     * 操作
     */
    private String action;

    /**
     * 可能不存在，如当action为点击事件时,一个string集合，以#分割
     */
    private String values;

    /**
     * 脚本，目前只支持java
     */
    private String script;
}
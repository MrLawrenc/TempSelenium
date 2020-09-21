package com.github.mrlawrenc.entity;

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
     * 一个string集合，以#分割，如组合键 CTRL#T
     * <p>
     * 可能不存在，如当action为点击事件时。
     */
    private String values;

    /**
     * value脚本，当value不为定值时该字段才会有值，目前只支持java脚本，有如下格式：
     * <pre>
     *      1.ChineseIDCardNumberGenerator#getInstance()#generate() -> 代表执行ChineseIDCardNumberGenerator.getInstance().generate()方法
     *      2. 代码片段，程序会编译该片段，并执行。value的值=返回值的toString
     *      Random r=new Random();
     *      return r.nextInt(100)+1;
     *      3.
     *
     * </pre>
     */
    private String script;
    /**
     * 寻找元素的最大等待时间,单位毫秒
     */
    private String wait;
    /**
     * 该步的命令描述
     */
    private String desc;
}
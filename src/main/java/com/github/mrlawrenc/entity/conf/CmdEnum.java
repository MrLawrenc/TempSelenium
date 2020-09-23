package com.github.mrlawrenc.entity.conf;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : MrLawrenc
 * date  2020/9/23 21:56
 */
@AllArgsConstructor
@Getter
public enum CmdEnum {

    SLEEP("sleep", false, "睡眠"), GET("get", false, "打开一个链接"), SWITCH("switch", false, "切换窗口"), CLICK("click", true, "点击事件"), SEND_KEYS("sendKeys", true, "赋值事件"), BOARD("board", true, "键盘事件");
    private String cmd;
    /**
     * 是否有事件操作
     */
    private boolean action;
    private String desc;
}
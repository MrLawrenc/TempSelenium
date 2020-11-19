package com.github.mrlawrenc.utils;

import java.awt.*;

/**
 * 操作者单例工厂
 *
 * @author hz20035009-逍遥
 * date   2020/11/19 9:56
 * 操作者单例工厂
 */
public final class OperatorSingletonFactory {
    private static Robot robot;

    public synchronized static Robot createRobot() {
        try {
            return robot == null ? (robot = new Robot()) : robot;
        } catch (AWTException e) {
            throw new RuntimeException("create robot fail", e);
        }
    }
}
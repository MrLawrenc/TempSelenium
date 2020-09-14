package com.swust.handler;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Random;

/**
 * @author hz20035009-逍遥
 * date   2020/9/14 20:23
 * <p>
 * 身份证生成器
 */
public class IdCardGenerate extends GenerateValueHandler {

    @Override
    public String generateV() {
        Map<String, String> code = getAreaCode();
        String areaCode = code.keySet().toArray(new String[0])[RandomUtils
                .nextInt(0, code.size())]
                + StringUtils.leftPad((RandomUtils.nextInt(0, 9998) + 1) + "", 4,
                "0");

        String birthday = defaultRandomDate();
        String randomCode = String.valueOf(1000 + RandomUtils.nextInt(0, 999))
                .substring(1);
        String pre = areaCode + birthday + randomCode;
        String verifyCode = getVerifyCode(pre);
        return pre + verifyCode;
    }

    static String defaultRandomDate() {
        Random random = new Random();
        int month = random.nextInt(12)+1;
        return random.nextInt(30) + 1970 + "" + (month < 10 ? "0" + month : month + "") + (random.nextInt(28) + 1);
    }

    /**
     * 自定义身份证生日 yyyyMMdd
     */
    static String customBirthday(int year, int month, int dayOfMonth) {
        return year + (month < 10 ? "0" + month : month + "") + (dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth);
    }

    private static String getVerifyCode(String cardId) {
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        int tmp = 0;
        for (int i = 0; i < Wi.length; i++) {
            tmp += Integer.parseInt(String.valueOf(cardId.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }

        int modValue = tmp % 11;
        String strVerifyCode = ValCodeArr[modValue];

        return strVerifyCode;
    }

    private static Map<String, String> getAreaCode() {
        final Map<String, String> map = Maps.newHashMap();
        map.put("11", "北京");
        map.put("12", "天津");
        map.put("13", "河北");
        map.put("14", "山西");
        map.put("15", "内蒙古");
        map.put("21", "辽宁");
        map.put("22", "吉林");
        map.put("23", "黑龙江");
        map.put("31", "上海");
        map.put("32", "江苏");
        map.put("33", "浙江");
        map.put("34", "安徽");
        map.put("35", "福建");
        map.put("36", "江西");
        map.put("37", "山东");
        map.put("41", "河南");
        map.put("42", "湖北");
        map.put("43", "湖南");
        map.put("44", "广东");
        map.put("45", "广西");
        map.put("46", "海南");
        map.put("50", "重庆");
        map.put("51", "四川");
        map.put("52", "贵州");
        map.put("53", "云南");
        map.put("54", "西藏");
        map.put("61", "陕西");
        map.put("62", "甘肃");
        map.put("63", "青海");
        map.put("64", "宁夏");
        map.put("65", "新疆");
        map.put("71", "台湾");
        map.put("81", "香港");
        map.put("82", "澳门");
        map.put("91", "国外");

        return map;
    }
}
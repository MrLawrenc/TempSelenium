package com.swust.entity;

import com.swust.controller.PreCheckConfig;
import lombok.Data;

/**
 * @author : MrLawrenc
 * date  2020/9/18 23:03
 * <p>
 * 产品配置
 */
@Data
public class ProductConfig {

    /**
     * 用例名
     */
    private String caseName;

    private Integer companyId;
    private Integer productId;

    /**
     * p 版地址
     */
    private String previewUrl = "https://lmy25.wang/";
    /**
     * 生产地址
     */
    private String productionUrl = "https://lmy25.wang/";

    private PreCheckConfig preCheckConfig;
}
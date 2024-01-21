package io.bhex.broker.admin.constants;

import java.math.BigDecimal;

/**
 * @Description: 业务使用的一些常量值
 * @Date: 2018/9/22 下午12:14
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public class BizConstant {
    /** 券商费率设置费率默认值 */
    public static final BigDecimal DEFAULT_TRADE_FEE_RATE = new BigDecimal("0.002");

    public static final BigDecimal TRADE_FEE_RATE_PRECISION = new BigDecimal("100000");

    //设置费率折扣时最小费率
    public static final BigDecimal ADJUST_MIN_TRADE_FEE_RATE = new BigDecimal("0.0001");
    /** 一个券商操作解绑次数限制 */
    public static final Integer UNBIND_GA_MAX_TIMES = 50;

    public static final Integer NOTIFY_TYPE_KYC = 0;
    public static final Integer NOTIFY_TYPE_OTC = 1;
    public static final Integer NOTIFY_TYPE_WITHDRAW = 2;

    //期货精度
    public static final Integer FUTURES_AMOUNT_PRECISION = 8;
    public static final Integer FUTURES_QUANTITY_PRECISION = 3;

    public static final String INVITE_POSTER_TEMPLATE = "invite_poster_template";

    public static final String SITE_LANGUAGE_GROUP = "site.language";
    public static final String ODS_DATA_GROUP = "ods.data.group";

    public static final String CUSTOM_CONFIG_GROUP = "custom.config.group";
    public static final String INDEX_NEW_VERSION = "index.new.version"; //首页新版本开关
    public static final String CUSTOM_QUOTE_GROUP = "customer.quote";

    public static final String APP_DOWNLOAD_URL_GROUP = "app.download.url";
    public static final String TESTFIGHT_URL = "testfight.download.url";
    public static final String APP_STORE_URL = "appstore.download.url";
    public static final String GOOGLE_PLAY_URL = "googleplay.download.url";
}

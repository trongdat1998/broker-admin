package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:55 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerWholeConfigDTO {

    private String sslCrtFile;
    private String sslKeyFile;
    private String logo;
    private String domainHost;
    private String internalHost;
    private String copyright;
    private String zendesk;
    private String facebook;
    private String twitter;
    private String telegram;
    private String reddit;
    private String wechat;
    private String weibo;
    private String favicon;

    //扩展联系方式
    private String medium;
    private String linkedin;
    private String github;
    private String discord;
    private String line;
    private String biyong;
    private String qq;
    private String coinmarketcap;
    private String coingecko;
    private String myToken;
    private String feixiaohao;


    private List<BrokerLocalConfigDTO> localConfigList;


    private List<FootConfig> footConfigList;

    private List<HeadConfig> headConfigList;

    private FunctionConfig functionConfig;

    private String logoUrl;

    @Data
    public static class FootConfig{
        private Integer enable;

        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String locale;
        private List<Object> list;
    }

    @Data
    public static class HeadConfig{
        private Integer enable;

        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String locale;
        private List<Object> list;
    }

    @Data
    public static class FunctionConfig{
        private Boolean guild;
        private Boolean vol;
        private Boolean loan;
        private Boolean activity;
        private Boolean coupon;
        private Boolean explore;
        private Boolean bonus;
        private Boolean pointcard;
        private Boolean exchange;
        private Boolean futures;
        private Boolean otc;
        private Boolean option;
    }
}

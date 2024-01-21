package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:55 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveBrokerWholeConfigPO {

    private Long brokerId;
    private String sslCrtFile;
    private String sslKeyFile;
    private String logo;
    private String domainHost;
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

    private List<BrokerLocalConfigPO> localConfigList;

    private List<FootConfig> footConfigList;

    private List<HeadConfig> headConfigList;

    private String logoUrl;

    @Data
    public static class FootConfig {
        private Integer enable;
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String locale;
        private List<Object> list;
    }


    @Data
    public static class HeadConfig {
        private Integer enable;
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String locale;
        private List<Object> list;
    }
}

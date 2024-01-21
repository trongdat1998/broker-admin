package io.bhex.broker.admin.controller.dto;


import com.google.common.base.Splitter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCompetitionDTO {

    private Long id;

    //编码
    private String code;

    //开始时间
    private String begin;

    //结束时间
    private String end;

    //合约id
    private String contractId;

    //排行榜类型
    private List<Integer> rankTypes;

    //榜单排名数量
    private int rankNumber;

    //状态 0初始化 1进行中 2已结束 3已失效
    private int status;

    //参赛资格
    private Qualify qualify;

    //扩展信息
    private List<Extend> extendList;

    private Integer isReverse; //0单个普通合约大赛 1正向组团合约大赛

    public long getIdSafe() {
        if (Objects.isNull(id)) {
            return 0L;
        }
        return id.longValue();
    }
    
    @Builder(builderClassName = "builder")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Qualify {
        private String position;

        public String getPositionSafe() {
            if (StringUtils.isBlank(position)) {
                return "0";
            }

            return this.position;
        }

    }

    @Builder(builderClassName = "builder")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extend {
        private String language;  //语言
        private String pcBanner;  //pc banner
        private String appBanner; //app banner
        private String description; //描述

        public String convertLanguage() {
            if (StringUtils.isBlank(this.language)) {
                return "en_US";
            }

            List<String> list = Splitter.on("-").trimResults().omitEmptyStrings().splitToList(this.language);
            if (list.size() != 2) {
                throw new IllegalArgumentException("Invalid language format,language=" + this.language);
            }

            return list.get(0) + "_" + list.get(1).toUpperCase();
        }
    }

}

package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class BaseConfigPO {

    private Long id;

    @NotEmpty
    private String group;

    @Length(max = 64)
    private String key;

    private Object value;

    private String extraValue;

    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String language;

    private Boolean switchStatus;

    private String symbol;

    private String token;

    private Long startTime;
    private Long endTime;

    private int status = 1;

    private int pageSize = 0;
    private long fromId = 0;

    @Length(max = 64)
    private String remark;

    //---------

    private String opPlatform;

    private Boolean withLanguage;

    //private String opType;


    public static final String OP_TYPE_COMMOM = "common";
    public static final String OP_TYPE_SYMBOL = "symbol";
    public static final String OP_TYPE_TOKEN = "token";

    public static final String OP_PLATFORM_BH = "bh";
    public static final String OP_PLATFORM_BROKER = "broker";

}

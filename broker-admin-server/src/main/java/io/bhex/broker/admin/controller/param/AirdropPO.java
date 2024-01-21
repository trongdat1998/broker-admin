package io.bhex.broker.admin.controller.param;

import com.google.common.base.Strings;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import io.bhex.bhop.common.util.validation.UrlValid;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 09/11/2018 2:53 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class AirdropPO {

    public static final Integer AIRDROP_TYPE_CANDY = 1;
    public static final Integer AIRDROP_TYPE_FORK = 2;

    public static final Integer USER_TYPE_ALL = 1;
    public static final Integer USER_TYPE_SPECIAL = 2;


    private Integer lockModel; //是否锁仓模式 1-是 0-否

    private Integer tmplModel; //是否模板模式 1-是 0-否
    private String sequenceId;

    private Integer type; //空头类型 1免费空投 2分叉空投

    @CommonInputValid
    private String title = "";

    @CommonInputValid(maxLength = 256)
    private String description = "";

    private Long brokerId;

    private Long accountId; //发放空头币的账号id

    private Integer userType; //空头用户类型，1全部 2指定user_id

    private String userAccountIds = ""; //用户id字符串
    // 空投币种数量与持有币种数量的比例关系决定到底给用户空投多少币
    // 如果为 免费空投，则直接按空投数量进行空投
    @TokenValid(allowEmpty = true)
    private String airdropTokenId = ""; //空投币种
    @Min(value = 0)
    private BigDecimal airdropTokenNum = new BigDecimal(0); //空投币种的数量
    @TokenValid(allowEmpty = true)
    private String haveTokenId = ""; //空投比例需要用户持有的币种
    private BigDecimal haveTokenNum = new BigDecimal(1); //用户持有设定币的数量
    private BigDecimal transferAssetAmount = new BigDecimal(0); //总空投数量
    private long snapshotTime = 0L; //快照时间
    private long userCount = 0L; //发送的用户总数
    private boolean isScheduleJod = true; //是否为定时任务
    private long airdropTime = 0L; //定时任务时，发送时间
    private String failedReason = ""; //空投失败说明
    private long adminId;
    @UrlValid(allowEmpty = true)
    private String tmplUrl; //空投名单地址

    private Integer authType;
    private String verifyCode;

    public String getUserIds(){
        return Strings.nullToEmpty(this.userAccountIds);
    }

}

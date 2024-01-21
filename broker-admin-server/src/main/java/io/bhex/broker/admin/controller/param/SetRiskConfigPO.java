package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetRiskConfigPO {

    @NotNull
    String withdrawLine;
    @NotNull
    String warnLine;
    @NotNull
    String appendLine;
    @NotNull
    String stopLine;
    //最大借币限额
    String maxLoanLimit = "0";
    //通知类型  1手机，2邮箱
    Integer notifyType = 0;

    String notifyNumber = "";

    String maxLoanLimitVip1 = "0";

    String maxLoanLimitVip2 = "0";

    String maxLoanLimitVip3 = "0";


}

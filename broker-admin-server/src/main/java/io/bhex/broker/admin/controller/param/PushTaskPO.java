package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 11:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushTaskPO {
    @NotEmpty
    @CommonInputValid(maxLength = 120)
    public String name;

    public Long taskId = 0L;
    //推送类型 1：全部  9：指定UID
    @NotNull
    public Integer rangeType;

    public String userIds = "";
    //推送类别1.通知消息 2.透传
    public Integer pushCategory = 1;

    //计划执行类型，0.一次性任务、1.周期性每天执行 2.周期性每周执行
    public Integer cycleType = 0;

    public Integer cycleDayOfWeek = 0;

    public Long firstActionTime = 0L;

    /**
     * 允许不传
     */
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    public String defaultLocale = "";

    public List<PushFilterCondition> filterConditions = new ArrayList<>();

    @NotEmpty
    public List<@Valid PushTaskLocaleDetail> pushTaskLocaleDetails;
}

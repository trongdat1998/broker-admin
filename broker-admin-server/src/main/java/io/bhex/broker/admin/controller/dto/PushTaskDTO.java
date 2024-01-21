package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:00
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class PushTaskDTO {
    public String name;
    public Long taskId;
    //推送类型 1：全部  2：指定UID
    public Integer rangeType;
    public List<Long> userIds;
    //推送类别1.通知消息 2.透传
    public Integer pushCategory;
    //1：点击后打开特定URL   2：点击后打开应用App
    public Integer clickType;
    //计划执行类型，0.一次性任务、1.周期性每天执行 2.周期性每周执行
    public Integer cycleType;

    public Integer cycleDayOfWeek;

    public Long firstActionTime;

    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String defaultLocale;

    public List<PushFilterConditionDTO> filterConditions;

    public List<PushTaskLocaleDetailDTO> pushTaskLocaleDetails;
}

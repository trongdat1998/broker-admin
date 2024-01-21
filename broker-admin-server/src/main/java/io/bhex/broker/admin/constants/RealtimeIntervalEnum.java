package io.bhex.broker.admin.constants;


/**
 * @author wangshouchao
 */
public enum RealtimeIntervalEnum {
    /**
     * D1 基于utc时间
     * D1_8 基于utc+8时间
     * NONE 默认的24小时
     */
    D1("1d"), D1_8("1d+8"), NONE("24h");
    private final String interval;

    RealtimeIntervalEnum(String interval){
        this.interval = interval;
    }

    public String getInterval() {
        return interval;
    }

    public static RealtimeIntervalEnum intervalOf(String interval) {
        for (RealtimeIntervalEnum realtimeIntervalEnum : values()) {
            if (realtimeIntervalEnum.interval.equals(interval)) {
                return realtimeIntervalEnum;
            }
        }
        return null;
    }

}

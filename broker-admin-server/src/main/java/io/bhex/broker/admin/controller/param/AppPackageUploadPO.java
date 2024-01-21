package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date: 2019/8/8 下午3:15
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class AppPackageUploadPO {

    private Integer type;

    private String androidDownloadUrl;

    private String iosDownloadUrl;

    private String googlePlayDownloadUrl;

    private String appStoreDownloadUrl;

    private String testflightDownloadUrl;

    private String androidAppId;

    private String iosAppId;

    private String androidAppVersion;

    private String iosAppVersion;


    private List<DownloadConfigLocalePO> localeInfo = new ArrayList<>();

    @Data
    public static class DownloadConfigLocalePO {

        @NotNull
        private String androidGuideImageUrl;

        @NotNull
        private String iosGuideImageUrl;

        @NotNull
        private String downloadWebUrl;

        @NotNull
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;

        private Boolean enable;

    }

}

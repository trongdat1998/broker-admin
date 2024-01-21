package io.bhex.broker.admin.controller.param;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

@Data
public class CalculateActivityResultPO {
    @NotEmpty
    private String projectId;
    @Nullable
    private String actualOfferingsVolume;

    private final static String ZERO="0";

    public String getActualOfferingsVolumeNumber(){
        if(StringUtils.isBlank(this.actualOfferingsVolume)){
            return ZERO;
        }

        return this.actualOfferingsVolume;
    }

    public long getProjectIdNumber(){
        if(StringUtils.isBlank(this.projectId)){
            return 0;
        }

        return Integer.parseInt(this.projectId);
    }
}

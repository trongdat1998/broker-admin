package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:39 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ShareConfigPO {

    private Long brokerId;
    @NotNull
    private String logoUrl;
    @NotNull
    private String watermarkImageUrl;
    private Long adminUserId;
    private List<ShareConfigLocalePO> localeInfo;
}

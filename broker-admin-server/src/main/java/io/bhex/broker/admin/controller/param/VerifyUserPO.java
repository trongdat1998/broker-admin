package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 09/09/2018 2:57 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class VerifyUserPO {
    @NotNull
    private Long userVerifyId;

    private Boolean verifyPassed;

    private Long reasonId;

    @CommonInputValid
    private String remark;




    public enum UserVerifyStatus {
        UNDER_REVIEW(1),
        PASSED(2),
        REFUSED(3);

        private int value;

        UserVerifyStatus(Integer value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }
}

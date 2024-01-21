/*************************************
 * @项目名称: saas-admin-parent
 * @文件名称: DepositReceiptExecPO
 * @Date 2019/12/11
 * @Author fred.wang@bhex.io
 * @Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/
package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created on 2019/12/11
 *
 * @author wangxuefei
 */
@Data
public class DepositReceiptExecPO {

    @NotNull
    private Long orderId;
    @NotNull
    private Long accountId;

    @NotNull
    private Boolean execReceipt;

    @CommonInputValid
    private String remark;
}

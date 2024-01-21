/*************************************
 * @项目名称: saas-admin-parent
 * @文件名称: AskWalletAddressDTO
 * @Date 2019/12/13
 * @Author fred.wang@bhex.io
 * @Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/
package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2019/12/13
 *
 * @author wangxuefei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AskWalletAddressDTO {

    private Boolean isWalletAddress;

    private String brokerUserId;

    private Long accountId;
}

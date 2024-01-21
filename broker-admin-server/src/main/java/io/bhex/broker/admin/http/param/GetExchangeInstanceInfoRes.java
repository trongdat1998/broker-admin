package io.bhex.broker.admin.http.param;

import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.http.param
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 8:27 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
public class GetExchangeInstanceInfoRes {

    private Long exchangeId;
}

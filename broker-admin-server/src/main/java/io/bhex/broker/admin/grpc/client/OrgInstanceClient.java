package io.bhex.broker.admin.grpc.client;

import io.bhex.bhop.common.dto.param.BrokerInstanceRes;
import io.bhex.bhop.common.dto.param.ExchangeInstanceRes;
import java.util.List;
import javax.annotation.Nullable;

/**
 * @author wangsc
 * @description orgInstance服务
 * @date 2020-7-3 2:53
 */
public interface OrgInstanceClient {
    /**
     * 立即刷新远程org实例缓存
     * @return
     */
    void refreshOrgInstance();
    /**
     * 获取交易所实例(需要处理null)
     * @return
     */
    @Nullable
    List<ExchangeInstanceRes> getExchangeInstanceList();
    /**
     * 获取broker实例(需要处理null)
     * @return
     */
    @Nullable
    List<BrokerInstanceRes> getBrokerInstanceList();
}

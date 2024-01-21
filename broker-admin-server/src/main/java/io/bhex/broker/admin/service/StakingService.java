package io.bhex.broker.admin.service;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.StakingProductDTO;
import io.bhex.broker.admin.controller.dto.StakingProductPermissionDTO;
import io.bhex.broker.admin.controller.dto.StakingProductProfileDTO;
import io.bhex.broker.admin.controller.dto.StakingProductRebateDTO;
import io.bhex.broker.admin.controller.param.StakingProductListPO;
import io.bhex.broker.admin.controller.param.StakingProductPO;

import java.util.List;

public interface StakingService {

    /**
     * 保存产品信息
     *
     * @param stakingProductPO
     * @return
     */
    ResultModel saveProduct(StakingProductPO stakingProductPO);

    /**
     * 获取产品详情信息
     *
     * @param orgId
     * @param productId
     * @return
     */
    StakingProductDTO getProductDetail(Long orgId, Long productId);

    /**
     * 获取产品列表
     *
     * @param orgId
     * @param language
     * @param stakingProductListPO
     * @return
     */
    List<StakingProductProfileDTO> getProductList(Long orgId, String language, StakingProductListPO stakingProductListPO);

    /**
     * 上下架产品
     *
     * @param orgId
     * @param productId
     * @param status
     * @return
     */
    boolean onlineProduct(Long orgId, Long productId, Integer status);

    StakingProductPermissionDTO getBrokerProductPermission(Long orgId);


    List<StakingProductRebateDTO> queryBrokerProductUndoRebate(Long orgId, String language, Integer productType);

    List<StakingProductRebateDTO> queryBrokerProductHistoryRebate(Long orgId, String language, Integer productType, Integer pageNo, Integer size);

    /**
     * 取消派息
     *
     * @param orgId
     * @param productId
     * @param productRebateId
     * @return
     */
    Boolean cancelDividend(Long orgId, Long productId, Long productRebateId);

    /**
     * 开始派息
     *
     * @param orgId
     * @param productId
     * @param productRebateId
     * @return
     */
    Integer dividendTransfer(Long orgId, Long productId, Long productRebateId);
}

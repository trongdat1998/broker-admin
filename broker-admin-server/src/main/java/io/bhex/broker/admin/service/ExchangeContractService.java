package io.bhex.broker.admin.service;

import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.controller.dto.ExchangeContractDTO;
import io.bhex.broker.admin.controller.param.ExchangeContractPO;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:13 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface ExchangeContractService {

    /**
     * 合作中的合同
     *
     * @param brokerId
     * @param current
     * @param pageSize
     * @return
     */
    PaginationVO<ExchangeContractDTO> listExchangeContract(Long brokerId, Integer current, Integer pageSize);

    /**
     * 重新打开合作
     *
     * @param brokerId
     * @param cotractId
     * @param exchangeId
     * @param adminUserId
     * @return
     */
    Boolean reopenExchangeContract(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId);

    /**
     * 对方重新打开合作，待我方确认
     * @param brokerId
     * @param cotractId
     * @return
     */
//    Boolean otherReopenContract(Long brokerId, Long cotractId);

    /**
     * 关闭合作
     *
     * @param brokerId
     * @param cotractId
     * @param exchangeId
     * @param adminUserId
     * @return
     */
    Boolean closeExchangeContract(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId);


    /**
     * 合作请求列表
     *
     * @param brokerId
     * @param current
     * @param pageSize
     * @return
     */
    PaginationVO<ExchangeContractDTO> listApplication(Long brokerId, Integer current, Integer pageSize);

    /**
     * 同意合作
     *
     * @param brokerId
     * @param cotractId
     * @param exchangeId
     * @param adminUserId
     * @return
     */
    Boolean enableApplication(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId);

    /**
     * 拒绝合作
     *
     * @param brokerId
     * @param cotractId
     * @param exchangeId
     * @param adminUserId
     * @return
     */
    Boolean rejectApplication(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId);

    /**
     * 新建申请（对方调用，创建申请待我方确认）
     *
     * @param param
     * @return
     */
    Boolean addApplication(ExchangeContractPO param);

    /**
     * 编辑合作的备注信息
     *
     * @param param
     * @return
     */
    Boolean editContactInfo(ExchangeContractPO param);

    /**
     * 展示全部的合作机构。合作交易所的接口用到，展示合作的交易所名称
     *
     * @param brokerId
     * @return
     */
    List<ContractExchangeInfo> listALlExchangeContractInfo(Long brokerId);

}

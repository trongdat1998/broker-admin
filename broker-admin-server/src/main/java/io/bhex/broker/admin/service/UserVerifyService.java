package io.bhex.broker.admin.service;

import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyHistoryDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyReasonDTO;
import io.bhex.broker.admin.controller.param.ListUpdateUserByDatePO;
import io.bhex.broker.admin.controller.param.QueryUserVerifyPO;
import io.bhex.broker.grpc.admin.*;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:14 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface UserVerifyService {

    //PaginationVO<UserVerifyDTO> queryUnverifyiedUser(Integer current, Integer pageSize, Long brokerId, Long userId, String locale);

    PaginationVO<UserVerifyDTO> queryUserVerifyList(QueryUserVerifyPO po, String locale, long brokerId);

    PaginationVO<UserVerifyDTO> queryUnverifyiedUser(QueryUserVerifyPO po, String locale, long brokerId);

    UserVerifyDTO getVerifyUserById(Long userVerifyId, Long brokerId, String locale, boolean decryptUrl);

    Boolean updateVerifyUser(Long brokerId, Long adminUserId, Long userVerifyId, Integer verifyStatus, Long reasonId, String remark);

    List<UserVerifyReasonDTO> listVerifyReason(String locale);

    List<UserVerifyHistoryDTO> listVerifyHistory(Long brokerId, String locale, Long userVerifyId);

    List<BrokerUserDTO> listUpdateUserByDate(ListUpdateUserByDatePO param);


    AddBrokerKycConfigReply addBrokerKycConfig(AddBrokerKycConfigRequest request);

    DegradeBrokerKycLevelReply degradeBrokerKycLevel(long brokerId, long userId);

    OpenThirdKycAuthReply openThirdKycAuth(long brokerId, long userId);

    List<BrokerKycConfig> getBrokerKycConfigs(long brokerId);
}

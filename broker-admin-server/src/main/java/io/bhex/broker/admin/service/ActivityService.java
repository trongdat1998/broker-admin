package io.bhex.broker.admin.service;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ActivityInfoDTO;
import io.bhex.broker.admin.controller.dto.ActivityProfileDTO;
import io.bhex.broker.admin.controller.dto.ActivityPurchaseInfoDTO;
import io.bhex.broker.admin.controller.dto.IEOUploadDTO;
import io.bhex.broker.admin.controller.dto.IEOWhiteListDTO;
import io.bhex.broker.admin.controller.dto.LockInterestOrderInfoDto;
import io.bhex.broker.admin.controller.param.IEOProjectPO;
import io.bhex.broker.admin.controller.param.IEOWhiteListPO;
import io.bhex.broker.admin.controller.param.QueryActivityOrderPO;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoResponse;

public interface ActivityService {

    ResultModel createIEOProject(Long brokerId, IEOProjectPO project);

    Pair<List<ActivityProfileDTO>, Integer> listActivity(long brokerId, int pageNo, int size,
                                                         List<Integer> typesInt, String language);

    IEOProjectPO findActivity(Long id);

    ActivityPurchaseInfoDTO calculateActivityResult(long projectId, long brokerId, String language, String actualOfferingsVolume);

    ActivityPurchaseInfoDTO findActivityResult(long projectId, long brokerId, String language);

    ResultModel confirmResult(String language, Long projectId, Long brokerId);

    boolean onlineStatus(Long projectId, Long brokerId, Integer isShow);

    List<LockInterestOrderInfoDto> adminQueryAllActivityOrderInfo(QueryActivityOrderPO queryActivityOrderPO);

    ResultModel<IEOWhiteListDTO> queryIeoWhiteList(IEOWhiteListPO whiteList);

    ResultModel saveIeoWhiteList(IEOWhiteListPO whiteList);

    ResultModel modifyActivityOrderInfo(Long orgId, Long projectId, String url, List<IEOUploadDTO> dtoList);

    ActivityInfoDTO queryActivityProjectInfo(long orgId, long projectId);
}

package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.AirdropDTO;
import io.bhex.broker.admin.controller.dto.AirdropTmplDTO;
import io.bhex.broker.admin.controller.param.AirdropPO;
import io.bhex.broker.admin.controller.param.QueryAirdropPO;
import io.bhex.broker.grpc.account.GetBrokerAccountListResponse;
import io.bhex.broker.grpc.admin.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 07/11/2018 6:15 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AirdropService {

   Boolean airdropProcess(Long airdropId, Long brokerId);

   List<AirdropInfo> listScheduleAirdrop();

   Combo2<Boolean, List<Long>> createAirdrop(AirdropPO param, AdminUserReply adminUser);

   Boolean retryAirdrop(Long airdropId, Long brokerId);

   Boolean closeAirdrop(Long airdropId, Long brokerId);

   List<AirdropDTO> queryAirdropInfo(QueryAirdropPO param);

   AirdropInfo getAirdropInfo(Long airdropId, Long brokerId);

   Boolean lockAndAirdrop(Long airdropId, Long brokerId);

   // delete
   Integer batchTransferProcess(AirdropInfo airdropInfo);

   GetBrokerAccountListResponse getAllUserPageable(Long registerTime, Long fromId, Long brokerId, Integer limit);

   List<Long> getErrorUserIds(long brokerId, List<Long> userIds);


   List<AirdropTmplDTO> convertTmpl(XSSFWorkbook workbook);

   List<TmplRecord> listTmplRecords(long brokerId, long airdropId, long groupId, String orderBy, int limit);

   TokenDetail getBrokerTokenDetail(String tokenId, Long brokerId);
}

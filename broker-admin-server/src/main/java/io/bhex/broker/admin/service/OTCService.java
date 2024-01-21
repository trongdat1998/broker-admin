package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.OTCPaymentDTO;
import io.bhex.broker.admin.controller.dto.OtcWhiteUserDTO;

import java.util.List;

public interface OTCService {

    List<OtcWhiteUserDTO> listUser(int pageSize, int pageNo, List<Long> userIds, Long orgId);

    List<OTCPaymentDTO> listPayments(long orgId,long accountId);

    boolean modifyNickname(long orgId,long accountId,String nickname);
}

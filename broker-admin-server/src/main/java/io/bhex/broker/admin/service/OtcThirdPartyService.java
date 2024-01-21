package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.OtcThirdPartyDTO;
import io.bhex.broker.admin.controller.dto.OtcThirdPartyDisclaimerDTO;
import io.bhex.broker.admin.controller.dto.OtcThirdPartyOrderDTO;
import io.bhex.broker.admin.controller.param.OtcThirdPartyDisclaimerUpdatePO;
import io.bhex.broker.admin.controller.param.OtcThirdPartyOrderQueryPO;
import io.bhex.broker.grpc.otc.third.party.UpdateOtcThirdPartyDisclaimerResponse;

import java.util.List;

public interface OtcThirdPartyService {

    List<OtcThirdPartyDTO> getOtcThirdParty(Long orgId);

    List<OtcThirdPartyDisclaimerDTO> queryOtcThirdPartyDisclaimer(Long orgId);

    UpdateOtcThirdPartyDisclaimerResponse updateOtcThirdPartyDisclaimer(OtcThirdPartyDisclaimerUpdatePO po, Long orgId);

    List<OtcThirdPartyOrderDTO> queryOtcThirdPartyOrders(OtcThirdPartyOrderQueryPO po, Long orgId);
}

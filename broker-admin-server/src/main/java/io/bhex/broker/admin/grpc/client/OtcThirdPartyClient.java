package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.otc.third.party.*;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: cookie.yuan
 * @CreateDate: 18/08/2020
 * @Copyright（C）: 2020 BHEX Inc. All rights reserved.
 */
public interface OtcThirdPartyClient {

    GetOtcThirdPartyResponse getOtcThirdParty(GetOtcThirdPartyRequest request);

    QueryOtcThirdPartyDisclaimerResponse queryOtcThirdPartyDisclaimer(QueryOtcThirdPartyDisclaimerRequest request);

    UpdateOtcThirdPartyDisclaimerResponse updateOtcThirdPartyDisclaimer(UpdateOtcThirdPartyDisclaimerRequest request);

    QueryOtcThirdPartyOrdersResponse queryOtcThirdPartyOrdersByAdmin(QueryOtcThirdPartyOrdersByAdminRequest request);

}

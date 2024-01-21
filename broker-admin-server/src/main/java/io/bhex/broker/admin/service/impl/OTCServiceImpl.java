package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import io.bhex.broker.admin.config.AwsStorageConfig;
import io.bhex.broker.admin.controller.dto.OTCPaymentDTO;
import io.bhex.broker.admin.controller.dto.OtcWhiteUserDTO;
import io.bhex.broker.admin.grpc.client.impl.OtcClient;
import io.bhex.broker.admin.service.OTCService;
import io.bhex.broker.admin.util.OtcBaseReqUtil;
import io.bhex.ex.otc.*;
import io.bhex.ex.proto.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OTCServiceImpl implements OTCService {

    private static final Integer URL_ACCESSIBLE_SECONDS = 30 * 60; // 30分钟有效

    private static final String FILE_KEY_ACCESS_PREFIX = "/api/os/";

    @Resource
    private OtcClient otcClient;

    @Resource
    private AwsStorageConfig awsStorageConfig;

    @Override
    public List<OtcWhiteUserDTO> listUser(int pageSize, int pageNo, List<Long> userIds, Long orgId) {
        ListOtcUserRequest request = ListOtcUserRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(orgId))
                .setPageNo(pageNo)
                .setPageSize(pageSize)
                .addAllUserIds(userIds)
                .build();
        ListOtcUserResponse response = otcClient.listUser(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getUsersList())) {
            return Lists.newArrayList();
        }

        return response.getUsersList().stream().map(
                otcUser -> {
                    return OtcWhiteUserDTO.builder()
                            .userId(String.valueOf(otcUser.getUserId()))
                            .nickname(otcUser.getNickname())
                            .finishOrderfRate30Days(otcUser.getFinishOrderRate30Days())
                            .finishOrderNumber30Days(otcUser.getFinishOrderNumber30Days())
                            .accountId(otcUser.getAccountId())
                            .usdtValue24HoursBuy(otcUser.getExt().getUsdtValue24HoursBuy())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<OTCPaymentDTO> listPayments(long orgId, long accountId) {
        OTCGetPaymentRequest request=OTCGetPaymentRequest.newBuilder()
                .setAccountId(accountId)
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(orgId).build())
                .build();
        OTCGetPaymentResponse resp=otcClient.listPayment(request);
        if(resp.getResult()!= OTCResult.SUCCESS){
            log.warn("list payment fail,accountId="+accountId+",result="+resp.getResult().name());
            return Lists.newArrayList();
        }

        return resp.getPaymentTermsList().stream().map(i->
                OTCPaymentDTO.builder()
                        .accountId(accountId)
                        .realName(i.getRealName())
                        .paymentType(i.getPaymentType().getNumber())
                        .accountNo(i.getAccountNo())
                        .bankName(i.getBankName())
                        .qrcode(createAccessUrl(i.getQrcode()))
                        .visible(i.getVisible()== OTCVisibleEnum.ENABLE)
                        .build()
        ).collect(Collectors.toList());
    }


    public String createAccessUrl(String fileKey) {
        String accessKey = awsStorageConfig.getAccessOsFileKey();
        Integer expireTime = (Ints.checkedCast(System.currentTimeMillis() / 1000L) + URL_ACCESSIBLE_SECONDS);
        String url = FILE_KEY_ACCESS_PREFIX + fileKey + "?e=" + expireTime;
        String token = BaseEncoding.base64Url().encode(Hashing.sha256().hashString(accessKey + expireTime, Charset.forName("UTF-8")).asBytes());
        return url + "&token=" + token;
    }

    @Override
    public boolean modifyNickname(long orgId,long accountId,String nickname){

        OTCSetNickNameRequest request = OTCSetNickNameRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(orgId))
                .setAccountId(accountId)
                .setNickName(nickname)
                .setOrgId(orgId)
                .build();
        OTCSetNickNameResponse response = otcClient.modifyUser(request);
        if(response.getResult()==OTCResult.SUCCESS){
            return true;
        }

        log.warn("modify nickname fail,accountId={},nickname={}",accountId,nickname);
        return false;

    }
}

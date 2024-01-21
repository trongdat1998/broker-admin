package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.aspect.OtcAppealHandleAnnotation;
import io.bhex.broker.admin.aspect.OtcUnconfirmOrderAnnotation;
import io.bhex.broker.admin.controller.dto.BrokerExtDTO;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.util.OtcBaseReqUtil;
import io.bhex.broker.grpc.function.config.BrokerFunctionConfigServiceGrpc;
import io.bhex.broker.grpc.function.config.GetBrokerFunctionConfigRequest;
import io.bhex.broker.grpc.function.config.GetBrokerFunctionConfigResponse;
import io.bhex.broker.grpc.function.config.SetBrokerFunctionConfigRequest;
import io.bhex.broker.grpc.function.config.SetBrokerFunctionConfigResponse;
import io.bhex.broker.grpc.otc.AddOTCWhiteListRequest;
import io.bhex.broker.grpc.otc.AddOTCWhiteListResponse;
import io.bhex.broker.grpc.otc.DelOTCWhiteListRequest;
import io.bhex.broker.grpc.otc.DelOTCWhiteListResponse;
import io.bhex.broker.grpc.otc.GetOTCWhiteListUserRequest;
import io.bhex.broker.grpc.otc.GetOTCWhiteListUserResponse;
import io.bhex.broker.grpc.otc.OTCConfigServiceGrpc;
import io.bhex.ex.otc.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lizhen
 * @date 2018-11-09
 */
@Slf4j
@Service
public class OtcClient {

    @Resource
    GrpcClientConfig grpcConfig;

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCItemDetail getItem(OTCGetItemInfoRequest request) {
        OTCItemServiceGrpc.OTCItemServiceBlockingStub stub = grpcConfig.otcItemServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getItem(request).getItem();
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public OTCGetItemsResponse getItemList(OTCGetItemsAdminRequest request) {
        OTCItemServiceGrpc.OTCItemServiceBlockingStub stub = grpcConfig.otcItemServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getItemsAdmin(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCUser getUserByNickname(GetUserByNicknameRequest request) {
        OTCUserServiceGrpc.OTCUserServiceBlockingStub stub = grpcConfig.otcUserServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getUserByNickname(request).getUser();
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCCancelItemResponse cancelItem(OTCCancelItemRequest request) {
        OTCItemServiceGrpc.OTCItemServiceBlockingStub stub = grpcConfig.otcItemServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.cancelItemToDelete(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    @OtcUnconfirmOrderAnnotation
    @OtcAppealHandleAnnotation
    public OTCGetOrdersResponse getOrderList(OTCGetOrdersRequest request) {
        OTCOrderServiceGrpc.OTCOrderServiceBlockingStub stub = grpcConfig.otcOrderServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getOrderListForAdmin(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCGetOrderInfoResponse getOrder(OTCGetOrderInfoRequest request) {
        OTCOrderServiceGrpc.OTCOrderServiceBlockingStub stub = grpcConfig.otcOrderServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getOrderInfo(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    //@OtcAppealHandleAnnotation
    public OTCHandleOrderResponse handleOrder(OTCHandleOrderRequest request) {
        OTCOrderServiceGrpc.OTCOrderServiceBlockingStub stub = grpcConfig.otcOrderServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.adminHandleOrder(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public GetAppealMessagesResponse getAppealMessages(GetAppealMessagesRequest request) {
        OTCMessageServiceGrpc.OTCMessageServiceBlockingStub stub = grpcConfig.otcMessageServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getAppealMessages(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public AddOTCWhiteListResponse addWhite(AddOTCWhiteListRequest request) {
        OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.otcConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        try {
            return stub.addOTCWhiteList(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public GetOTCWhiteListUserResponse getWhite(GetOTCWhiteListUserRequest request) {
        OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.otcConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        try {
            return stub.getOTCWhiteListUser(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public DelOTCWhiteListResponse delWhite(DelOTCWhiteListRequest request) {
        OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.otcConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        try {
            return stub.delOTCWhiteList(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public GetBrokerFunctionConfigResponse getWhiteListStatus(GetBrokerFunctionConfigRequest request) {
        BrokerFunctionConfigServiceGrpc.BrokerFunctionConfigServiceBlockingStub stub =
            grpcConfig.brokerFunctionConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        try {
            return stub.getAllBrokerFunctionConfig(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public SetBrokerFunctionConfigResponse setWhiteListStatus(SetBrokerFunctionConfigRequest request) {
        BrokerFunctionConfigServiceGrpc.BrokerFunctionConfigServiceBlockingStub stub =
                grpcConfig.brokerFunctionConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        try {
            return stub.setBrokerFunctionConfig(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public ShareOrderAppealResponse listShareOrderAppeal(OTCNormalOrderRequest request) {
        OTCOrderServiceGrpc.OTCOrderServiceBlockingStub stub = grpcConfig.otcOrderServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getShareOrderAppealInfo(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public GetOTCTokensResponse listToken(GetOTCTokensRequest request){
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getOTCBrokerTokens(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public BaseResponse updateBrokerTokenStatus(BrokerTokenStatusRequest request){
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.updateBrokerTokenStatus(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public BaseResponse updateBrokerTokenShareStatus(BrokerTokenStatusRequest request){
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.updateBrokerTokenShareStatus(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }



    public boolean saveBrokerExt(BrokerExtDTO brokerExt) {

        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {

            io.bhex.ex.otc.SaveBrokerExtRequest request= io.bhex.ex.otc.SaveBrokerExtRequest.newBuilder()
                    .setBrokerId(brokerExt.getBrokerId())
                    .setPhone(brokerExt.getPhone())
                    .setBrokerName(brokerExt.getBrokerName())
                    .build();

            SimpleResponse resp= stub.saveBrokerExt(request);
            log.info("method=saveBrokerExt,request:{}, reply:{}", request, resp);
            return resp.getResult() == OTCResult.SUCCESS;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return false;

    }


    public BrokerExtDTO getBrokerExt(Long brokerId) {
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        OrgIdRequest req= OrgIdRequest.newBuilder().setOrgId(brokerId).build();
        io.bhex.ex.otc.BrokerExtResponse resp=stub.getBrokerExt(req);
        log.info("method=getBrokerExt,request:{}, reply:{}", req, resp);
        if(resp.getRet()!=OTCResult.SUCCESS){
            return null;
        }
        return BrokerExtDTO.builder()
                .brokerId(resp.getBrokerId())
                .brokerName(resp.getBrokerName())
                .phone(resp.getPhone())
                .build();
    }


    public OTCOrderContactResponse getOtcOrderContact(Long orderId, Long orgId) {
        io.bhex.ex.otc.OTCOrderServiceGrpc.OTCOrderServiceBlockingStub stub = grpcConfig.exOtcOrderServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        OTCOrderContactRequest req = OTCOrderContactRequest.newBuilder()
                .setOrderId(orderId)
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(orgId))
                .build();
        io.bhex.ex.otc.OTCOrderContactResponse resp = stub.getOrderContact(req);
        log.info("method=getOtcOrderContact,request:{}, reply:{}", req, resp);
        return resp;
    }

    public InitOtcConfigResponse initOtcConfig(InitOtcConfigRequest request) {
        OTCAdminServiceGrpc.OTCAdminServiceBlockingStub stub = grpcConfig.otcAdminServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.initOtcConfig(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public BaseResponse saveBrokerToken(SaveOTCBrokerTokenRequest request) {
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.saveOTCBrokerToken(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public GetOTCBrokerTokenResponse getBrokerToken(GetOTCBrokerTokenRequest request) {
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getOTCBrokerToken(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public BaseResponse saveOTCBrokerCurrency(SaveOTCCurrencyRequest request) {
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.saveOTCBrokerCurrency(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public GetOTCCurrencysResponse listBrokerCurrency(GetOTCCurrencysRequest request){
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub= grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getOTCBrokerCurrencysForAdmin(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCGetPaymentResponse listPayment(OTCGetPaymentRequest request) {
        io.bhex.ex.otc.OTCPaymentTermServiceGrpc.OTCPaymentTermServiceBlockingStub stub = grpcConfig.exOtcPaymentTermServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.getPaymentTerms(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }


    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public ListOtcUserResponse listUser(ListOtcUserRequest request) {
        OTCUserServiceGrpc.OTCUserServiceBlockingStub stub = grpcConfig.otcUserServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.listUser(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public OTCSetNickNameResponse modifyUser(OTCSetNickNameRequest request) {
        OTCUserServiceGrpc.OTCUserServiceBlockingStub stub = grpcConfig.otcUserServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            return stub.setNickName(request);
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    /**
     * 独立部署--需要补全baseRequest
     *
     * @param request
     * @return
     */
    public boolean sortToken(SortTokenRequest request) {
        io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub stub = grpcConfig.exOtcConfigServiceBlockingStub(GrpcClientConfig.OTC_SERVER_CHANNEL_NAME);
        try {
            BaseResponse resp = stub.sortToken(request);
            if (resp.getResult() == OTCResult.SUCCESS) {
                return true;
            }

            log.info("sortToken fail,code={}",resp.getResult().name());
        } catch (StatusRuntimeException e) {
            log.error("sortToken exception", e);
        }
        return false;
    }
}

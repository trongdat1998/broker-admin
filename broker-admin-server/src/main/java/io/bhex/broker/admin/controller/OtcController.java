package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.SimpleSMSRequest;
import io.bhex.base.common.Telephone;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerExtDTO;
import io.bhex.broker.admin.controller.dto.OTCMessageDetailDTO;
import io.bhex.broker.admin.controller.dto.OTCOrderContactDTO;
import io.bhex.broker.admin.controller.dto.OtcItemDTO;
import io.bhex.broker.admin.controller.dto.OtcOrderDTO;
import io.bhex.broker.admin.controller.dto.OtcShareOrderAppealDTO;
import io.bhex.broker.admin.controller.dto.OtcWhiteUserDTO;
import io.bhex.broker.admin.controller.param.CancelOtcItemPO;
import io.bhex.broker.admin.controller.param.GetOtcItemPO;
import io.bhex.broker.admin.controller.param.GetOtcOrderPO;
import io.bhex.broker.admin.controller.param.HandleOtcOrderPO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.OTCOrderContactPO;
import io.bhex.broker.admin.controller.param.OTCOrderPO;
import io.bhex.broker.admin.controller.param.OtcWhiteListPO;
import io.bhex.broker.admin.controller.param.OtcWhiteUserPO;
import io.bhex.broker.admin.grpc.client.impl.OtcClient;
import io.bhex.broker.admin.grpc.client.impl.SupportClientImpl;
import io.bhex.broker.admin.service.impl.OsAccessAuthService;
import io.bhex.broker.admin.util.OtcBaseReqUtil;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.function.config.BrokerFunction;
import io.bhex.broker.grpc.function.config.BrokerFunctionConfig;
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
import io.bhex.ex.otc.GetAppealMessagesRequest;
import io.bhex.ex.otc.GetAppealMessagesResponse;
import io.bhex.ex.otc.GetUserByNicknameRequest;
import io.bhex.ex.otc.OTCCancelItemRequest;
import io.bhex.ex.otc.OTCCancelItemResponse;
import io.bhex.ex.otc.OTCGetItemInfoRequest;
import io.bhex.ex.otc.OTCGetItemsAdminRequest;
import io.bhex.ex.otc.OTCGetItemsResponse;
import io.bhex.ex.otc.OTCGetOrderInfoRequest;
import io.bhex.ex.otc.OTCGetOrderInfoResponse;
import io.bhex.ex.otc.OTCGetOrdersRequest;
import io.bhex.ex.otc.OTCGetOrdersResponse;
import io.bhex.ex.otc.OTCHandleOrderRequest;
import io.bhex.ex.otc.OTCHandleOrderResponse;
import io.bhex.ex.otc.OTCItemDetail;
import io.bhex.ex.otc.OTCItemStatusEnum;
import io.bhex.ex.otc.OTCMessageDetail;
import io.bhex.ex.otc.OTCNormalOrderRequest;
import io.bhex.ex.otc.OTCOrderContact;
import io.bhex.ex.otc.OTCOrderContactResponse;
import io.bhex.ex.otc.OTCOrderDetail;
import io.bhex.ex.otc.OTCOrderHandleTypeEnum;
import io.bhex.ex.otc.OTCOrderStatusEnum;
import io.bhex.ex.otc.OTCResult;
import io.bhex.ex.otc.OTCUser;
import io.bhex.ex.otc.ShareOrderAppealResponse;
import io.bhex.ex.proto.BaseRequest;
import io.bhex.ex.proto.Decimal;
import io.bhex.ex.proto.OrderSideEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lizhen
 * @date 2018-10-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/otc")
public class OtcController extends BrokerBaseController implements MessageSourceAware {

    @Autowired
    private OtcClient otcClient;
    @Autowired
    private OsAccessAuthService osAccessAuthService;

    private MessageSource messageSource;
    @Autowired
    private AdminLoginUserService adminLoginUserService;
    @Resource
    private SupportClientImpl supportClientImpl;

    @BussinessLogAnnotation(entityId = "{#po.itemId}")
    @RequestMapping(value = "/item/cancel", method = RequestMethod.POST)
    public ResultModel cancelItem(@RequestBody @Valid CancelOtcItemPO po, AdminUserReply adminUser) {
        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminUser.getId(), adminUser.getOrgId(), getAdminPlatform());
        OTCGetItemInfoRequest getItemInfoRequest = OTCGetItemInfoRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(adminUser.getOrgId()).build())
                .setAccountId(po.getAccountId())
                .setItemId(po.getItemId()).build();
        OTCItemDetail itemDetail = otcClient.getItem(getItemInfoRequest);
        if (itemDetail == null || itemDetail.getItemId() == 0) {
            return ResultModel.errorParameter("itemId", "error itemId");
        }

        OTCCancelItemRequest cancelItemInfoRequest = OTCCancelItemRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(adminUser.getOrgId()).setExchangeId(itemDetail.getExchangeId()).build())
                .setAccountId(po.getAccountId())
                .setItemId(po.getItemId())
                .build();
        OTCCancelItemResponse response = otcClient.cancelItem(cancelItemInfoRequest);
        if (response.getResult() != OTCResult.SUCCESS) {
            return ResultModel.error(response.getResult().name());
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/item/list", method = RequestMethod.POST)
    public ResultModel<List<OtcItemDTO>> getItems(@RequestBody @Valid GetOtcItemPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2;
        Long accountId = null;
        if (StringUtils.isNotEmpty(po.getNickname())) {
            OTCUser otcUser = otcClient.getUserByNickname(GetUserByNicknameRequest.newBuilder()
                    .setBaseRequest(OtcBaseReqUtil.getBaseRequest(adminUser.getOrgId()))
                    .setNickname(po.getNickname()).build());
            if (otcUser == null || otcUser.getUserId() == 0 || otcUser.getOrgId() != adminUser.getOrgId()) {
                return ResultModel.ok(Lists.newArrayList());
            }
            accountId = otcUser.getAccountId();
        } else {
            if (hasUserQueryCondition(po)) {
                combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());
                if (combo2 == null) {
                    return ResultModel.ok();
                }
                accountId = combo2.getV2();
            }
        }

        OTCGetItemsAdminRequest.Builder builder = OTCGetItemsAdminRequest.newBuilder()
                .setOrgId(adminUser.getOrgId())
                .setAccountId(accountId != null ? accountId : 0)
                .setLastId(po.getLastId())
                .setSize(po.getPageSize())
                .setTokenId(Strings.nullToEmpty(po.getTokenId()));
        if (po.getSide() == 1) {
            builder.addSide(OrderSideEnum.BUY);
        } else if (po.getSide() == 2) {
            builder.addSide(OrderSideEnum.SELL);
        }

        if (po.getStatus() > 0) {
            builder.addStatus(OTCItemStatusEnum.forNumber(po.getStatus()));
        }
        OTCGetItemsResponse response = otcClient.getItemList(builder.build());
        if (response == null || CollectionUtils.isEmpty(response.getItemsList())) {
            return ResultModel.ok(Lists.newArrayList());
        }
        List<OtcItemDTO> itemDTOList = response.getItemsList().stream().map(
                itemDetail -> convertOtcItemDTO(itemDetail, 8, 2)
        ).collect(Collectors.toList());
        if (itemDTOList.size() > po.getPageSize()) {
            itemDTOList.remove(itemDTOList.size() - 1);
        }
        return ResultModel.ok(itemDTOList);
    }

    @RequestMapping(value = "/order/list", method = RequestMethod.POST)
    public ResultModel<List<OtcOrderDTO>> listOrder(@RequestBody @Valid GetOtcOrderPO po) {
        log.info("Get otc order list {}", new Gson().toJson(po));
        List<OTCOrderStatusEnum> orderStatusEnums = new ArrayList<>();
        OTCOrderStatusEnum status = OTCOrderStatusEnum.OTC_ORDER_APPEAL;
        if (Objects.nonNull(po.getStatus()) && !po.getStatus().equals(0)) {
            OTCOrderStatusEnum tmpStatus = OTCOrderStatusEnum.forNumber(po.getStatus());
            if (Objects.nonNull(tmpStatus)) {
                status = tmpStatus;
            }
        } else {
            orderStatusEnums = Arrays.asList(
                    OTCOrderStatusEnum.OTC_ORDER_APPEAL,
                    OTCOrderStatusEnum.OTC_ORDER_CANCEL,
                    OTCOrderStatusEnum.OTC_ORDER_DELETE,
                    OTCOrderStatusEnum.OTC_ORDER_FINISH,
                    OTCOrderStatusEnum.OTC_ORDER_NORMAL,
                    OTCOrderStatusEnum.OTC_ORDER_UNCONFIRM,
                    OTCOrderStatusEnum.OTC_ORDER_INIT);
        }

        OTCGetOrdersRequest.Builder builder = OTCGetOrdersRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(getOrgId()).build())
                .addAllOrderStatus((Objects.nonNull(po.getStatus()) && !po.getStatus().equals(0)) ? Arrays.asList(status) : orderStatusEnums)
                .setPage(po.getCurrent())
                .setSize(po.getPageSize())
                .setEmail(StringUtils.isNotEmpty(po.getEmail()) ? po.getEmail() : "")
                .setMobile(StringUtils.isNotEmpty(po.getMobile()) ? po.getMobile() : "")
                .setUserId((po.getUserId() != null && po.getUserId() > 0) ? po.getUserId() : 0L)
                .setId((po.getOrderId() != null && po.getOrderId() > 0) ? po.getOrderId() : 0L)
                .setBeginTime((po.getStartTime() != null && po.getStartTime() > 0) ? po.getStartTime() : 0L)
                .setEndTime((po.getEndTime() != null && po.getEndTime() > 0) ? po.getEndTime() : 0L)
                .setTokenId(Strings.nullToEmpty(po.getTokenId()));
        if (OTCOrderStatusEnum.OTC_ORDER_UNCONFIRM == status) {
            //10分钟付款未确认
            builder.setEndTime(System.currentTimeMillis() - 60 * 8 * 1000);
        }

        if (po.getSide() != null && po.getSide().equals(0)) {
            builder.addAllSide(Arrays.asList(OrderSideEnum.SELL, OrderSideEnum.BUY));
        } else if (po.getSide() != null && po.getSide().equals(1)) {
            builder.addAllSide(Arrays.asList(OrderSideEnum.BUY));
        } else if (po.getSide() != null && po.getSide().equals(2)) {
            builder.addAllSide(Arrays.asList(OrderSideEnum.SELL));
        }

        OTCGetOrdersResponse response = otcClient.getOrderList(builder.build());
        if (response == null || CollectionUtils.isEmpty(response.getOrdersList())) {
            return ResultModel.ok(Lists.newArrayList());
        }

        OTCOrderStatusEnum orderStatus = status;

        Long orgId = getOrgId();

        List<OtcOrderDTO> orderDTOList = response.getOrdersList().stream().map(
                orderDetail -> convertOtcOrderResult(orgId, orderDetail, 8, 2, orderStatus)
        ).collect(Collectors.toList());
        return ResultModel.ok(orderDTOList);
    }


    private ResultModel<List<OtcOrderDTO>> getOtcOrder(@Valid @RequestBody GetOtcOrderPO po, Long orgId) {
        OTCGetOrderInfoRequest request = OTCGetOrderInfoRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(orgId))
                .setOrderId(po.getOrderId())
                .build();
        OTCGetOrderInfoResponse response = otcClient.getOrder(request);
        if (response == null || response.getOrder() == null ||
                response.getOrder().getOrderId() == 0L ||
                (response.getOrder().getMakerOrgId() != orgId &&
                        response.getOrder().getTakerOrgId() != orgId)) {
            return ResultModel.ok(Lists.newArrayList());
        }
        return ResultModel.ok(Lists.newArrayList(convertOtcOrderResult(getOrgId(), response.getOrder(), 8, 2, null)));
    }

    @RequestMapping(value = "/order/appeal_messages", method = RequestMethod.POST)
    public ResultModel<List<OtcOrderDTO>> getAppealMessages(@RequestBody @Valid OTCOrderPO po) {
        Map<String, List<OTCMessageDetailDTO>> result = new HashMap<>();
        result.put("takerMessages", new ArrayList<>());
        result.put("makerMessages", new ArrayList<>());

        GetAppealMessagesRequest request = GetAppealMessagesRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(getOrgId()).build())
                .setOrderId(po.getOrderId())
                .build();
        GetAppealMessagesResponse response = otcClient.getAppealMessages(request);
        if (response == null || CollectionUtils.isEmpty(response.getMessagesList())) {
            return ResultModel.ok(result);
        }
        List<OTCMessageDetail> messagesList = response.getMessagesList();

        OTCGetOrderInfoRequest orderRequest = OTCGetOrderInfoRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(getOrgId()))
                .setOrderId(po.getOrderId())
                .build();
        OTCGetOrderInfoResponse orderResponse = otcClient.getOrder(orderRequest);
        OTCOrderDetail orderDetail = orderResponse.getOrder();


        List<OTCMessageDetail> makerMessagesList = messagesList.stream()
                .filter(otcMessageDetail -> otcMessageDetail.getAccountId() == orderDetail.getAccountId())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(makerMessagesList)) {
            result.put("takerMessages", convertMessageDetais(makerMessagesList));
        }
        List<OTCMessageDetail> takerMessagesList = messagesList.stream()
                .filter(otcMessageDetail -> otcMessageDetail.getAccountId() == orderDetail.getTargetAccountId())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(takerMessagesList)) {
            result.put("makerMessages", convertMessageDetais(takerMessagesList));
        }
        return ResultModel.ok(result);

    }

    private List<OTCMessageDetailDTO> convertMessageDetais(List<OTCMessageDetail> messageDetails) {

        List<OTCMessageDetailDTO> dtos = messageDetails.stream().map(message -> {
            boolean image = message.getMsgType().name().contains("IMAGE");
            OTCMessageDetailDTO otcMessageDetailDTO = new OTCMessageDetailDTO();
            otcMessageDetailDTO.setCreateDate(message.getCreateDate());
            otcMessageDetailDTO.setMessage(image ? osAccessAuthService.createAccessUrl(message.getMessage()) : message.getMessage());
            otcMessageDetailDTO.setMessageType(image ? "image" : "word");
            return otcMessageDetailDTO;
        }).collect(Collectors.toList());
        return dtos;
    }

    @BussinessLogAnnotation(opContent = "{#po.type == 1 ? 'Cancel OTC Order' : 'Fulfilled OTC Order'} OtcOrderId:{#po.orderId} ")
    @RequestMapping(value = "/order/handle", method = RequestMethod.POST)
    public ResultModel<Boolean> handleOrder(@RequestBody @Valid HandleOtcOrderPO po) {
        OTCOrderHandleTypeEnum type;
        if (po.getType() == 1) {
            type = OTCOrderHandleTypeEnum.CANCEL;
        } else {
            type = OTCOrderHandleTypeEnum.FINISH;
        }

        OTCHandleOrderRequest request = OTCHandleOrderRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(getOrgId()).build())
                .setOrderId(po.getOrderId())
                .setType(type)
                .setExt(getRequestUserId().toString())
                .setAppealContent(Strings.nullToEmpty(po.getRemark()))
                .build();
        OTCHandleOrderResponse response = otcClient.handleOrder(request);
        if (response != null && response.getResult() == OTCResult.SUCCESS) {
            return ResultModel.ok(true);
        }
        return ResultModel.ok(false);
    }

    @BussinessLogAnnotation(opContent = "Remove OTC Merchant UID:{#po.userId}")
    @RequestMapping(value = "/user/white/del", method = RequestMethod.POST)
    public ResultModel<Boolean> delWhiteUser(@RequestBody @Valid OtcWhiteUserPO po) {
        DelOTCWhiteListRequest request = DelOTCWhiteListRequest.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(po.getUserId())
                .build();
        DelOTCWhiteListResponse response = otcClient.delWhite(request);
        if (response != null && response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.ok(false);
    }

    @RequestMapping(value = "/user/white/list", method = RequestMethod.POST)
    public ResultModel<List<OtcWhiteUserDTO>> getWhiteUserList(@RequestBody @Valid OtcWhiteListPO po) {
        GetOTCWhiteListUserRequest request = GetOTCWhiteListUserRequest.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(po.getUserId() == null ? 0 : po.getUserId())
                .setPage(po.getCurrent())
                .setSize(po.getPageSize())
                .build();
        GetOTCWhiteListUserResponse response = otcClient.getWhite(request);
        if (response != null && !CollectionUtils.isEmpty(response.getOtcUsersList())) {
            List<OtcWhiteUserDTO> list = response.getOtcUsersList().stream().map(
                    otcUser -> {
                        io.bhex.broker.grpc.user.User user = otcUser.getUser();
                        io.bhex.broker.grpc.otc.UserExtend extend = otcUser.getUserExt();
                        return OtcWhiteUserDTO.builder()
                                .userId(String.valueOf(user.getUserId()))
                                .email(user.getEmail())
/*                                .email(StringUtils.isNotBlank(user.getEmail())
                                        ? user.getEmail().replaceAll("(?<=.).(?=[^@]*?.@)", "*") : "")*/
                                .nationalCode(user.getNationalCode())
                                .mobile(user.getMobile())
/*                                .mobile(StringUtils.isNoneBlank(user.getMobile())
                                        ? user.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "")*/
                                .realName(extend.getRealName())
                                .nickname(extend.getNickname())
                                .finishOrderfRate30Days(extend.getFinishOrderRate30Days())
                                .finishOrderNumber30Days(extend.getFinishOrderNumber30Days())
                                .accountId(extend.getAccountId())
                                .build();
                    }
            ).collect(Collectors.toList());
            return ResultModel.ok(list);
        }
        return ResultModel.ok();
    }


    @BussinessLogAnnotation(opContent = "Add OTC Merchant UID:{#po.userId}")
    @RequestMapping(value = "/user/white/add", method = RequestMethod.POST)
    public ResultModel<Boolean> setWhiteUser(@RequestBody @Valid OtcWhiteUserPO po) {

        GetOTCWhiteListUserRequest listReq = GetOTCWhiteListUserRequest.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(po.getUserId())
                .setPage(1)
                .setSize(1)
                .build();

        GetOTCWhiteListUserResponse listResp = otcClient.getWhite(listReq);
        if (listResp.getOtcUsersCount() == 1) {
            return ResultModel.ok(true);
        }

        AddOTCWhiteListRequest addReq = AddOTCWhiteListRequest.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(po.getUserId())
                .build();

        AddOTCWhiteListResponse response = otcClient.addWhite(addReq);
        if (response != null && response.getRet() == 0) {
            return ResultModel.ok(true);
        }

        if (response.getRet() == BrokerErrorCode.USER_NOT_EXIST.code()) {
            String message = getMultiLanguageMsg("custom.label.user.id.have.not.exist");
            return ResultModel.error(message);
        }else if(response.getRet() == BrokerErrorCode.OTC_PERMISSION_DENIED.code()){
            String message = getMultiLanguageMsg("otc.permission.denied");
            return ResultModel.error(message);
        }

        return ResultModel.ok(false);
    }

    @RequestMapping(value = "/white_list/status", method = RequestMethod.POST)
    public ResultModel<Boolean> getWhiteListStatus() {
        GetBrokerFunctionConfigRequest request = GetBrokerFunctionConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(getOrgId()).build())
                .setFunction(BrokerFunction.OTC_WHITE_LIST)
                .build();
        GetBrokerFunctionConfigResponse response = otcClient.getWhiteListStatus(request);
        if (response != null && !CollectionUtils.isEmpty(response.getConfigListList())) {
            for (BrokerFunctionConfig brokerFunctionConfig : response.getConfigListList()) {
                if (brokerFunctionConfig.getFunction().equals(BrokerFunction.OTC_WHITE_LIST.name())
                        && brokerFunctionConfig.getStatus() == 1) {
                    return ResultModel.ok(true);
                }
            }
        }
        return ResultModel.ok(false);
    }

    @RequestMapping(value = "/white_list/set", method = RequestMethod.POST)
    public ResultModel<Boolean> setWhiteListStatus(@RequestBody @Valid OtcWhiteUserPO po) {
        SetBrokerFunctionConfigRequest request = SetBrokerFunctionConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(getOrgId()).build())
                .setFunction(BrokerFunction.OTC_WHITE_LIST)
                .setStatus(po.getStatus() == 1 ? 1 : 0)
                .build();
        SetBrokerFunctionConfigResponse response = otcClient.setWhiteListStatus(request);
        if (response != null && response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.ok(false);
    }

    /**
     * 深度共享订单查询申诉信息列表
     */
    @RequestMapping(value = "/order/appeal/list", method = RequestMethod.POST)
    public ResultModel<Map<String, OtcShareOrderAppealDTO>> listShareOrderAppeal(@RequestBody IdPO idPo) {

        ResultModel rm = ResultModel.ok();
        OTCNormalOrderRequest req = OTCNormalOrderRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(getOrgId()))
                .setOrderId(idPo.getId())
                .build();
        ShareOrderAppealResponse resp = otcClient.listShareOrderAppeal(req);
        if (resp.getResult() != OTCResult.SUCCESS) {
            return ResultModel.error(resp.getResult().getNumber(), resp.getResult().name());
        }

        if (resp.getAppealInfoCount() == 0) {
            rm.setData(Maps.newHashMap());
            return rm;
        }

        Long orgId = getOrgId();
        Map<String, OtcShareOrderAppealDTO> map = Maps.newHashMap();
        resp.getAppealInfoList().forEach(i -> {
            int status = 0;
            if (OTCOrderStatusEnum.OTC_ORDER_CANCEL == i.getStatus()) {
                status = OtcShareOrderAppealDTO.CANCEL;
            }

            if (OTCOrderStatusEnum.OTC_ORDER_FINISH == i.getStatus()) {
                status = OtcShareOrderAppealDTO.FINISH;
            }

            OtcShareOrderAppealDTO dto = OtcShareOrderAppealDTO.builder()
                    .brokerId(i.getBrokerId())
                    .type(status)
                    .remark(i.getComment())
                    .build();
            if (orgId.longValue() == i.getBrokerId()) {
                map.put("self", dto);
            } else {
                map.put("target", dto);
            }
        });

        rm.setData(map);
        return rm;
    }

    @RequestMapping(value = "/order/contact", method = RequestMethod.POST)
    public ResultModel<OTCOrderContactDTO> getOrderContact(@RequestBody OTCOrderContactPO param) {
        //查询券商联系方式
        BrokerExtDTO brokerExt = otcClient.getBrokerExt(param.getBrokerId());
        //查询是否是超级券商，可以查看用户信息
        //BrokerInfoDTO brokerInfo = brokerClient.queryBrokerInfoById(getOrgId());
        //查询订单状态
        OTCGetOrderInfoRequest orderReq = OTCGetOrderInfoRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(param.getBrokerId()))
                .setOrderId(param.getOrderId())
                .build();
        OTCGetOrderInfoResponse orderResp = otcClient.getOrder(orderReq);
        OTCOrderStatusEnum status = orderResp.getOrder().getOrderStatus();


        OTCOrderContactResponse contactResp = otcClient.getOtcOrderContact(param.getOrderId(), param.getBrokerId());
        OTCOrderContact maker = contactResp.getMaker();
        OTCOrderContact taker = contactResp.getTaker();

        OTCOrderContact target = null;
        if (maker.getOrgId() == param.getBrokerId().longValue()) {
            target = maker;
        }

        if (taker.getOrgId() == param.getBrokerId().longValue()) {
            target = taker;
        }

        long uid = 0L;
        String email = "";
        String mobile = "";
        boolean showDetail = false;
        boolean showSendSMS = false;

        if (Objects.nonNull(target) && status == OTCOrderStatusEnum.OTC_ORDER_APPEAL) {
            uid = target.getUserId();
            email = target.getEmail();
            mobile = target.getMobile();
            showDetail = true;
            showSendSMS = true;
        }

        OTCOrderContactDTO dto = OTCOrderContactDTO.builder()
                .brokerInfo(brokerExt)
                .email(email)
                .mobile(mobile)
                .uid(uid + "")
                .showDetail(showDetail)
                .showSendSMS(showSendSMS)
                .build();

        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/order/contact/sendSms", method = RequestMethod.POST)
    public ResultModel<Boolean> sendSMSForShareOrder(@RequestBody OTCOrderContactPO param) {
        //查询券商联系方式
        BrokerExtDTO brokerExt = otcClient.getBrokerExt(param.getBrokerId());

        //查询订单状态
        OTCGetOrderInfoRequest orderReq = OTCGetOrderInfoRequest.newBuilder()
                .setBaseRequest(OtcBaseReqUtil.getBaseRequest(param.getBrokerId()))
                .setOrderId(param.getOrderId())
                .build();
        OTCGetOrderInfoResponse orderResp = otcClient.getOrder(orderReq);
        OTCOrderStatusEnum status = orderResp.getOrder().getOrderStatus();
        if (status != OTCOrderStatusEnum.OTC_ORDER_APPEAL) {
            return ResultModel.error("Invalid order status");
        }

        //查询用户信息
        OTCOrderContactResponse contactResp = otcClient.getOtcOrderContact(param.getOrderId(), param.getBrokerId());
        OTCOrderContact maker = contactResp.getMaker();
        OTCOrderContact taker = contactResp.getTaker();

        Pair<String, String> brokerPhone = formatMobile(brokerExt.getPhone());
        String brokerMobile = brokerPhone.getValue();
        String nationalCodeBroker = brokerPhone.getKey();

        String userMobile = "";
        long userOrgId = 0;
        if (maker.getOrgId() == param.getBrokerId().longValue()) {
            userMobile = maker.getMobile();
            userOrgId = maker.getOrgId();
        }

        if (taker.getOrgId() == param.getBrokerId().longValue()) {
            userMobile = taker.getMobile();
            userOrgId = taker.getOrgId();
        }
        Pair<String, String> userPhone = formatMobile(userMobile);
        String nationalCodeUser = userPhone.getKey();
        String userMobile2 = userPhone.getValue();

        Locale brokerLocal, userLocal;

        if (nationalCodeBroker.startsWith("86") ||
                nationalCodeBroker.startsWith("086")) {
            brokerLocal = Locale.CHINA;
        } else {
            brokerLocal = Locale.ENGLISH;
        }

        if (nationalCodeUser.startsWith("86") ||
                nationalCodeUser.startsWith("086")) {
            userLocal = Locale.CHINA;
        } else {
            userLocal = Locale.ENGLISH;
        }

        String brokerMessage = messageSource.getMessage("otc.order.share.broker.notice", null, brokerLocal);
        String userMessage = messageSource.getMessage("otc.order.share.user.notice", null, userLocal);
        //给对方券商发消息
        sendSMS(param.getBrokerId(), nationalCodeBroker, brokerMobile, brokerMessage);
        //给对方用户发消息
        sendSMS(userOrgId, nationalCodeUser, userMobile2, userMessage);

        return ResultModel.ok(Boolean.TRUE);
    }

    private Pair<String, String> formatMobile(String phone) {
        if (StringUtils.isBlank(phone)) {
            return Pair.of("", "");
        }

        if (phone.contains("@")) {
            return Pair.of("", "");
        }

        String tmp = phone.split(",")[0];
        if (tmp.length() == 11) {
            return Pair.of("86", tmp);
        } else {
            String[] mobile = tmp.split(" ");
            if (mobile.length == 2) {
                return Pair.of(mobile[0], mobile[1]);
            }
        }

        return Pair.of("", "");
    }

    private void sendSMS(Long orgId, String nationalCode, String mobile, String content) {

        if (Objects.isNull(orgId) || orgId.longValue() < 1L) {
            log.warn("Invalid orgId", orgId);
            return;
        }

        if (StringUtils.isBlank(nationalCode) || StringUtils.isBlank(mobile)) {
            log.warn("Invalid phone,national code={},mobile={}", nationalCode, mobile);
            return;
        }

        if (StringUtils.isBlank(content)) {
            log.warn("Content is blank,national code={},mobile={}", nationalCode, mobile);
            return;
        }

        SimpleSMSRequest req = SimpleSMSRequest.newBuilder()
                .setOrgId(orgId)
                .setTelephone(Telephone.newBuilder().setNationCode(nationalCode).setMobile(mobile).build())
                .addAllParams(Arrays.asList(new String[]{content}))
                .setSign("")
                .build();

        supportClientImpl.sendSms(req);

    }

    private OtcItemDTO convertOtcItemDTO(OTCItemDetail itemDetail, int tokenScale, int currencyScale) {
        return OtcItemDTO.builder()
                .id(String.valueOf(itemDetail.getItemId()))
                .accountId(String.valueOf(itemDetail.getAccountId()))
                .nickName(itemDetail.getNickName())
                .tokenId(itemDetail.getTokenId())
                .currencyId(itemDetail.getCurrencyId())
                .side(itemDetail.getSideValue())
                .priceType(itemDetail.getPriceTypeValue())
                .price(toTrimString(itemDetail.getPrice(), currencyScale))
                .premium(itemDetail.getPremium() != null ? toTrimString(itemDetail.getPremium(), 0) : null)
                .quantity(toTrimString(itemDetail.getQuantity(), tokenScale))
                .lastQuantity(toTrimString(itemDetail.getLastQuantity(), tokenScale))
                .executedQuantity(toTrimString(itemDetail.getExecutedQuantity(), tokenScale))
                .frozenQuantity(toTrimString(itemDetail.getFrozenQuantity(), tokenScale))
                .minAmount(toTrimString(itemDetail.getMinAmount(), currencyScale))
                .maxAmount(toTrimString(itemDetail.getMaxAmount(), currencyScale))
                .remark(itemDetail.getAutoReply())
                .status(itemDetail.getItemStatusValue())
                .createDate(itemDetail.getCreateDate())
                .orderNum(itemDetail.getOrderNum())
                .finishNum(itemDetail.getFinishNum())
                .recentOrderNum(itemDetail.getRecentOrderNum())
                .recentExecuteRate(itemDetail.getRecentOrderNum() == 0
                        ? 0 : 100 * itemDetail.getRecentExecuteNum() / itemDetail.getRecentOrderNum())
                .build();
    }

    private String toTrimString(Decimal value, int scale) {
        return toBigDecimal(value, scale).stripTrailingZeros().toPlainString();
    }

    private BigDecimal toBigDecimal(Decimal decimalValue, int scale) {
        if (null != decimalValue.getStr() && !"".equals(decimalValue.getStr().trim())) {
            return new BigDecimal(decimalValue.getStr()).setScale(scale, BigDecimal.ROUND_DOWN);
        }
        return BigDecimal.valueOf(decimalValue.getUnscaledValue(),
                decimalValue.getScale()).setScale(scale, BigDecimal.ROUND_DOWN);
    }

    private OtcOrderDTO convertOtcOrderResult(Long orgId, OTCOrderDetail orderDetail, int tokenScale, int currencyScale, OTCOrderStatusEnum orderStatus) {
        long diff = (System.currentTimeMillis() - orderDetail.getUpdateDate()) / 1000;
        long remainSeconds = 0L;
        if (orderStatus == OTCOrderStatusEnum.OTC_ORDER_UNCONFIRM) {
            if (diff <= 30 * 60) {
                remainSeconds = 30 * 60 - diff;
            }
        }

        if (orderStatus == OTCOrderStatusEnum.OTC_ORDER_NORMAL) {
            remainSeconds = 15 * 60 - diff;
        }

        String nickname = orderDetail.getNickName();
        String targetNickname = orderDetail.getTargetNickName();
        long dealBrokerId = 0L;
        int brokerType = 0;

        String message = messageSource.getMessage("otc.appeal.order.contact.broker", null, LocaleContextHolder.getLocale());

        if (orderDetail.getDepthShare()) {
            if (orgId.equals(orderDetail.getMakerOrgId())) {
                nickname = message;
                dealBrokerId = orderDetail.getTakerOrgId();
                brokerType = 1;
            } else if (orgId.equals(orderDetail.getTakerOrgId())) {
                targetNickname = message;
                dealBrokerId = orderDetail.getMakerOrgId();
                brokerType = 2;
            }
        }

        if (orderDetail.getOrderExt().getCurrencyScale() > 0) {
            currencyScale = orderDetail.getOrderExt().getCurrencyScale();
        }

        if (orderDetail.getOrderExt().getTokenScale() > 0) {
            tokenScale = orderDetail.getOrderExt().getTokenScale();
        }

        return OtcOrderDTO.builder()
                .id(String.valueOf(orderDetail.getOrderId()))
                .itemId(String.valueOf(orderDetail.getItemId()))
                .accountId(String.valueOf(orderDetail.getAccountId()))
                //.nickName(orderDetail.getNickName())
                .nickName(nickname)
                .targetAccountId(String.valueOf(orderDetail.getTargetAccountId()))
                //.targetNickName(orderDetail.getTargetNickName())
                .targetNickName(targetNickname)
                .side(orderDetail.getSideValue())
                .paymentType(orderDetail.getPayment() != null ? orderDetail.getPaymentValue() : null)
                .price(toTrimString(orderDetail.getPrice(), currencyScale))
                .quantity(toTrimString(orderDetail.getQuantity(), tokenScale))
                .amount(toTrimString(orderDetail.getAmount(), currencyScale))
                .tokenId(orderDetail.getTokenId())
                .currencyId(orderDetail.getCurrencyId())
                .payCode(orderDetail.getPayCode())
                .transferDate(orderDetail.getTransferDate())
                .createDate(orderDetail.getCreateDate())
                .remainSeconds(remainSeconds)
                .status(orderDetail.getOrderStatusValue())
                .remark(orderDetail.getRemark())
                .appealType(orderDetail.getAppealTypeValue())
                .appealContent(StringUtils.isNotBlank(orderDetail.getAppealContent()) ? orderDetail.getAppealContent() : "")
                .depthShare(orderDetail.getDepthShare())
                .dealBrokerId(dealBrokerId)
                .brokerType(brokerType)
                .build();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMultiLanguageMsg(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }


}

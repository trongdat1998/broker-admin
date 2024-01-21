package io.bhex.broker.admin.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.protobuf.TextFormat;
import io.bhex.base.account.*;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.clear.AssetRequest;
import io.bhex.base.clear.AssetResponse;
import io.bhex.base.proto.Decimal;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.base.quote.GetLegalCoinRatesReply;
import io.bhex.base.quote.GetRatesRequest;
import io.bhex.base.quote.Rate;
import io.bhex.base.token.GetTokenRequest;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.AirdropDTO;
import io.bhex.broker.admin.controller.dto.AirdropTmplDTO;
import io.bhex.broker.admin.controller.param.AirdropPO;
import io.bhex.broker.admin.controller.param.QueryAirdropPO;
import io.bhex.broker.admin.grpc.client.*;
import io.bhex.broker.admin.service.AirdropService;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.GrpcQuoteService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.account.*;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.Header;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 07/11/2018 6:24 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class AirdropServiceImpl implements AirdropService {


    private static final String AIRDROP_ADD_TMPL_LOCK_KEY = "aridrop.addtmpl.lock.";

    private static final String USDT_TOKEN_ID = "USDT";

    // 单个用户空投的USDT上限
    private static final BigDecimal AIRDROP_USDT_LIMIT = new BigDecimal(10000);

    private static final Integer BATCH_LIMIT = 2500;

    private static final String SPLIT_REGEX = ",";

    @Autowired
    private BalanceTransferClient balanceTransferClient;

    @Autowired
    private BalanceClient balanceClient;

    @Resource
    private GrpcQuoteService grpcQuoteService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private AdminUserClient adminUserClient;

    @Autowired
    private BrokerAccountClient brokerAccountClient;

    @Autowired
    private AirdropClient airdropClient;

    @Autowired
    private TokenClient tokenClient;

    @Autowired
    private AdminUserNameService adminUserNameService;

    @Resource
    private BrokerUserClient brokerUserClient;

    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;


    /**
     * 重新启动时要重试空投中的空投项目
     */
    @PostConstruct
    private void retryAirdrop() {

    }

    /**
     * 按照分组重试空投转账
     *
     * @param airdropInfo
     * @return
     */
    public Integer autoRetryAirdrop(AirdropInfo airdropInfo) {
        //1.重试状态为空投中的项目
        //2.无需检验账户余额
        List<TransferGroupInfo> transferGroupInfos = listAllTransferGroup(airdropInfo.getId(), airdropInfo.getBrokerId());
//        if (!isOrgBanlanceEnough(airdropInfo, transferGroupInfos)) {
//            return AirdropClient.STATUS_FAILED_INSUFFICIENT;
//        }
        Integer status = AirdropClient.STATUS_SUCCESS;
        Boolean haveError = false;
        Boolean haveSuccess = false;
        if (!CollectionUtils.isEmpty(transferGroupInfos)) {
            status = AirdropClient.STATUS_FAILED;
            for (TransferGroupInfo groupInfo : transferGroupInfos) {

                if (AirdropClient.STATUS_INIT == groupInfo.getStatus() || AirdropClient.STATUS_AIRDOP == groupInfo.getStatus()) {
                    // 开始发送，更新状态为发送中
                    updateTransferGroupStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), AirdropClient.STATUS_AIRDOP, groupInfo.getId());
                    List<TransferRecord> transferRecords = listTransferRecordByGroupId(airdropInfo.getId(), airdropInfo.getBrokerId(), groupInfo.getId());

                    List<BatchTransferItem> transferItems = transferRecords.stream().map(tr -> {
                        return BatchTransferItem.newBuilder()
                                .setSubject(BusinessSubject.AIRDROP)
                                .setTargetAccountId(tr.getAccountId())
                                .setTargetAccountType(AccountType.GENERAL_ACCOUNT)
                                .setTokenId(tr.getTokenId())
                                .setAmount(tr.getTokenAmount())
                                // .setIsLocked(airdropInfo.getLockModel() == 1) //是否锁仓 批量转账不支持锁仓
                                .build();
                    }).collect(Collectors.toList());


                    Boolean isOk = batchTransfer(airdropInfo, groupInfo.getId(), transferItems);
                    if (!isOk) {
                        haveError = true;
                    } else {
                        haveSuccess = true;
                    }
                    updateTransferGroupStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), isOk ? AirdropClient.STATUS_SUCCESS : AirdropClient.STATUS_FAILED, groupInfo.getId());

                } else {
                    haveSuccess = true;
                }
            }
            // 更新空投状态 发送完毕
            if (haveError && haveSuccess) {
                //有异常就是部分成功
                status = AirdropClient.STATUS_PART_SUCCESS;
            } else if (haveError && !haveSuccess) {
                //有错误，没有成功 失败
                status = AirdropClient.STATUS_FAILED;
            } else if (!haveError && haveSuccess) {
                //有成功，没有失败 成功
                status = AirdropClient.STATUS_SUCCESS;
            }
        }

        return status;
    }

//    @Scheduled(cron = "36 * * * * ?")
//    private void scheduleAirdrop() {
//        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        log.info(sdf.format(DateTime.now().toDate()) + " Airdrop Schedule Job.");
//
//        List<AirdropInfo> infos = listScheduleAirdrop();
//        if (!CollectionUtils.isEmpty(infos)) {
//            for (AirdropInfo info : infos) {
//                if (redisTemplate.opsForValue().get(AIRDROP_ADD_TMPL_LOCK_KEY + info.getId()) == null) {
//                    airdropProcess(info.getId(), info.getBrokerId());
//                }
//            }
//        }
//    }

    @Override
    public List<AirdropDTO> queryAirdropInfo(QueryAirdropPO param) {
        QueryAirdropInfoRequest request = QueryAirdropInfoRequest.newBuilder()
                .setTitle(param.getTitle())
                .setBeginTime(param.getBeginTime())
                .setEndTime(param.getEndTime())
                .setBrokerId(param.getBrokerId())
                .build();
        QueryAirdropInfoReply reply = airdropClient.queryAirdropInfo(request);
        List<AirdropInfo> airdropInfoList = reply.getAirdropInfoList();
        List<AirdropDTO> airdropDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(airdropInfoList)) {
            airdropDTOs = airdropInfoList.stream().map(airdropInfo -> {
                AirdropDTO dto = new AirdropDTO();
                BeanUtils.copyProperties(airdropInfo, dto);
                AdminUserReply adminUser = adminUserClient.getAdminUserById(airdropInfo.getAdminId());
                if (null != adminUser) {
                    dto.setAdminId(adminUserNameService.getAdminName(airdropInfo.getBrokerId(), adminUser.getEmail()));
                }
                dto.setAirdropTokenNum(new BigDecimal(airdropInfo.getAirdropTokenNum()));
                dto.setHaveTokenNum(new BigDecimal(airdropInfo.getHaveTokenNum()));
                dto.setUserAccountIds(airdropInfo.getUserIds());
                dto.setTmplModel(StringUtils.isEmpty(airdropInfo.getTmplUrl()) ? 0 : 1);

                return dto;
            }).collect(Collectors.toList());
        }
        return airdropDTOs;
    }

    @Override
    public Boolean retryAirdrop(Long airdropId, Long brokerId) {
        //重试前要确认下当前空投的状态
        AirdropInfo airdropInfo = getAirdropInfo(airdropId, brokerId);
        if (null == airdropInfo || airdropInfo.getStatus() != AirdropClient.STATUS_FAILED || airdropInfo.getStatus() != AirdropClient.STATUS_PART_SUCCESS) {
            return false;
        }
        log.info("Airdrop Retry: {}", airdropInfo);
        Integer status = batchTransferProcess(airdropInfo);
        updateAirdropStatus(airdropInfo.getAccountId(), airdropInfo.getBrokerId(), status);
        return true;
    }

    @Override
    public AirdropInfo getAirdropInfo(Long airdropId, Long brokerId) {
        if (null != airdropId && airdropId != 0L) {
            GetAirdropInfoRequest request = GetAirdropInfoRequest.newBuilder()
                    .setAirdropId(airdropId)
                    .setBrokerId(brokerId)
                    .build();
            return airdropClient.getAirdropInfo(request);
        }
        return null;
    }

    @Override
    public Boolean lockAndAirdrop(Long airdropId, Long brokerId) {
        if (null != airdropId && airdropId != 0L) {
            LockAndAirdropRequest request = LockAndAirdropRequest.newBuilder()
                    .setAirdropId(airdropId)
                    .setBrokerId(brokerId)
                    .build();
            return airdropClient.lockAndAirdrop(request).getIsLocked();
        }
        return null;
    }

    @Override
    public List<AirdropInfo> listScheduleAirdrop() {
        QueryAirdropInfoReply reply = airdropClient.listScheduleAirdrop();
        List<AirdropInfo> airdropInfoList = reply.getAirdropInfoList();
        return airdropInfoList;
    }

    @Override
    public Boolean airdropProcess(Long airdropId, Long brokerId) {
        //1.获取用户信息 （分页）
        //2.取得总用户数，计算总共空投钱数，验证空投账户余额是否够
        //3.拉取快照 快照数据入库
        //4.根据用户资产计算空投数量
        //4.分组生成转账记录入库
        //5.根据转账记录进行转账
        //

        //判断空投状态，如果为初始化，则锁定并变更空投状态为 空投中
        Boolean isLocked = lockAndAirdrop(airdropId, brokerId);
        AirdropInfo airdropInfo = getAirdropInfo(airdropId, brokerId);
        if (null == airdropInfo || !isLocked) {
            return false;
        }

        boolean tmplModel = !StringUtils.isEmpty(airdropInfo.getTmplUrl());

        // 更新空投状态 转账中
        updateAirdropStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), AirdropClient.STATUS_AIRDOP);
        Integer index = 0;
        Long fromId = 0L;
        while (true) {
            if (!tmplModel) {
                List<Long> accountIds = new ArrayList<>();
                if (AirdropPO.USER_TYPE_ALL == airdropInfo.getUserType()) {
                    GetBrokerAccountListResponse reply = getAllUserPageable(airdropInfo.getAirdropTime(), fromId, airdropInfo.getBrokerId(), BATCH_LIMIT);
                    List<SimpleAccount> simpleAccounts = reply.getAccountsList();
                    if (!CollectionUtils.isEmpty(simpleAccounts)) {
                        for (SimpleAccount sa : simpleAccounts) {
                            fromId = sa.getId() > fromId ? sa.getId() : fromId;
                            accountIds.add(sa.getAccountId());
                        }
                    }
                } else if (AirdropPO.USER_TYPE_SPECIAL == airdropInfo.getUserType()) {
                    accountIds = getSpecialUserPageable(airdropInfo, index, BATCH_LIMIT);
                    index += BATCH_LIMIT;
                }
                // 如果待发送用户为空，则退出发送
                if (CollectionUtils.isEmpty(accountIds)) {
                    break;
                }
                // 校验account id是否为本交易所用户
                accountIds = verifyAccountIds(brokerId, accountIds);
                // 是否已经转过钱了，查询转账记录并过滤account_id
                accountIds = transferRecordFilter(accountIds, airdropInfo.getId());

                List<TransferRecord> transferItems = getUnTmplModelTransferItems(accountIds, airdropInfo, index, BATCH_LIMIT);
                // 存储转账记录，后续按此记录进行转账
                saveTransferRecord(airdropInfo, transferItems);
            } else {
                log.info("tmpl model airdrop");
                List<TransferRecord> transferItems = getTmplModelTransferItems(airdropInfo, index, BATCH_LIMIT);
                if (CollectionUtils.isEmpty(transferItems)) {
                    break;
                }
                // 存储转账记录，后续按此记录进行转账
                saveTransferRecord(airdropInfo, transferItems);
                index += BATCH_LIMIT;
            }
        }
        Integer status = batchTransferProcess(airdropInfo);
        updateAirdropStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), status);
        return true;
    }

    //非模板转账条目
    private List<TransferRecord> getUnTmplModelTransferItems(List<Long> accountIds, AirdropInfo airdropInfo, Integer index, Integer limit) {
        // 免费糖果 按照设定比例直接空投
        if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_CANDY) {
            List<TransferRecord> transferItems = accountIds.stream().map(accountId -> {
                return TransferRecord.newBuilder()
                        .setAccountId(accountId)
                        .setBrokerId(airdropInfo.getBrokerId())
                        .setSnapshotTime(airdropInfo.getSnapshotTime())
                        .setTokenId(airdropInfo.getAirdropTokenId())
                        .setTokenAmount(airdropInfo.getAirdropTokenNum())
                        .build();
            }).collect(Collectors.toList());
            return transferItems;
            // 分叉空投 需要根据持有币数量进行空投
        } else if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
            Map<Long, BigDecimal> userAssetMap = getUserAsset(airdropInfo, accountIds, airdropInfo.getHaveTokenId(), airdropInfo.getSnapshotTime());
            List<TransferRecord> transferItems = new ArrayList<>();
            if (null != userAssetMap) {
                userAssetMap.forEach((accountId, asset) -> {
                    transferItems.add(TransferRecord.newBuilder()
                            .setAccountId(accountId)
                            .setBrokerId(airdropInfo.getBrokerId())
                            .setSnapshotTime(airdropInfo.getSnapshotTime())
                            .setTokenId(airdropInfo.getAirdropTokenId())
                            .setTokenAmount(countAirdropTokenNum(airdropInfo, asset).toString())
                            .build());
                });
            }
            return transferItems;
        }
        return new ArrayList<>();
    }

    private List<TransferRecord> getTmplModelTransferItems(AirdropInfo airdropInfo, Integer index, Integer limit) {
        ListTmplRecordsRequest.Builder builder = ListTmplRecordsRequest.newBuilder()
                .setAirdropId(airdropInfo.getId())
                .setBrokerId(airdropInfo.getBrokerId())
                .setGroupId(0)
                .setLimit(limit);

        if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_CANDY) {
            builder.setOrderByColumn("tokenId");
        } else if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
            builder.setOrderByColumn("haveTokenId");
        }
        ListTmplRecordsReply replay = airdropClient.listTmplRecords(builder.build());
        List<TmplRecord> tmplRecords = replay.getTmplRecordList();
        if (CollectionUtils.isEmpty(tmplRecords)) {
            return new ArrayList<>();
        }

        List<TmplRecord> usedRecords = new ArrayList<>();
        usedRecords.add(tmplRecords.get(0));
        for (int i = 1; i < tmplRecords.size(); i++) {
            TmplRecord record = tmplRecords.get(i);
            if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_CANDY) {
                if (record.getTokenId().equals(tmplRecords.get(i - 1).getTokenId())) {
                    usedRecords.add(record);
                } else {
                    break;
                }
            } else if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
                if (record.getTokenId().equals(tmplRecords.get(i - 1).getTokenId())
                        && record.getHaveTokenId().equals(tmplRecords.get(i - 1).getHaveTokenId())) {
                    usedRecords.add(record);
                } else {
                    break;
                }
            }
        }


//        // 免费糖果 按照设定比例直接空投
        if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_CANDY) {
            List<TransferRecord> transferItems = usedRecords.stream().map(d -> {
                return TransferRecord.newBuilder()
                        .setAccountId(d.getAccountId())
                        .setBrokerId(d.getBrokerId())
                        .setSnapshotTime(airdropInfo.getSnapshotTime())
                        .setTokenId(d.getTokenId())
                        .setTokenAmount(d.getTokenAmount())
                        .setTmplLineId(d.getTmplLineId())
                        .build();
            }).collect(Collectors.toList());
            return transferItems;
            // 分叉空投 需要根据持有币数量进行空投
        } else if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
            List<Long> accountIds = usedRecords.stream().map(u -> u.getAccountId()).collect(Collectors.toList());
            Map<Long, BigDecimal> userAssetMap = getUserAsset(airdropInfo, accountIds, usedRecords.get(0).getHaveTokenId(), airdropInfo.getSnapshotTime());


            List<TransferRecord> transferItems = usedRecords.stream().map(d -> {
                return TransferRecord.newBuilder()
                        .setAccountId(d.getAccountId())
                        .setBrokerId(d.getBrokerId())
                        .setSnapshotTime(airdropInfo.getSnapshotTime())
                        .setTokenId(d.getTokenId())

                        .setTokenAmount(countTmplModelAirdropTokenNum(d, userAssetMap.get(d.getAccountId()), d.getBrokerId()).toPlainString())

                        .setTmplLineId(d.getTmplLineId())
                        .build();
            }).collect(Collectors.toList());

            return transferItems;
        }
        return new ArrayList<>();
    }

    private BigDecimal countTmplModelAirdropTokenNum(TmplRecord dto, BigDecimal balance, Long brokerId) {
        if (BigDecimal.ZERO.compareTo(new BigDecimal(dto.getHaveTokenAmount())) == 0
                || balance == null || BigDecimal.ZERO.compareTo(balance) == 0) {
            return new BigDecimal(0);
        }
        BigDecimal airdropNum = new BigDecimal(dto.getTokenAmount())
                .multiply(balance.divide(new BigDecimal(dto.getHaveTokenAmount()), RoundingMode.DOWN));
        // 获取投放token的最小精度
        Integer tokenScale = getTokenScale(dto.getTokenId(), brokerId);
        airdropNum = airdropNum.setScale(tokenScale, BigDecimal.ROUND_DOWN);
        return airdropNum;
    }

    /**
     * 判断机构用户余额是否够本次空投
     *
     * @param airdropInfo
     * @param transferGroupInfos
     * @return
     */
    private Boolean isOrgBanlanceEnough(AirdropInfo airdropInfo, List<TransferGroupInfo> transferGroupInfos) {
        Map<String, BigDecimal> transferMap = new HashMap<>();

        for (TransferGroupInfo info : transferGroupInfos) {
            if (AirdropClient.STATUS_INIT == info.getStatus()) {
                String token = info.getTokenId();
                if (transferMap.containsKey(token)) {
                    BigDecimal transferAmount = transferMap.get(token).add(new BigDecimal(info.getTransferAssetAmount()));
                    transferMap.put(token, transferAmount);
                } else {
                    transferMap.put(token, new BigDecimal(info.getTransferAssetAmount()));
                }
            }
        }

        for (String token : transferMap.keySet()) {
            BigDecimal orgBalance = getOrgBalance(airdropInfo.getAccountId(), token, airdropInfo.getBrokerId());
            if (orgBalance.compareTo(transferMap.get(token)) < 0) {
                // 机构账户余额小于待发送总钱数，不予发送
                updateAirdropStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), AirdropClient.STATUS_FAILED);
                log.warn("Airdrop ERROR: Insufficient account balance. token:{} orgBalance: {},transferAmount: {}, info: {}",
                        token, orgBalance, transferMap.get(token), TextFormat.shortDebugString(airdropInfo));
                return false;
            }
        }


        return true;
    }

//    private Boolean isOrgBanlanceEnough(long orgAccount, String orgTokenId, TransferGroupInfo info) {
//        BigDecimal transferAmount = new BigDecimal(info.getTransferAssetAmount());
//
//        BigDecimal orgBalance = getOrgBalance(orgAccount, orgTokenId);
//        if (orgBalance.compareTo(transferAmount) < 0) {
//            log.error("Airdrop Error: Insufficient account balance. orgTokenId:{}, orgBalance: {},transferAmount: {}, airdropId: {}",
//                    orgTokenId, orgBalance.toPlainString(), transferAmount.toPlainString(), info.getAirdropId());
//            return false;
//        }
//        return true;
//    }

    /**
     * 按照分组进行空投转账
     *
     * @param airdropInfo
     * @return
     */
    public Integer batchTransferProcess(AirdropInfo airdropInfo) {
        //1.计算发送账户资金是否够空投
        //2.获取全部分组信息
        //3.按分组信息进行空投
        //4.空投成功更新对应分组状态
        //5.更新空投状态

        List<TransferGroupInfo> transferGroupInfos = listAllTransferGroup(airdropInfo.getId(), airdropInfo.getBrokerId());
        if (!isOrgBanlanceEnough(airdropInfo, transferGroupInfos)) {
            return AirdropClient.STATUS_FAILED_INSUFFICIENT;
        }
        Integer status = AirdropClient.STATUS_SUCCESS;
        Boolean haveError = false;
        Boolean haveSuccess = false;
        if (!CollectionUtils.isEmpty(transferGroupInfos)) {
            status = AirdropClient.STATUS_FAILED;
            for (TransferGroupInfo groupInfo : transferGroupInfos) {

                if (AirdropClient.STATUS_INIT == groupInfo.getStatus()) {
                    // 开始发送，更新状态为发送中
                    updateTransferGroupStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), AirdropClient.STATUS_AIRDOP, groupInfo.getId());
                    List<TransferRecord> transferRecords = listTransferRecordByGroupId(airdropInfo.getId(), airdropInfo.getBrokerId(), groupInfo.getId());

                    List<BatchTransferItem> transferItems = transferRecords.stream().map(tr -> {
                        return BatchTransferItem.newBuilder()
                                .setSubject(BusinessSubject.AIRDROP)
                                .setTargetAccountId(tr.getAccountId())
                                .setTargetAccountType(AccountType.GENERAL_ACCOUNT)
                                .setTokenId(tr.getTokenId())
                                .setAmount(tr.getTokenAmount())
                                // .setIsLocked(airdropInfo.getLockModel() == 1) //是否锁仓 批量转账不支持锁仓
                                .build();
                    }).collect(Collectors.toList());


                    Boolean isOk = batchTransfer(airdropInfo, groupInfo.getId(), transferItems);
                    if (!isOk) {
                        haveError = true;
                    } else {
                        haveSuccess = true;
                    }
                    updateTransferGroupStatus(airdropInfo.getId(), airdropInfo.getBrokerId(), isOk ? AirdropClient.STATUS_SUCCESS : AirdropClient.STATUS_FAILED, groupInfo.getId());

                } else {
                    haveSuccess = true;
                }
            }
            // 更新空投状态 发送完毕
            if (haveError && haveSuccess) {
                //有异常就是部分成功
                status = AirdropClient.STATUS_PART_SUCCESS;
            } else if (haveError && !haveSuccess) {
                //有错误，没有成功 失败
                status = AirdropClient.STATUS_FAILED;
            } else if (!haveError && haveSuccess) {
                //有成功，没有失败 成功
                status = AirdropClient.STATUS_SUCCESS;
            }
        }

        return status;
    }

    @Override
    public Combo2<Boolean, List<Long>> createAirdrop(AirdropPO param, AdminUserReply adminUser) {

        boolean tmplModel = param.getTmplModel() == 1;
        CreateAirdropInfoRequest.Builder airdropBuilder = CreateAirdropInfoRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(param, airdropBuilder);

        if (tmplModel) {
            airdropBuilder.setAdminId(adminUser.getId());
            airdropBuilder.setAdminUserName(adminUser.getUsername());
            airdropBuilder.setAdminRealName(adminUser.getRealName());
            airdropBuilder.setTmplUrl(param.getTmplUrl());
            CreateAirdropInfoReply airdropInfoReply = airdropClient.createAirdropInfo(airdropBuilder.build());
            redisTemplate.opsForValue().set(AIRDROP_ADD_TMPL_LOCK_KEY + airdropInfoReply.getAirdropId(), "", 10, TimeUnit.MINUTES);
            //String content = new String(awsPublicObjectStorage.downloadObject(param.getTmplUrl()));
            try {
                byte[] bytes = awsPublicObjectStorage.downloadObject(param.getTmplUrl());
                XSSFWorkbook workbook;
                try {
                    workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes));
                } catch (Exception e) {
                    throw new BizException(ErrorCode.ERROR);
                }

                List<AirdropTmplDTO> tmplRecords = convertTmpl(workbook);
                List<String> tokenIds = new ArrayList<>();
                tmplRecords.forEach(r -> {
                    if (!tokenIds.contains(r.getAirdropTokenId())) {
                        tokenIds.add(r.getAirdropTokenId());
                    }
                });
                Map<String, Rate>  rateMap = getRate(param.getBrokerId(), tokenIds);

                // 如果超过USDT限额，则终止创建并抛出对应异常
                tmplRecords.forEach(r -> {
                    BigDecimal usdtRate = getUSDTRate(r.getAirdropTokenId(), rateMap);
                    overUSDTLimit(param.getBrokerId(), airdropInfoReply.getAirdropId(), r.getAirdropTokenId(), new BigDecimal(r.getAirdropTokenNum()), usdtRate);
                });

                int counter = 0;
                List<TmplRecord> records = new ArrayList<>();
                for (AirdropTmplDTO dto : tmplRecords) {
                    TmplRecord.Builder record = TmplRecord.newBuilder();
                    BeanCopyUtils.copyPropertiesIgnoreNull(dto, record);
                    record.setUserId(Long.parseLong(dto.getUserId()));
                    record.setTokenId(dto.getAirdropTokenId());
                    record.setTokenAmount(dto.getAirdropTokenNum());

                    record.setHaveTokenId(dto.getHaveTokenId());
                    record.setHaveTokenAmount(dto.getHaveTokenNum());

                    records.add(record.build());

                    if (++counter % BATCH_LIMIT == 0) {
                        addTmplRecords(airdropInfoReply.getAirdropId(), param.getBrokerId(), records);
                        records = new ArrayList<>();
                    }
                }

                if (!CollectionUtils.isEmpty(records)) {
                    addTmplRecords(airdropInfoReply.getAirdropId(), param.getBrokerId(), records);
                }
            } catch (Exception e) {
                updateAirdropStatus(airdropInfoReply.getAirdropId(), adminUser.getOrgId(), AirdropClient.STATUS_CLOSED);
                if (e instanceof BizException) {
                    throw e;
                } else {
                    log.error("", e);
                    throw new BizException(ErrorCode.ERROR);
                }
            } finally {
                redisTemplate.delete(AIRDROP_ADD_TMPL_LOCK_KEY + airdropInfoReply.getAirdropId());
            }


        } else {
            airdropBuilder.setAirdropTokenNum(param.getAirdropTokenNum().toString());
            String haveTokenNum = null == param.getHaveTokenNum() ? "0" : param.getHaveTokenNum().toString();
            airdropBuilder.setHaveTokenNum(haveTokenNum);

            // 只针对免费空投，分叉空投是持币空投
            if (param.getType() == 1) {
                BigDecimal usdtRate = getUSDTRate(param.getBrokerId(), param.getAirdropTokenId());
                overUSDTLimit(param.getBrokerId(), null, param.getAirdropTokenId(), param.getAirdropTokenNum(), usdtRate);
            }

            //accountId实际填充的是userId
            String userIds = param.getUserAccountIds();

            if (!StringUtils.isEmpty(userIds)) {
                userIds = userIds.replaceAll("\r\n", "")
                        .replaceAll("\n", "");

                Set<Long> userIdSet = Splitter.on(",").splitToList(userIds).stream()
                        .filter(u -> !getUserIdStr(u.trim()).equals(""))
                        .map(i -> Long.parseLong(getUserIdStr(i.trim())))
                        .collect(Collectors.toSet());
                if (CollectionUtils.isEmpty(userIdSet)) {
                    return new Combo2<>(true, new ArrayList<>());
                }
                userIds = String.join(",", userIdSet.stream().map(u -> u + "").collect(Collectors.toList()));

                List<Long> errorUserIds = getErrorUserIds(param.getBrokerId(), Lists.newArrayList(userIdSet));
                if (!CollectionUtils.isEmpty(errorUserIds)) {
                    return new Combo2<>(false, errorUserIds);
                }

                List<UserAccountMap> list = listUserMainAccount(param.getBrokerId(), Lists.newArrayList(userIdSet));

                if (CollectionUtils.isEmpty(list)) {
                    log.error("User account is empty,brokerId={}", param.getBrokerId());
                    return new Combo2<>(false, new ArrayList<>());
                }
                if (userIdSet.size() != list.size()) {
                    log.error("Airdrop UserIds Error: exist wrong userId. brokerId={}", param.getBrokerId());
                    throw new BizException(ErrorCode.AIRDROP_WRONG_USERID_ERROR);
                }

                List<Long> accountIds = list.stream()
                        .filter(i -> Objects.nonNull(i.getAccountId()))
                        .map(i -> i.getAccountId()).collect(Collectors.toList());

                airdropBuilder.setUserAccountIds(Joiner.on(",").join(accountIds));
                airdropBuilder.setUserIds(userIds);
            }
            airdropBuilder.setAdminId(adminUser.getId());
            airdropBuilder.setAdminUserName(adminUser.getUsername());
            airdropBuilder.setAdminRealName(adminUser.getRealName());
            airdropClient.createAirdropInfo(airdropBuilder.build());
        }


        return new Combo2<>(true, new ArrayList<>());
    }



    private void addTmplRecords(long airdropId, long brokerId, List<TmplRecord> records) {
        log.info("size={}", records.size());
        List<Long> userIds = records.stream().map(r -> r.getUserId()).distinct().collect(Collectors.toList());
        List<UserAccountMap> uams = listUserMainAccount(brokerId, userIds);

        List<TmplRecord> params = new ArrayList<>();
        for (TmplRecord record : records) {
            long userId = record.getUserId();
            Optional<UserAccountMap> optional = uams.stream()
                    .filter(uam -> uam.getUserId() == userId)
                    .findFirst();
            if (optional.isPresent()) {
                record = record.toBuilder().setAccountId(optional.get().getAccountId()).build();
            }
            params.add(record);
        }


        AddTmplRecordRequest request = AddTmplRecordRequest.newBuilder()
                .addAllTmplRecordList(params)
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .build();
        airdropClient.addTmplRecord(request);
    }

    /**
     * 获取空投对应的转账分组信息
     *
     * @param airdropId
     * @param brokerId
     * @return
     */
    private List<TransferGroupInfo> listAllTransferGroup(Long airdropId, Long brokerId) {
        ListAllTransferGroupRequest request = ListAllTransferGroupRequest.newBuilder()
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .build();
        return airdropClient.listAllTransferGroup(request).getTransferGroupInfosList();
    }

    public List<Long> verifyAccountIds(Long brokerId, List<Long> accountIds) {
        List<Long> collect = new ArrayList<>();
        Header header = Header.newBuilder()
                .setOrgId(brokerId)
                .build();
        VerifyBrokerAccountRequest request = VerifyBrokerAccountRequest.newBuilder()
                .addAllAccountIds(accountIds)
                .setHeader(header)
                .build();
        VerifyBrokerAccountResponse response = brokerAccountClient.verifyBrokerAccount(request);
        List<SimpleAccount> accountsList = response.getAccountsList();
        if (!CollectionUtils.isEmpty(accountIds)) {
            collect = accountsList.stream().map(sa -> {
                return sa.getAccountId();
            }).collect(Collectors.toList());
        }
        return collect;
    }

    /**
     * 根据组id获取对应的发送数据
     *
     * @param airdropId
     * @param brokerId
     * @param groupId
     * @return
     */
    private List<TransferRecord> listTransferRecordByGroupId(Long airdropId, Long brokerId, Long groupId) {
        ListTransferRecordRequest resquest = ListTransferRecordRequest.newBuilder()
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .setGroupId(groupId)
                .build();
        return airdropClient.listTransferRecordByGroupId(resquest).getTransferRecordList();
    }

    /**
     * 更新空投状态
     *
     * @param airdropId
     * @param brokerId
     * @param status
     * @return
     */
    private Boolean updateAirdropStatus(Long airdropId, Long brokerId, Integer status) {
        UpdateAirdropStatusRequest request = UpdateAirdropStatusRequest.newBuilder()
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .setStatus(status)
                .build();
        return airdropClient.updateAirdropStatus(request).getResult();
    }

    @Override
    public Boolean closeAirdrop(Long airdropId, Long brokerId) {
        // 关闭前要确认下当前空投的状态
        AirdropInfo airdropInfo = getAirdropInfo(airdropId, brokerId);
        if (null == airdropInfo || airdropInfo.getStatus() != AirdropClient.STATUS_AUDIT_PASSED) {
            return false;
        } else {
            return updateAirdropStatus(airdropId, brokerId, AirdropClient.STATUS_CLOSED);
        }
    }

    /**
     * 更新分组转账状态
     *
     * @param airdropId
     * @param brokerId
     * @param status
     * @param transferGroupId
     * @return
     */
    private Boolean updateTransferGroupStatus(Long airdropId, Long brokerId, Integer status, Long transferGroupId) {
        UpdateTransferGroupStatusRequest request = UpdateTransferGroupStatusRequest.newBuilder()
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .setStatus(status)
                .setGroupId(transferGroupId)
                .build();
        return airdropClient.updateTransferGroupStatus(request).getResult();
    }

    /**
     * @param registerTime
     * @param fromId
     * @param brokerId
     * @param limit
     * @return
     */
    @Override
    public GetBrokerAccountListResponse getAllUserPageable(Long registerTime, Long fromId, Long brokerId, Integer limit) {
        Header header = Header.newBuilder()
                .setOrgId(brokerId)
                .build();
        GetBrokerAccountListRequest request = GetBrokerAccountListRequest.newBuilder()
                .setHeader(header)
                .setBeginTime(0)
                .setEndTime(registerTime)
                .setFromId(fromId)
                .setLimit(limit)
                .build();
        GetBrokerAccountListResponse reply = brokerAccountClient.getBrokerAccountList(request);
        return reply;
    }

    private List<UserAccountMap> listUserMainAccount(long brokerId, List<Long> userIds) {
        ListUserAccountResponse resp = brokerUserClient.listUserAccount(brokerId, userIds);
        List<UserAccountMap> list = resp.getAccountInfoList();
        list = list.stream().filter(u -> u.getAccountIndex() == 0).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<Long> getErrorUserIds(long brokerId, List<Long> userIds) {
        List<UserAccountMap> list = listUserMainAccount(brokerId, userIds);
        List<Long> errorList = new ArrayList<>();
        for (Long userId : userIds) {
            boolean existed = false;
            for (UserAccountMap accountMap : list) {
                if (accountMap.getUserId() == userId) {
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                errorList.add(userId);
            }
        }
        return errorList;
    }

    /**
     * 分页获取指定用户的account id
     *
     * @param airdropInfo
     * @param index
     * @param limit
     * @return
     */
    private static List<Long> getSpecialUserPageable(AirdropInfo airdropInfo, Integer index, Integer limit) {
        List<Long> accountIds = new ArrayList<>();
        String userAccountIdStr = airdropInfo.getUserAccountIds();
        userAccountIdStr = userAccountIdStr.replaceAll("\\s*", "");
        if (StringUtils.isEmpty(userAccountIdStr)) {
            return accountIds;
        }
        String[] accountIdArray = userAccountIdStr.split(SPLIT_REGEX);
        Integer length;
        if (index >= accountIdArray.length || accountIdArray.length < 1) {
            return accountIds;
        } else if (index + limit > accountIdArray.length) {
            length = accountIdArray.length;
        } else {
            length = index + limit;
        }
        for (Integer i = index; i < length; i++) {
            if (!StringUtils.isEmpty(accountIdArray[i])) {
                accountIds.add(new Long(accountIdArray[i]));
            }
        }
        return accountIds;
    }

    /**
     * 根据转账记录过滤已经转过钱的account id
     *
     * @return
     */
    private List<Long> transferRecordFilter(List<Long> accountIds, Long airdropId) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return new ArrayList();
        }
        TransferRecordFilterRequest request = TransferRecordFilterRequest.newBuilder()
                .setAirdropId(airdropId)
                .addAllAccountIds(accountIds)
                .build();
        TransferRecordFilterReply reply = airdropClient.transferRecordFilter(request);
        return reply.getAccountIdsList();
    }

    /**
     * 获取机构账户余额
     *
     * @param orgAccountId
     * @param tokenId
     */
    private BigDecimal getOrgBalance(Long orgAccountId, String tokenId, Long orgId) {
        GetBalanceDetailRequest request = GetBalanceDetailRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setAccountId(orgAccountId)
                .addAllTokenId(Collections.singletonList(tokenId))
                .build();

        BalanceDetailList replay = balanceClient.getBalanceDetail(request);
        List<BalanceDetail> balanceDetailsList = replay.getBalanceDetailsList();
        if (!CollectionUtils.isEmpty(balanceDetailsList)) {
            BalanceDetail balanceDetail = balanceDetailsList.get(0);
            Decimal decimal = balanceDetail.getAvailable();
            BigDecimal available = DecimalUtil.toBigDecimal(decimal);
            return available;
        }
        return new BigDecimal(0);
    }

    /**
     * 获取资产快照
     *
     * @param accountIds
     * @param tokenId
     * @param snapshotTime
     */
    private Map<Long, BigDecimal> getUserAsset(AirdropInfo airdropInfo, List<Long> accountIds, String tokenId, Long snapshotTime) {
        AssetRequest request = AssetRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(airdropInfo.getBrokerId()))
                .addAllAccountId(accountIds)
                .setDateTime(snapshotTime)
                .setTokenId(tokenId)
                .build();
        Map<Long, BigDecimal> result = new HashMap<>();
        try {
            log.info("Get Asset Snapshot From Clear Begin. Sanpshot Time:{}, Account Ids {}, Token Id {}", snapshotTime, accountIds, tokenId);
            AssetResponse reply = accountClient.getAsset(request);
            List<AssetResponse.AccountAsset> accountAssetList = reply.getAccountAssetList();
            if (!CollectionUtils.isEmpty(accountAssetList)) {
                log.info("Get Asset Snapshot From Clear: Success. Sanpshot Time:{}, Token Id {}, Asset List Size {}", snapshotTime, tokenId, accountAssetList.size());
                for (AssetResponse.AccountAsset asset : accountAssetList) {
                    if (new BigDecimal(asset.getAmount()).compareTo(BigDecimal.ZERO) > 0) {
                        result.put(asset.getAccountId(), new BigDecimal(asset.getAmount()));
                        log.info("Asset Log: {} {}", asset.getAccountId(), asset.getAmount());
                    }
                }
                //资产快照入库
                saveUserAssetRecord(airdropInfo.getBrokerId(), airdropInfo.getHaveTokenId(), airdropInfo.getSnapshotTime(), result, airdropInfo);
            }
        } catch (Exception e) {
            log.error("Get Asset Snapshot From Clear: Error. Sanpshot Time:{}, Account Ids {}, Token Id {}", snapshotTime, accountIds, tokenId, e);
        }
        return result;
    }

    /**
     * 计算空投数量
     *
     * @param airdropInfo
     * @param balance
     * @return
     */
    private BigDecimal countAirdropTokenNum(AirdropInfo airdropInfo, BigDecimal balance) {
        if (BigDecimal.ZERO.compareTo(new BigDecimal(airdropInfo.getHaveTokenNum())) == 0 || BigDecimal.ZERO.compareTo(balance) == 0) {
            log.info("Airdrop countAirdropTokenNum is 0: airdrop id => {}, balance => {}.", airdropInfo.getId(), balance.toPlainString());
            return new BigDecimal(0);
        }
        BigDecimal airdropNum = new BigDecimal(airdropInfo.getAirdropTokenNum()).multiply(balance.divide(new BigDecimal(airdropInfo.getHaveTokenNum())));
        // 获取投放token的最小精度
        Integer tokenScale = getTokenScale(airdropInfo.getAirdropTokenId(), airdropInfo.getBrokerId());
        airdropNum = airdropNum.setScale(tokenScale, BigDecimal.ROUND_DOWN);
        return airdropNum;
    }

    /**
     * 获取token最小精度，默认为18位
     *
     * @param tokenId
     * @return
     */
    private Integer getTokenScale(String tokenId, Long orgId) {
        GetTokenRequest request = GetTokenRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setTokenId(tokenId)
                .build();
        io.bhex.base.token.TokenDetail token = tokenClient.getToken(request);
        if (null != token) {
            log.info("Airdrop getTokenScale: tokenId => {}, MinPrecision => {}", tokenId, token.getMinPrecision());
            return token.getMinPrecision();
        }
        log.info("Airdrop getTokenScale: tokenId => {}, MinPrecision => {}", tokenId, 18);
        return 18;
    }

    @Override
    public TokenDetail getBrokerTokenDetail(String tokenId, Long brokerId) {
        QueryTokenReply queryTokenReply = tokenClient.queryToken(0, 1, TokenCategory.MAIN_CATEGORY_VALUE, tokenId, "", brokerId);
        List<TokenDetail> tokenDetailsList = queryTokenReply.getTokenDetailsList();
        log.info("broker:{} token:{} detail:{}", brokerId, tokenId, tokenDetailsList);
        if (!CollectionUtils.isEmpty(tokenDetailsList)) {
            return tokenDetailsList.get(0);
        }
        return null;
    }

    /**
     * 批量转账
     *
     * @param airdropInfo
     * @param userTransferList
     */
    private Boolean batchTransfer(AirdropInfo airdropInfo, Long groupId, List<BatchTransferItem> userTransferList) {
        BatchTransferResponse response = null;
        try {
            BatchTransferRequest request = BatchTransferRequest.newBuilder()
                    .setBaseRequest(BaseReqUtil.getBaseRequest(airdropInfo.getBrokerId()))
                    .setDelayExecute(false)
                    .addAllTransferTo(userTransferList)
                    .setSourceAccountId(airdropInfo.getAccountId())
                    .setSourceAccountType(AccountType.OPERATION_ACCOUNT)
                    .setSourceOrgId(airdropInfo.getBrokerId())
                    .setSubject(BusinessSubject.AIRDROP)
                    .setClientTransferId(groupId)
                    .build();
            log.info("req:{}", TextFormat.shortDebugString(request));
            response = balanceTransferClient.batchTransfer(request);
            if (response.getErrorCode() == 0) {
                log.info("Batch Transfer Success. {} => {}", response, airdropInfo.getId());
                return true;
            } else {
                log.error("Batch Transfer Error. req:{} {} => {}", request, response.getErrorCode(), airdropInfo);
                return false;
            }
        } catch (StatusRuntimeException e) {
            log.error("Batch Transfer Error. {} => {}", airdropInfo, printStatusRuntimeException(e), e);
            return false;
        } catch (Exception e) {
            log.error("Batch Transfer Error. {} => {}", airdropInfo, e);
            return false;
        }
    }

    private String printStatusRuntimeException(StatusRuntimeException e) {
        return String.format("status.code=%s, status.desc=%s, keys=%s",
                e.getStatus().getCode(),
                e.getStatus().getDescription(),
                e.getTrailers() != null ? e.getTrailers().keys() : "trailers is null");
    }

    /**
     * 用户资产快照入库
     *
     * @param userAssetMap
     */
    private void saveUserAssetRecord(long brokerId, String haveTokenId, long snapshotTime, Map<Long, BigDecimal> userAssetMap, AirdropInfo airdropInfo) {
        List<AssetSnapshot> assetSnapshots = new ArrayList<>();
        userAssetMap.forEach((accountId, asset) -> {
            if (asset.compareTo(BigDecimal.ZERO) > 0) {
                assetSnapshots.add(AssetSnapshot.newBuilder()
                        .setAccountId(accountId)
                        .setAssetAmount(asset.toString())
//                    .setAirdropId(airdropInfo.)
                        .setBrokerId(brokerId)
                        .setSnapshotTime(snapshotTime)
                        .setTokenId(haveTokenId)
                        .build());
            }
        });
        AddAssetSnapshotRequest.Builder builder = AddAssetSnapshotRequest.newBuilder();
        builder.addAllAssetSnapshotList(assetSnapshots);
        Boolean isOk = airdropClient.addAssetSnapshot(builder.build()).getResult();
        if (!isOk) {
            log.error("Save User Asset Record Error. {}", airdropInfo);
        }
    }

    /**
     * 转账记录入库
     *
     * @param airdropInfo
     * @param transferRecords
     */
    private void saveTransferRecord(AirdropInfo airdropInfo, List<TransferRecord> transferRecords) {
        if (CollectionUtils.isEmpty(transferRecords)) {
            return;
        }
        AddTransferRecordRequest request = AddTransferRecordRequest.newBuilder()
                .setAirdropId(airdropInfo.getId())
                .setBrokerId(airdropInfo.getBrokerId())
                .setTokenId(transferRecords.get(0).getTokenId())
                .addAllTransferRecordList(transferRecords)
                .build();
        Boolean isOk = airdropClient.addTransferRecord(request).getResult();
        if (!isOk) {
            log.error("Save Transfer Record Error. {}", airdropInfo);
        }
    }

    private String getUserId(String userIdStr) {
        Pattern pattern = Pattern.compile("\\d{1,}");
        Matcher matcher = pattern.matcher(userIdStr);
        if (matcher.find()) {
            return matcher.group();
        }
        return userIdStr;
    }

    private String getUserIdStr(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    @Override
    public List<AirdropTmplDTO> convertTmpl(XSSFWorkbook workbook) {
        List<String> titles = new ArrayList<>();
        XSSFSheet sheetAt = workbook.getSheetAt(0);

        List<AirdropTmplDTO> airdropList = new ArrayList<>();
        for (Row row : sheetAt) {
            if (row.getRowNum() == 0) {
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    String column = row.getCell(i).toString().trim();
                    titles.add(column);
                }
            } else {
                Cell userIdCell = row.getCell(titles.indexOf(AirdropTmplDTO.UID_COLUMN));
                String userId = userIdCell != null ? getUserId(userIdCell.toString()) : "";

                Cell tokenCell = row.getCell(titles.indexOf(AirdropTmplDTO.TOKEN_COLUMN));
                String token = tokenCell != null ? tokenCell.toString().trim().replaceAll("\t", "") : "";

                Cell quantityCell = row.getCell(titles.indexOf(AirdropTmplDTO.QUANTITY_COLUMN));
                String quantity = quantityCell != null ? quantityCell.toString().trim().replaceAll("\t", "") : "";

                if (userId.trim().equals("") && token.equals("")) {
                    break;
                }

                AirdropTmplDTO dto = new AirdropTmplDTO();
                if (titles.indexOf(AirdropTmplDTO.LINE_ID_COLUMN) != -1) {
                    String lineId = row.getCell(titles.indexOf(AirdropTmplDTO.LINE_ID_COLUMN)).toString().trim();
                    dto.setTmplLineId(Integer.parseInt(lineId));
                }
                dto.setUserId(userId);
                dto.setAirdropTokenId(token);
                dto.setAirdropTokenNum(quantity);

                if (titles.indexOf(AirdropTmplDTO.HAVE_TOKEN_COLUMN) != -1) {
                    String haveTokenId = row.getCell(titles.indexOf(AirdropTmplDTO.HAVE_TOKEN_COLUMN)).toString().trim();
                    String haveTokenQuantity = row.getCell(titles.indexOf(AirdropTmplDTO.HAVE_QUANTITY_COLUMN)).toString().trim();
                    dto.setHaveTokenId(haveTokenId);
                    dto.setHaveTokenNum(haveTokenQuantity);
                } else {
                    dto.setHaveTokenId("");
                    dto.setHaveTokenNum("0");
                }
                airdropList.add(dto);
            }
        }
        return airdropList;
    }


    @Override
    public List<TmplRecord> listTmplRecords(long brokerId, long airdropId, long groupId, String orderBy, int limit) {
        ListTmplRecordsRequest.Builder builder = ListTmplRecordsRequest.newBuilder()
                .setAirdropId(airdropId)
                .setBrokerId(brokerId)
                .setGroupId(groupId)
                .setLimit(limit);

        if (!StringUtils.isEmpty(orderBy)) {
            builder.setOrderByColumn(orderBy);
        }
        ListTmplRecordsReply replay = airdropClient.listTmplRecords(builder.build());
        List<TmplRecord> tmplRecords = replay.getTmplRecordList();
        if (CollectionUtils.isEmpty(tmplRecords)) {
            return new ArrayList<>();
        }
        return replay.getTmplRecordList();
    }

    private static final Cache<Long, BigDecimal> usdtLimitCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS).build();

    private void overUSDTLimit(Long brokerId, Long airdropId, String tokenId, BigDecimal airdropTokenNum, BigDecimal usdtRate) {

        if (Objects.isNull(usdtRate) || usdtRate.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Airdrop error: rate is 0 or null. rate => {}. tokenId => {}, brokerId => {}.", usdtRate, tokenId, brokerId);
            if (Objects.nonNull(airdropId) && airdropId != 0L) {
                updateAirdropStatus(airdropId, brokerId, AirdropClient.STATUS_CLOSED);
            }
            throw new BizException(ErrorCode.EXCHANGE_RATE_ERROR);
        }
        BigDecimal usdtValue = airdropTokenNum.multiply(usdtRate);

        BigDecimal limitUsdt = usdtLimitCache.getIfPresent(brokerId);
        if (limitUsdt == null) {
            limitUsdt = AIRDROP_USDT_LIMIT;
            String limitUsdtStr = baseConfigService.getBrokerConfig(brokerId, "airdrop.config", "airdrop.limit.usdt", "");
            if (!StringUtils.isEmpty(limitUsdtStr)) {
                log.info("broker:{} limit:{}USDT", brokerId, limitUsdtStr);
                limitUsdt = new BigDecimal(limitUsdtStr);
            }
            usdtLimitCache.put(brokerId, limitUsdt);
        }
        log.info("broker:{} limit:{}USDT", brokerId, limitUsdt);

        if (usdtValue.compareTo(limitUsdt) > 0) {
            log.info("Airdrop error: over USDT Limit. rate => {}. tokenId => {}, brokerId => {}, usdtValue => {}.", usdtRate, tokenId, brokerId, usdtValue);
            if (Objects.nonNull(airdropId) && airdropId != 0L) {
                updateAirdropStatus(airdropId, brokerId, AirdropClient.STATUS_CLOSED);
            }
            throw new BizException(ErrorCode.OVER_AMOUNT_LIMIT);
        }
    }

    private BigDecimal getUSDTRate(String tokenId, Map<String, Rate> ratesMap) {
        if (Objects.isNull(ratesMap) || Objects.isNull(ratesMap.get(tokenId))) {
            throw new BizException(ErrorCode.EXCHANGE_RATE_ERROR);
        }
        Rate rate = ratesMap.get(tokenId);
        if (Objects.nonNull(rate)) {
            Decimal usdtRate = rate.getRatesMap().get(USDT_TOKEN_ID);
            if (Objects.nonNull(usdtRate)) {
                return DecimalUtil.toBigDecimal(usdtRate);
            }
        }
        return null;
    }

    private BigDecimal getUSDTRate(Long brokerId, String tokenId) {
        Map<String, Rate> ratesMap = getRate(brokerId, Arrays.asList(tokenId));
        if (Objects.isNull(ratesMap) || Objects.isNull(ratesMap.get(tokenId))) {
            throw new BizException(ErrorCode.EXCHANGE_RATE_ERROR);
        }
        Rate rate = ratesMap.get(tokenId);
        if (Objects.nonNull(rate)) {
            Decimal usdtRate = rate.getRatesMap().get(USDT_TOKEN_ID);
            if (Objects.nonNull(usdtRate)) {
                return DecimalUtil.toBigDecimal(usdtRate);
            }
        }
        return null;
    }

    private Map<String, Rate> getRate(Long brokerId, List<String> tokenIdList) {
        try {
            List<TokenDetail> tokenDetails = new ArrayList<>();
            tokenIdList.forEach(t -> {
                tokenDetails.add(getBrokerTokenDetail(t, brokerId));
            });
            List<io.bhex.base.quote.Token> quoteToken = tokenDetails.stream().map(token -> io.bhex.base.quote.Token.newBuilder()
                    .setExchangeId(token.getExchangeId()).setToken(token.getTokenId()).build()).collect(Collectors.toList());
            GetRatesRequest request = GetRatesRequest.newBuilder()
                    .addAllTokens(quoteToken)
                    .build();
            GetLegalCoinRatesReply reply = grpcQuoteService.getRates(request);
            Map<String, Rate> ratesMap = reply.getRatesMapMap();
            log.info("getRate: rate map => {}.", JsonUtil.defaultGson().toJson(ratesMap));
            return ratesMap;
        } catch (Exception e) {
            log.error("Get token FXRate occurred a exception", e);
            return null;
        }
    }
}

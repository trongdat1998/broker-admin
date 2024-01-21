/*
 ************************************
 * @项目名称: broker
 * @文件名称: GrcpClientConfig
 * @Date 2018/05/22
 * @Author will.zhao@bhex.io
 * @Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 **************************************
 */
package io.bhex.broker.admin.grpc.client.config;

import io.bhex.base.account.AccountServiceGrpc;
import io.bhex.base.account.BalanceServiceGrpc;
import io.bhex.base.account.BatchTransferServiceGrpc;
import io.bhex.base.account.OrderServiceGrpc;
import io.bhex.base.account.OrgServiceGrpc;
import io.bhex.base.account.WithdrawAdminServiceGrpc;
import io.bhex.base.account.WithdrawalServiceGrpc;
import io.bhex.base.admin.AdminOrgContractServiceGrpc;
import io.bhex.base.admin.AdminRoleAuthServiceGrpc;
import io.bhex.base.admin.AdminUserIpWhitelistServiceGrpc;
import io.bhex.base.admin.SecurityServiceGrpc;
import io.bhex.base.admin.common.AdminUserServiceGrpc;
import io.bhex.base.admin.common.BrokerAccountTradeFeeSettingServiceGrpc;
import io.bhex.base.admin.common.BrokerTradeFeeSettingServiceGrpc;
import io.bhex.base.admin.common.BusinessLogServiceGrpc;
import io.bhex.base.admin.common.CommissionServiceGrpc;
import io.bhex.base.admin.common.CountryServiceGrpc;
import io.bhex.base.bhadmin.AdminSymbolApplyServiceGrpc;
import io.bhex.base.bhadmin.AdminSymbolTransferServiceGrpc;
import io.bhex.base.bhadmin.AdminTokenApplyServiceGrpc;
import io.bhex.base.common.BaseConfigServiceGrpc;
import io.bhex.base.common.MessageServiceGrpc;
import io.bhex.base.exadmin.BrokerSmsTemplateServiceGrpc;
import io.bhex.base.grpc.client.channel.IGrpcClientPool;
import io.bhex.base.quote.QuoteServiceGrpc;
import io.bhex.base.token.SaasTokenServiceGrpc;
import io.bhex.base.token.SymbolServiceGrpc;
import io.bhex.base.token.TokenServiceGrpc;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.common.entity.GrpcChannelInfo;
import io.bhex.broker.common.entity.GrpcClientProperties;
import io.bhex.broker.grpc.activity.contract.competition.AdminContractCompetitionServiceGrpc;
import io.bhex.broker.grpc.activity.experiencefund.ExperienceFundServiceGrpc;
import io.bhex.broker.grpc.activity.lockInterest.ActivityLockInterestServiceGrpc;
import io.bhex.broker.grpc.admin.AdminActivityServiceGrpc;
import io.bhex.broker.grpc.admin.AdminAirdropServiceGrpc;
import io.bhex.broker.grpc.admin.AdminAnnouncementServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBannerServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerConfigServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerExchangeServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerNotifyServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerOptionServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerServiceGrpc;
import io.bhex.broker.grpc.admin.AdminBrokerTaskConfigServiceGrpc;
import io.bhex.broker.grpc.admin.AdminContractApplicationServiceGrpc;
import io.bhex.broker.grpc.admin.AdminCurrencyServiceGrpc;
import io.bhex.broker.grpc.admin.AdminCustomLabelServiceGrpc;
import io.bhex.broker.grpc.admin.AdminStatisticServiceGrpc;
import io.bhex.broker.grpc.admin.AdminSymbolServiceGrpc;
import io.bhex.broker.grpc.admin.AdminTokenServiceGrpc;
import io.bhex.broker.grpc.admin.AdminUserVerifyServiceGrpc;
import io.bhex.broker.grpc.admin.AdminWithdrawOrderServiceGrpc;
import io.bhex.broker.grpc.admin.BrokerUserServiceGrpc;
import io.bhex.broker.grpc.agent.AgentServiceGrpc;
import io.bhex.broker.grpc.airdrop.AutoAirdropServiceGrpc;
import io.bhex.broker.grpc.app_config.AppConfigServiceGrpc;
import io.bhex.broker.grpc.app_push.AppPushServiceGrpc;
import io.bhex.broker.grpc.auditflow.AdminAuditFlowServiceGrpc;
import io.bhex.broker.grpc.basic.BasicServiceGrpc;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteListConfigServiceGrpc;
import io.bhex.broker.grpc.common_ini.CommonIniServiceGrpc;
import io.bhex.broker.grpc.convert.ConvertServiceGrpc;
import io.bhex.broker.grpc.deposit.DepositServiceGrpc;
import io.bhex.broker.grpc.fee.FeeServiceGrpc;
import io.bhex.broker.grpc.function.config.BrokerFunctionConfigServiceGrpc;
import io.bhex.broker.grpc.gateway.OrgInstanceServiceGrpc;
import io.bhex.broker.grpc.invite.InviteServiceGrpc;
import io.bhex.broker.grpc.margin.MarginPositionServiceGrpc;
import io.bhex.broker.grpc.margin.MarginServiceGrpc;
import io.bhex.broker.grpc.news.NewsServiceGrpc;
import io.bhex.broker.grpc.notice.NoticeTemplateServiceGrpc;
import io.bhex.broker.grpc.order.FuturesOrderServiceGrpc;
import io.bhex.broker.grpc.order.ShareConfigServiceGrpc;
import io.bhex.broker.grpc.org_api.OrgApiKeyServiceGrpc;
import io.bhex.broker.grpc.otc.OTCConfigServiceGrpc;
import io.bhex.broker.grpc.otc.third.party.OtcThirdPartyServiceGrpc;
import io.bhex.broker.grpc.red_packet.RedPacketAdminServiceGrpc;
import io.bhex.broker.grpc.staking.AdminStakingProductServiceGrpc;
import io.bhex.broker.grpc.statistics.OdsServiceGrpc;
import io.bhex.broker.grpc.statistics.StatisticsServiceGrpc;
import io.bhex.broker.grpc.sub_business_subject.SubBusinessSubjectServiceGrpc;
import io.bhex.broker.grpc.user.UserServiceGrpc;
import io.bhex.broker.grpc.user.level.UserLevelServiceGrpc;
import io.bhex.broker.grpc.useraction.UserActionLogServiceGrpc;
import io.bhex.broker.grpc.withdraw.WithdrawServiceGrpc;
import io.bhex.ex.otc.OTCAdminServiceGrpc;
import io.bhex.ex.otc.OTCItemServiceGrpc;
import io.bhex.ex.otc.OTCMessageServiceGrpc;
import io.bhex.ex.otc.OTCOrderServiceGrpc;
import io.bhex.ex.otc.OTCPaymentTermServiceGrpc;
import io.bhex.ex.otc.OTCUserServiceGrpc;
import io.grpc.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("grpcConfig")
public class GrpcClientConfig extends GrpcConfig {

    public static final String BH_SERVER_CHANNEL_NAME = "bhChannel";
    public static final String BROKER_SERVER_CHANNEL_NAME = "brokerServerChannel";
    public static final String CLEAR_CHANNEL_NAME = "clearChannel";
    public static final String QUOTE_CHANNEL_NAME = "quoteChannel";
    public static final String OTC_SERVER_CHANNEL_NAME = "otcServerChannel";
    public static final String ADMIN_GATEWAY_CHANNEL_NAME = "adminGatewayChannel";

    @Resource
    GrpcClientProperties grpcClientProperties;

    @Resource
    IGrpcClientPool pool;

    Long stubDeadline;

    Long shortStubDeadline;

    Long futureTimeout;

    @Override
    @PostConstruct
    public void init() {
        stubDeadline = grpcClientProperties.getStubDeadline();
        shortStubDeadline = grpcClientProperties.getShortStubDeadline();
        futureTimeout = grpcClientProperties.getFutureTimeout();
        List<GrpcChannelInfo> channelInfoList = grpcClientProperties.getChannelInfo();
        for (GrpcChannelInfo channelInfo : channelInfoList) {
            pool.setShortcut(channelInfo.getChannelName(), channelInfo.getHost(), channelInfo.getPort());
        }
    }

    @Override
    public AdminRoleAuthServiceGrpc.AdminRoleAuthServiceBlockingStub adminRoleAuthServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminRoleAuthServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public SecurityServiceGrpc.SecurityServiceBlockingStub securityServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return SecurityServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public CommissionServiceGrpc.CommissionServiceBlockingStub commissionServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return CommissionServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public AdminUserServiceGrpc.AdminUserServiceBlockingStub adminUserServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminUserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public BusinessLogServiceGrpc.BusinessLogServiceBlockingStub businessLogServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return BusinessLogServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public AdminUserIpWhitelistServiceGrpc.AdminUserIpWhitelistServiceBlockingStub adminUserIpWhitelistServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return AdminUserIpWhitelistServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public CountryServiceGrpc.CountryServiceBlockingStub countryServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return CountryServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public SaasTokenServiceGrpc.SaasTokenServiceBlockingStub saasTokenServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return SaasTokenServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public BalanceServiceGrpc.BalanceServiceBlockingStub balanceServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return BalanceServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return AccountServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub brokerTradeFeeSettingServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return BrokerTradeFeeSettingServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public BrokerSmsTemplateServiceGrpc.BrokerSmsTemplateServiceBlockingStub brokerSmsTemplateServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return BrokerSmsTemplateServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public io.bhex.base.clear.CommissionServiceGrpc.CommissionServiceBlockingStub clearCommissionServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return io.bhex.base.clear.CommissionServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public MessageServiceGrpc.MessageServiceBlockingStub messageServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return MessageServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    @Override
    public BrokerAccountTradeFeeSettingServiceGrpc.BrokerAccountTradeFeeSettingServiceBlockingStub brokerAccountTradeFeeSettingServiceBlockingStub(String channelName) {
        Channel channel = this.pool.borrowChannel(channelName);
        return BrokerAccountTradeFeeSettingServiceGrpc.newBlockingStub(channel).withDeadlineAfter(this.stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCItemServiceGrpc.OTCItemServiceBlockingStub otcItemServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCItemServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCUserServiceGrpc.OTCUserServiceBlockingStub otcUserServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCUserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCOrderServiceGrpc.OTCOrderServiceBlockingStub otcOrderServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCOrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCMessageServiceGrpc.OTCMessageServiceBlockingStub otcMessageServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCMessageServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCAdminServiceGrpc.OTCAdminServiceBlockingStub otcAdminServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCAdminServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.ex.otc.OTCConfigServiceGrpc.OTCConfigServiceBlockingStub exOtcConfigServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.ex.otc.OTCConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCConfigServiceGrpc.OTCConfigServiceBlockingStub otcConfigServiceBlockingStub(String channelName){
        Channel channel = pool.borrowChannel(channelName);
        return OTCConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public QuoteServiceGrpc.QuoteServiceBlockingStub quoteServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return QuoteServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OrgServiceGrpc.OrgServiceBlockingStub orgServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return OrgServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }


    public BalanceServiceGrpc.BalanceServiceBlockingStub bhBalanceServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BalanceServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public TokenServiceGrpc.TokenServiceBlockingStub tokenServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return TokenServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public WithdrawalServiceGrpc.WithdrawalServiceBlockingStub withdrawalServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return WithdrawalServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.broker.grpc.account.AccountServiceGrpc.AccountServiceBlockingStub brokerAccountServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.broker.grpc.account.AccountServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }


    public io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub brokerOrderServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.broker.grpc.order.OrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return OrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminSymbolApplyServiceGrpc.AdminSymbolApplyServiceBlockingStub adminSymbolApplyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminSymbolApplyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminSymbolTransferServiceGrpc.AdminSymbolTransferServiceBlockingStub adminSymbolTransferServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminSymbolTransferServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminActivityServiceGrpc.AdminActivityServiceBlockingStub adminActivityServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminActivityServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public ActivityLockInterestServiceGrpc.ActivityLockInterestServiceBlockingStub activityLockInterestServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return ActivityLockInterestServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminAnnouncementServiceGrpc.AdminAnnouncementServiceBlockingStub adminAnnouncementServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminAnnouncementServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AppConfigServiceGrpc.AppConfigServiceBlockingStub appConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AppConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AutoAirdropServiceGrpc.AutoAirdropServiceBlockingStub autoAirdropServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AutoAirdropServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBannerServiceGrpc.AdminBannerServiceBlockingStub adminBannerServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBannerServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public BasicServiceGrpc.BasicServiceBlockingStub basicServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BasicServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminCurrencyServiceGrpc.AdminCurrencyServiceBlockingStub adminCurrencyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminCurrencyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public FeeServiceGrpc.FeeServiceBlockingStub feeServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return FeeServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.base.account.FeeServiceGrpc.FeeServiceBlockingStub saasFeeServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.base.account.FeeServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public StatisticsServiceGrpc.StatisticsServiceBlockingStub statisticsServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return StatisticsServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public ConvertServiceGrpc.ConvertServiceBlockingStub convertServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return ConvertServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public DepositServiceGrpc.DepositServiceBlockingStub depositServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return DepositServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.base.account.DepositServiceGrpc.DepositServiceBlockingStub bhDepositServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.base.account.DepositServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminContractApplicationServiceGrpc.AdminContractApplicationServiceBlockingStub adminContractApplicationServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminContractApplicationServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.base.clear.AccountServiceGrpc.AccountServiceBlockingStub clearAccountServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.base.clear.AccountServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AgentServiceGrpc.AgentServiceBlockingStub agentServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AgentServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminAirdropServiceGrpc.AdminAirdropServiceBlockingStub adminAirdropServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminAirdropServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public BatchTransferServiceGrpc.BatchTransferServiceBlockingStub batchTransferServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BatchTransferServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public BaseConfigServiceGrpc.BaseConfigServiceBlockingStub baseConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BaseConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerServiceGrpc.AdminBrokerServiceBlockingStub adminBrokerServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerConfigServiceGrpc.AdminBrokerConfigServiceBlockingStub adminBrokerConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerExchangeServiceGrpc.AdminBrokerExchangeServiceBlockingStub adminBrokerExchangeServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerExchangeServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerTaskConfigServiceGrpc.AdminBrokerTaskConfigServiceBlockingStub adminBrokerTaskConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerTaskConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public BrokerUserServiceGrpc.BrokerUserServiceBlockingStub brokerUserServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BrokerUserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return UserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public InviteServiceGrpc.InviteServiceBlockingStub inviteServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return InviteServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public UserBlackWhiteListConfigServiceGrpc.UserBlackWhiteListConfigServiceBlockingStub userBlackWhiteListConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return UserBlackWhiteListConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminUserVerifyServiceGrpc.AdminUserVerifyServiceBlockingStub adminUserVerifyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminUserVerifyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public UserActionLogServiceGrpc.UserActionLogServiceBlockingStub userActionLogServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return UserActionLogServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminContractCompetitionServiceGrpc.AdminContractCompetitionServiceBlockingStub adminContractCompetitionServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminContractCompetitionServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminCustomLabelServiceGrpc.AdminCustomLabelServiceBlockingStub adminCustomLabelServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminCustomLabelServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminOrgContractServiceGrpc.AdminOrgContractServiceBlockingStub adminOrgContractServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminOrgContractServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public ExperienceFundServiceGrpc.ExperienceFundServiceBlockingStub experienceFundServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return ExperienceFundServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public FuturesOrderServiceGrpc.FuturesOrderServiceBlockingStub futuresOrderServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return FuturesOrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public MarginServiceGrpc.MarginServiceBlockingStub marginServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return MarginServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public MarginPositionServiceGrpc.MarginPositionServiceBlockingStub marginPositionServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return MarginPositionServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public NewsServiceGrpc.NewsServiceBlockingStub newsServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return NewsServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public NoticeTemplateServiceGrpc.NoticeTemplateServiceBlockingStub noticeTemplateServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return NoticeTemplateServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OdsServiceGrpc.OdsServiceBlockingStub odsServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return OdsServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerOptionServiceGrpc.AdminBrokerOptionServiceBlockingStub adminBrokerOptionServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerOptionServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OrgInstanceServiceGrpc.OrgInstanceServiceBlockingStub orgInstanceServiceBlockingStub(String channelName) {
        log.error("must proxy is true!");
        throw new RuntimeException("no allow request!");
    }

    public BrokerFunctionConfigServiceGrpc.BrokerFunctionConfigServiceBlockingStub brokerFunctionConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return BrokerFunctionConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OTCPaymentTermServiceGrpc.OTCPaymentTermServiceBlockingStub exOtcPaymentTermServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.ex.otc.OTCPaymentTermServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public io.bhex.ex.otc.OTCOrderServiceGrpc.OTCOrderServiceBlockingStub exOtcOrderServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return io.bhex.ex.otc.OTCOrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AppPushServiceGrpc.AppPushServiceBlockingStub appPushServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AppPushServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public RedPacketAdminServiceGrpc.RedPacketAdminServiceBlockingStub redPacketAdminServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return RedPacketAdminServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public ShareConfigServiceGrpc.ShareConfigServiceBlockingStub shareConfigServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return ShareConfigServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminStakingProductServiceGrpc.AdminStakingProductServiceBlockingStub adminStakingProductServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminStakingProductServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminStatisticServiceGrpc.AdminStatisticServiceBlockingStub adminStatisticServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminStatisticServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminSymbolServiceGrpc.AdminSymbolServiceBlockingStub adminSymbolServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminSymbolServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public SymbolServiceGrpc.SymbolServiceBlockingStub symbolServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return SymbolServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminTokenServiceGrpc.AdminTokenServiceBlockingStub adminTokenServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminTokenServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminTokenApplyServiceGrpc.AdminTokenApplyServiceBlockingStub adminTokenApplyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminTokenApplyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public UserLevelServiceGrpc.UserLevelServiceBlockingStub userLevelServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return UserLevelServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminWithdrawOrderServiceGrpc.AdminWithdrawOrderServiceBlockingStub adminWithdrawOrderServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminWithdrawOrderServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return WithdrawServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public WithdrawAdminServiceGrpc.WithdrawAdminServiceBlockingStub withdrawAdminServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return WithdrawAdminServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public CommonIniServiceGrpc.CommonIniServiceBlockingStub commonIniServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return CommonIniServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public SubBusinessSubjectServiceGrpc.SubBusinessSubjectServiceBlockingStub subBusinessSubjectServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return SubBusinessSubjectServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OrgApiKeyServiceGrpc.OrgApiKeyServiceBlockingStub orgApiKeyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return OrgApiKeyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminAuditFlowServiceGrpc.AdminAuditFlowServiceBlockingStub adminAuditFlowServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminAuditFlowServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public AdminBrokerNotifyServiceGrpc.AdminBrokerNotifyServiceBlockingStub adminBrokerNotifyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return AdminBrokerNotifyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }

    public OtcThirdPartyServiceGrpc.OtcThirdPartyServiceBlockingStub otcThirdPartyServiceBlockingStub(String channelName) {
        Channel channel = pool.borrowChannel(channelName);
        return OtcThirdPartyServiceGrpc.newBlockingStub(channel).withDeadlineAfter(stubDeadline, TimeUnit.MILLISECONDS);
    }
}



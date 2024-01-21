package io.bhex.broker.admin.config;

import io.bhex.base.common.MailServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.config
 * @Author: ming.xu
 * @CreateDate: 19/08/2018 3:28 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Configuration
public class MailConfig {

    public final static Long REGISETER_MAIL_TEMPLATE_ID = 200000L;


}

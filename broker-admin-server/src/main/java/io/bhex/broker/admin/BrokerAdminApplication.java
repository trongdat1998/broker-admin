package io.bhex.broker.admin;

import io.bhex.base.idgen.api.ISequenceGenerator;
import io.bhex.base.idgen.enums.DataCenter;
import io.bhex.base.idgen.snowflake.SnowflakeGenerator;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.config.AwsStorageConfig;
import io.bhex.broker.admin.config.BrokerConfig;
import io.bhex.broker.admin.config.GeeTestConfig;
import io.bhex.broker.common.api.client.geetest.DkGeeTestApi;
import io.bhex.broker.common.api.client.geetest.v3.DKGeeTestV3Api;
import io.bhex.broker.common.api.client.recaptcha.GoogleRecaptchaApi;
import io.bhex.broker.common.api.client.recaptcha.GoogleRecaptchaProperties;
import io.bhex.broker.common.entity.GrpcClientProperties;
import io.bhex.broker.common.objectstorage.AwsObjectStorage;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin
 * @Author: ming.xu
 * @CreateDate: 08/08/2018 8:41 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "io.bhex", excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "io.bhex.bhop.common.config.GrpcConfig"))
public class BrokerAdminApplication {

    @Value("${re-captcha-secret-key:XXX}")
    private String reCaptchaSecretKey;

    @Value("${re-captcha-supplier:none}")
    private String reCaptchaSupplier;

    public static void main(String[] args) {
        SpringApplication.run(BrokerAdminApplication.class);
    }



    @Bean
    public AwsStorageConfig awsStorageConfig() {
        return new AwsStorageConfig();
    }

//    @Bean
//    public Executor taskScheduler() {
//        return Executors.newScheduledThreadPool(5);
//    }

    @Bean
    public TaskSchedulerBuilder taskSchedulerBuilder() {
        TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
        builder = builder.poolSize(5);
        builder = builder.threadNamePrefix("admin-thread-");
        return builder;
    }



    @Bean("objectStorage")
    @Qualifier("awsStorageConfig")
    public ObjectStorage awsObjectStorage(AwsStorageConfig awsStorageConfig) {
        return AwsObjectStorage.buildFromProperties(awsStorageConfig.getAws());
    }

    @Bean
    public AwsPublicStorageConfig awsPublicStorageConfig() {
        return new AwsPublicStorageConfig();
    }

    @Bean("objecPublictStorage")
    @Qualifier("awsPublicStorageConfig")
    public ObjectStorage awsPublicObjectStorage(AwsPublicStorageConfig awsPublicStorageConfig) {
        return AwsObjectStorage.buildFromProperties(awsPublicStorageConfig.getAws());
    }

    @Bean("dkGeeTestApi")
    @Qualifier("geeTestConfig")
    public DkGeeTestApi dkGeeTestApi(GeeTestConfig geeTestConfig) {
        return new DkGeeTestApi(geeTestConfig.getGeeTest());
    }

    @Bean("dkGeeTestV3Api")
    @Qualifier("geeTestConfig")
    public DKGeeTestV3Api dkGeeTestV3Api(GeeTestConfig geeTestConfig) {
        return new DKGeeTestV3Api(reCaptchaSupplier, geeTestConfig.getGeeTest());
    }

    @Bean("googleRecaptchaApi")
    public GoogleRecaptchaApi googleRecaptchaApi() {
        GoogleRecaptchaProperties recaptchaProperties = new GoogleRecaptchaProperties();
        recaptchaProperties.setRecaptchaUrl("https://recaptcha.net/recaptcha/api/siteverify");
        recaptchaProperties.setSecurityKey(reCaptchaSecretKey);
        return new GoogleRecaptchaApi(recaptchaProperties);
    }

    @Bean
    @ConfigurationProperties(prefix = "grpc-client")
    public GrpcClientProperties grpcClientProperties() {
        return new GrpcClientProperties();
    }

    @Bean
    public BrokerConfig getBroker() {
        return new BrokerConfig();
    }

    @Bean
    public ISequenceGenerator sequenceGenerator(StringRedisTemplate redisTemplate) {
        long workId;
        try {
            workId = redisTemplate.opsForValue().increment("idGenerator-wordId") % 512;
        } catch (Exception e) {
            workId = RandomUtils.nextLong(0, 512);
            log.error("getIdGeneratorWorkId from redis occurred exception. set a random workId:{}", workId);
        }
        log.info("use workId:{} for IdGenerator", workId);
        return SnowflakeGenerator.newInstance(DataCenter.DC1.value(), workId);
    }
}

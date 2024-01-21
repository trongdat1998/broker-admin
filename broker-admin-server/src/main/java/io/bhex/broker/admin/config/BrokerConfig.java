package io.bhex.broker.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangsc
 * @description broker配置
 * @date 2020-06-04 13:50
 */
@Data
@ConfigurationProperties(prefix = "broker")
public class BrokerConfig {
    boolean proxy;
    String apiKey;
    String secretKey;
}
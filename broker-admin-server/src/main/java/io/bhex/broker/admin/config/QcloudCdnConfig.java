package io.bhex.broker.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qcloud.cdn")
public class QcloudCdnConfig {

    private String secretId;

    private String secretKey;


}

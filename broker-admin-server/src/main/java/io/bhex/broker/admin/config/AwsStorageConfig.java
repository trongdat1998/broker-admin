package io.bhex.broker.admin.config;

import io.bhex.broker.common.objectstorage.AwsObjectStorageProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "awsstorage")
public class AwsStorageConfig {

    private String staticUrl;

    private AwsObjectStorageProperties aws = new AwsObjectStorageProperties();

    private String accessOsFileKey = "";


}

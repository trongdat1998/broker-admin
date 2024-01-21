package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.service.ImageUtilService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 26/09/2018 11:31 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class ImageUtilServiceImpl implements ImageUtilService {

    @Autowired
    private AwsPublicStorageConfig awsPublicStorageConfig;

    @Override
    public String getImageUrl(String path) {
        if (StringUtils.isNotEmpty(path)) {
            boolean b = path.startsWith(awsPublicStorageConfig.getStaticUrl());
            if (!b) {
                path = awsPublicStorageConfig.getStaticUrl() + path;
            }
        }
        return path;
    }

    @Override
    public String getImagePath(String url) {
        if (StringUtils.isNotEmpty(url)) {
            boolean b = url.startsWith(awsPublicStorageConfig.getStaticUrl());
            if (b) {
                url = url.replace(awsPublicStorageConfig.getStaticUrl(), "");
            }
        }
        return url;
    }
}

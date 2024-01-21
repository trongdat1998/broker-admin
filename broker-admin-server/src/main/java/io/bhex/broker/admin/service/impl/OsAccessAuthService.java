package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import io.bhex.broker.admin.config.AwsStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Slf4j
@Service
public class OsAccessAuthService {

    private static final Integer URL_ACCESSIBLE_SECONDS = 30 * 60; // 30分钟有效

    private static final String FILE_KEY_ACCESS_PREFIX = "/api/os/";

    @Autowired
    private AwsStorageConfig awsObjectStorageConfig;

    public String createAccessUrl(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return "";
        }
        String accessKey = awsObjectStorageConfig.getAccessOsFileKey();
        Integer expireTime = (Ints.checkedCast(System.currentTimeMillis() / 1000L) + URL_ACCESSIBLE_SECONDS);
        String url = FILE_KEY_ACCESS_PREFIX + fileKey + "?e=" + expireTime;
        String token = BaseEncoding.base64Url().encode(Hashing.sha256().hashString(accessKey + expireTime, Charset.forName("UTF-8")).asBytes());
        return url + "&token=" + token;
    }

    public String checkAccessUrl(String key, long expireTime, String token) {
        //log.info("key:{} expire:{} token:{}", key, expireTime, token);
        if (Strings.isNullOrEmpty(key) || expireTime == 0 || Strings.isNullOrEmpty(token)) {
            return null;
        }
        String accessKey = awsObjectStorageConfig.getAccessOsFileKey();
       // log.info("Ints.checkedCast:{}", Ints.checkedCast(System.currentTimeMillis() / 1000L));
        // check expireTime and urlPrefix
        if (expireTime < Ints.checkedCast(System.currentTimeMillis() / 1000L)) {
            return null;
        }
        String expectToken = BaseEncoding.base64Url().encode(Hashing.sha256().hashString(accessKey + expireTime, Charset.forName("UTF-8")).asBytes());
        //log.info("expectToken:{}", expectToken);
        if (!expectToken.equals(token)) {
            return null;
        }
        return key;
    }

}

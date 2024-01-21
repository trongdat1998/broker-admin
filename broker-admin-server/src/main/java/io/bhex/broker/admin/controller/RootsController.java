package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.broker.admin.service.impl.OsAccessAuthService;
import io.bhex.broker.common.objectstorage.ObjectMetadata;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.objectstorage.ObjectStorageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
public class RootsController {


    @Resource(name = "objectStorage")
    private ObjectStorage awsObjectStorage;
    @Resource
    private OsAccessAuthService osAccessAuthService;

    @AccessAnnotation(verifyLogin = false, verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping("/api/os/**")
    public void getOssFile(@RequestParam(name = "e") Long expireTime, @RequestParam String token,
                              @RequestParam(name = "scale", required = false, defaultValue = "0") Double scale,
                              @RequestParam(name = "width", required = false, defaultValue = "0") Integer width,
                              @RequestParam(name = "height", required = false, defaultValue = "0") Integer height,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getRequestURI().substring("/api/os/".length());
        //log.info("key:{} ", key);
        String ossKey = osAccessAuthService.checkAccessUrl(key, expireTime, token);
       // log.info("key:{} ossKey:{}", key, ossKey);
        if (Strings.isNullOrEmpty(ossKey)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        ObjectMetadata metadata = null;
        try {
            metadata = this.awsObjectStorage.getObjectMetadata(ossKey);
        } catch (ObjectStorageNotFoundException e) {
            log.warn("pic not found : {}", key);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        File ossFile = null;
        try {
            ossFile = File.createTempFile("broker-os-", ".tmp");
            this.awsObjectStorage.downloadObject(ossKey, ossFile);

           // log.info("key:{} ossFile:{}", key, ossFile != null ? ossFile.length() : 0);
            if (ossFile == null || ossFile.length() == 0) {
                log.error("pic not found length==0 : {}", key);
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }

            if (metadata.contentType() != null) {
                if (metadata.contentType().type().equalsIgnoreCase("image")) {
                    response.setContentType("image/png");
                } else {
                    response.setContentType(metadata.contentType().toString());
                }
            }

            // 判断是否缩略图
            if (scale < 1 && scale > 0) {
                Thumbnails.of(ossFile)
                        .scale(scale)
                        .toOutputStream(response.getOutputStream());
            } else if (width != 0d && height != 0d) {
                Thumbnails.of(ossFile)
                        .size(width, height)
                        .toOutputStream(response.getOutputStream());
            } else {
                Files.copy(ossFile, response.getOutputStream());
            }
        } catch (Exception e) {
            log.error("error in aws file copy", e);
        } finally {
            if (ossFile != null && !ossFile.delete()) {
                ossFile.deleteOnExit();
            }
        }
    }

}

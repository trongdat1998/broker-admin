package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.constants.StorageConstants;
import io.bhex.broker.admin.service.ImageUtilService;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.common.objectstorage.CannedAccessControlList;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.objectstorage.ObjectStorageUtil;
import io.bhex.broker.common.util.FileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 25/09/2018 11:06 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private static final String TEXT_FILE_PREFIX = "bhop/file/";

    private static final String IMAGE_FILE_PREFIX = "bhop/image/";

    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;

    @Resource(name = "awsPublicStorageConfig")
    private AwsPublicStorageConfig awsPublicStorageConfig;

    @Resource
    private ImageUtilService imageUtilService;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/image/banner", method = RequestMethod.POST)
    public ResultModel uploadImage(@RequestParam(name = "uploadFile") MultipartFile uploadImageFile, @RequestParam(value = "echoStr", required = false, defaultValue = "") String echoStr) throws Exception {
        String fileType = FileUtil.getFileSuffix(uploadImageFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType) || !StorageConstants.IMG_FILE_TYPES.contains(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        String suffix = fileType.toLowerCase();
        String fileKey = awsPublicStorageConfig.getPrefix() + ObjectStorageUtil.sha256FileName(uploadImageFile.getBytes(), suffix);
        awsPublicObjectStorage.uploadObjectWithCacheControl(fileKey, ObjectStorageUtil.getFileContentType(suffix, MediaType.ANY_IMAGE_TYPE),
                uploadImageFile.getInputStream(), CannedAccessControlList.PublicRead, "max-age=31536000");
        String url = imageUtilService.getImageUrl(fileKey);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("echoStr", echoStr);
        return ResultModel.ok(result);
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/file/image", method = RequestMethod.POST)
    public ResultModel uploadCommonImage(@RequestParam(name = "uploadFile") MultipartFile uploadImageFile,
                                         @RequestParam(value = "echoStr", required = false, defaultValue = "") String echoStr) throws Exception {
        String fileType = FileUtil.getFileSuffix(uploadImageFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType) || !StorageConstants.IMG_FILE_TYPES.contains(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        String suffix = fileType.toLowerCase();
        String fileKey = IMAGE_FILE_PREFIX + ObjectStorageUtil.sha256FileName(uploadImageFile.getBytes(), suffix);

        awsPublicObjectStorage.uploadObjectWithCacheControl(fileKey, ObjectStorageUtil.getFileContentType(suffix, MediaType.ANY_IMAGE_TYPE),
                uploadImageFile.getInputStream(), CannedAccessControlList.PublicRead, "max-age=31536000");

        String url = awsPublicStorageConfig.getStaticUrl() + fileKey;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("echoStr", echoStr);
        return ResultModel.ok(result);
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/file/text", method = RequestMethod.POST)
    public ResultModel uploadText(@RequestParam(name = "uploadFile") MultipartFile uploadImageFile, @RequestParam(value = "echoStr", required = false, defaultValue = "") String echoStr) throws Exception {
        String fileType = FileUtil.getFileSuffix(uploadImageFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType) || !StorageConstants.TEXT_FILE_TYPES.contains(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        String suffix = fileType.toLowerCase();
        String fileKey = TEXT_FILE_PREFIX + ObjectStorageUtil.sha256FileName(uploadImageFile.getBytes(), suffix);

        awsPublicObjectStorage.uploadObjectWithCacheControl(fileKey, ObjectStorageUtil.getFileContentType(suffix, MediaType.ANY_TEXT_TYPE),
                uploadImageFile.getInputStream(), CannedAccessControlList.PublicRead, "max-age=31536000");

        String url = awsPublicStorageConfig.getStaticUrl() + fileKey;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("echoStr", echoStr);
        return ResultModel.ok(result);
    }
}

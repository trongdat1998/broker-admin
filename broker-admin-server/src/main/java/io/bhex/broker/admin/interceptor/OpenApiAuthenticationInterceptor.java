package io.bhex.broker.admin.interceptor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.bhex.base.admin.common.AdminUserServiceGrpc;
import io.bhex.base.admin.common.OpenApiAuthenticationReply;
import io.bhex.base.admin.common.OpenApiAuthenticationRequest;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OpenApiAuthenticationInterceptor implements HandlerInterceptor, InitializingBean {

    public static final String USER_ID_ATTR_KEY = "openapi_user_id";
    private static final String HEADER_ACCESS_KEY = "X-ACCESS-KEY";
    private static final String REQUEST_ACCESS_KEY = "accessKey";
    private static final String SIGNATURE_PARAM_NAME = "signature";
    private static final String ORG_ID = "orgId";

    @Resource
    GrpcClientConfig grpcConfig;


    private AdminUserServiceGrpc.AdminUserServiceBlockingStub getAdminStub() {
        return grpcConfig.adminUserServiceBlockingStub(GrpcConfig.ADMIN_COMMON_GRPC_CHANNEL_NAME);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessKey = request.getHeader(HEADER_ACCESS_KEY);
        if (Strings.isNullOrEmpty(accessKey)) {
            accessKey = request.getParameter(REQUEST_ACCESS_KEY);
        }
        if (Strings.isNullOrEmpty(accessKey)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "access_key is null");
            return false;
        }
        String signature = request.getParameter(SIGNATURE_PARAM_NAME);
        if (Strings.isNullOrEmpty(signature)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "signature is null");
            return false;
        }
        TreeMap<String, String> requestParamMap = new TreeMap<>();
        Enumeration<String> paramNameEnumeration = request.getParameterNames();
        while (paramNameEnumeration.hasMoreElements()) {
            String paramName = paramNameEnumeration.nextElement();
            if (!paramName.equals(REQUEST_ACCESS_KEY)) {
                requestParamMap.put(paramName, request.getParameter(paramName));
            }
        }

        StringBuffer paramBuffer = new StringBuffer();
        // 拼接queryString的串
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            paramBuffer.append(buildQueryString(queryString, SIGNATURE_PARAM_NAME));
        }

        // 拼接requestBody的串
        String requestBodyString = buildRequestBodyString(request);
        if (!StringUtils.isEmpty(requestBodyString)) {
            paramBuffer.append(requestBodyString);
        }
        log.info("openapi requestBody :" + paramBuffer.toString());
        OpenApiAuthenticationRequest grpcRequest = OpenApiAuthenticationRequest.newBuilder()
                .setAccessKey(accessKey)
                .setOriginalStr(paramBuffer.toString())
                .setSignature(signature)
                .build();
        OpenApiAuthenticationReply reply = getAdminStub().openApiAuthenticate(grpcRequest);
        if (reply.getResult() != 0) {
            Integer httpStatus = 403;
            String errorMessage = "";
            switch (reply.getResult()) {
                case -1:
                    errorMessage = "bad request"; // record not found
                    break;
                case -2:
                    errorMessage = "check user status or apiKey status";
                    break;
                case -3:
                    errorMessage = "authenticate failed, please check!";
                    break;
                default:
                    errorMessage = "invoke error";
                    break;
            }
            log.warn("accessKey:{}, originalStr:{}, signature:{}, check failed", accessKey, paramBuffer.toString(), signature);
            response.sendError(HttpStatus.FORBIDDEN.value(), errorMessage);
            return false;
        }

        log.info("openapi orgId :" + reply.getOrgId());
        log.info("openapi userId :" + reply.getUserId());
        request.setAttribute(USER_ID_ATTR_KEY, reply.getUserId());
        request.setAttribute(ORG_ID, reply.getOrgId());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    public String buildQueryString(String queryString, String paramName) {
        String[] paramArray = queryString.split("&");
        List<String> paramList = Lists.newArrayList();
        for (String param : paramArray) {
            if (param.indexOf(paramName) > -1) {
                continue;
            }
            paramList.add(param);
        }

        return paramList.stream().collect(Collectors.joining("&"));
    }

    public String buildRequestBodyString(HttpServletRequest request) {

        String queryString = request.getQueryString();
        Map<String, String> queryMap = Maps.newHashMap();

        if (!StringUtils.isEmpty(queryString)) {
            for (String queryValue : queryString.split("&")) {
                if (StringUtils.isEmpty(queryValue)) {
                    continue;
                }
                String[] keyValue = queryValue.split("=");
                queryMap.put(keyValue[0], "1");
            }
        }
        List<String> paramNameList = Collections.list(request.getParameterNames());
        if (CollectionUtils.isEmpty(paramNameList)) {
            return null;
        }

        List<String> paramList = Lists.newArrayList();
        for (String paramName : paramNameList) {
            if (queryMap.get(paramName) != null) {
                continue;
            }
            if (SIGNATURE_PARAM_NAME.equals(paramName)) {
                continue;
            }
            paramList.add(paramName + "=" + request.getParameter(paramName));
        }

        if (CollectionUtils.isEmpty(paramList)) {
            return null;
        }

        return paramList.stream().collect(Collectors.joining("&"));
    }

}

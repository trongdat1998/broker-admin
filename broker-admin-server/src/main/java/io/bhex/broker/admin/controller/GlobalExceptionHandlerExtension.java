package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.controller.GlobalExceptionHandler;
import io.bhex.bhop.common.util.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 2019/9/19 6:17 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerExtension extends GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultModel exception(NullPointerException e, HttpServletRequest request) {
        log.error("null pointer exception domain:{} requesturi:{} ", request.getServerName(),
                request.getRequestURI(), e);
        return ResultModel.validateFail(getLocalMsg("request.parameter.error") + " : " + e.getMessage());
    }
}

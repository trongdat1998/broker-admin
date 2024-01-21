package io.bhex.broker.admin.interceptor;

import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.param.BrokerInstanceRes;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.service.BaseCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ForbidAccessInterceptor  implements HandlerInterceptor, InitializingBean {

    @Autowired
    private OrgInstanceConfig orgInstanceConfig;

    @Resource
    private BaseCommonService baseCommonService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("START_TIME", System.currentTimeMillis());
        Long orgId = baseCommonService.getOrgId(request);
        if (orgId == null || orgId == 0) {
            return true;
        }
        BrokerInstanceRes instanceRes = orgInstanceConfig.getBrokerInstance(orgId);
        if (instanceRes == null) {
            return true;
        }
        if (instanceRes.getForbidAccess() == 1) {
            response.getWriter().write("{\"code\":" + ErrorCode.NO_PERMISSION.getCode()
                    + ",\"msg\":\"Not Allowed!\"" + "}");
            return false;
        }
        if (instanceRes.getDueTime() > 0 && instanceRes.getDueTime() < System.currentTimeMillis()) {
            response.getWriter().write("{\"code\":" + ErrorCode.EXPIRED.getCode()
                    + ",\"msg\":\"Exceed the Time Limit!\"" + "}");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}

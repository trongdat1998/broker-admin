package io.bhex.broker.admin;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.jwt.filter.AccessAuthorizeInterceptor;
import io.bhex.broker.admin.filter.XSSRequestFilter;
import io.bhex.broker.admin.interceptor.ForbidAccessInterceptor;
import io.bhex.broker.admin.interceptor.OpenApiAuthenticationInterceptor;
import io.bhex.broker.core.interceptor.PrometheusInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Description: AdminWebMvcConfig
 * @Date: 2020/1/14 下午3:13
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@EnableWebMvc
@Configuration
public class AdminWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MessageSource messageSource;

    @Bean
    public OpenApiAuthenticationInterceptor openApiAuthenticationInterceptor() {
        return new OpenApiAuthenticationInterceptor();
    }

    @Bean
    public ForbidAccessInterceptor forbidAccessInterceptor() {
        return new ForbidAccessInterceptor();
    }

    @Bean
    public AccessAuthorizeInterceptor accessAuthorizeInterceptor() {
        return new AccessAuthorizeInterceptor();
    }

    @Bean
    public FilterRegistrationBean<XSSRequestFilter> xssRequestFilterFilterRegistrationBean() {
        FilterRegistrationBean<XSSRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setName("xssRequestFilter");
        registration.setFilter(new XSSRequestFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PrometheusInterceptor()).addPathPatterns("/api/v1/**");
        registry.addInterceptor(openApiAuthenticationInterceptor()).addPathPatterns("/org_api/**");
//        registry.addInterceptor(forbidAccessInterceptor()).addPathPatterns("/api/v1/**")
//                .excludePathPatterns("/api/v1/broker/query/broker/info")
//                .excludePathPatterns("/api/v1/user/login_user_info");
        registry.addInterceptor(accessAuthorizeInterceptor()).addPathPatterns("/api/v1/**");
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter methodParameter) {
                return methodParameter.getParameterType().equals(AdminUserReply.class);
            }

            @Override
            public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
                return webRequest.getAttribute("adminUser", 0);
            }
        });
    }
}



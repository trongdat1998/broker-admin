package io.bhex.broker.admin.grpc.client.interceptor;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import io.bhex.broker.admin.config.BrokerConfig;
import io.grpc.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * @author wangsc
 * @description 路由认证拦截器
 * @date 2020-06-04 14:34
 */
@Slf4j
public class RouteAuthInterceptor implements ClientInterceptor, CallCredentials {

    private final String routeKey;
    private final BrokerConfig brokerConfig;
    private static final String BEARER = "bhop_bearer_token:";
    private static final Metadata.Key<String> API_KEY_META = Metadata.Key.of("ApiKey", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * 认证刷新时间（gateway失效时间差1分钟）
     */
    private static final long AUTH_REFRESH_TIME = 4 * 60 * 1000L;
    private RouteAuthInterceptor.BrokerAuth brokerAuthCache;

    public RouteAuthInterceptor(String routeKey, BrokerConfig brokerConfig) {
        this.routeKey = routeKey;
        this.brokerConfig = brokerConfig;
    }

    @Override
    public void applyRequestMetadata(MethodDescriptor<?, ?> method, Attributes attrs, Executor appExecutor, MetadataApplier applier) {
        appExecutor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                Metadata.Key<String> rKey = Metadata.Key.of("route-channel", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(rKey, routeKey);
                Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                //检查发送认证
                if (brokerAuthCache == null || brokerAuthCache.refreshTime <= System.currentTimeMillis()) {
                    headers.put(authKey, makeKey(brokerConfig.getSecretKey()));
                } else {
                    headers.put(authKey, brokerAuthCache.authData);
                }
                headers.put(API_KEY_META, brokerConfig.getApiKey());
                applier.apply(headers);
            } catch (Throwable e) {
                applier.fail(Status.UNAUTHENTICATED.withCause(e));
            }
        });
    }

    @Override
    public void thisUsesUnstableApi() {

    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        //在拦截的过程中注入认证
        callOptions = callOptions.withCallCredentials(this);
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                super.start(responseListener, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                this.delegate().sendMessage(message);
            }
        };
    }

    String makeKey(String secretKey) {
        long currentTime = System.currentTimeMillis();
        String signature = Hashing.hmacSha256(secretKey.getBytes()).hashString(BEARER + "#" + secretKey + "#" + currentTime, Charsets.UTF_8).toString();
        String authData = BEARER + signature + "#" + currentTime;
        brokerAuthCache = RouteAuthInterceptor.BrokerAuth.builder().refreshTime(currentTime + AUTH_REFRESH_TIME).authData(authData).build();
        return authData;
    }

    /**
     * broker认证缓存
     */
    @Data
    @Builder
    private static class BrokerAuth {
        /**
         * 刷新时间（加签时间+4分钟）
         */
        private long refreshTime;
        /**
         * 认证串
         */
        private String authData;
    }
}

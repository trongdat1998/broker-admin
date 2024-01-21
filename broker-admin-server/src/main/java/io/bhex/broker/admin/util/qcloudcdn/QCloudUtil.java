package io.bhex.broker.admin.util.qcloudcdn;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.TreeMap;

@Slf4j
public class QCloudUtil {

    public static boolean refreshUrl(String secretId, String secretKey, String url) {
//params
        String serverHost = "cdn.api.qcloud.com";
        String serverUri = "/v2/index.php";
        String requestMethod = "GET";
        String action = "RefreshCdnUrl";

        /*add interface params,e.g.DescribeHosts*/
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("urls.0", url);
        if (params == null) {
            params = new TreeMap<>();
        }
        params.put("Action", action);
        log.info("refresh url : {}", url);
        try {
            String response = Request.send(params, secretId, secretKey, requestMethod, serverHost, serverUri, null);

            JSONObject result = new JSONObject(response);
            log.info("refresh url:{} result:{}", url, result);
        } catch (Exception e) {
            log.error("error..." + e.getMessage());
        }
        return true;
    }

    public static void main(String[] args) {
        refreshUrl("xxx", "xxx", "https://static.nucleex.com/token/jqluyj0UKJTAKoLz4vlWsfXKELr8jT7mpjyutgajvFs.png");
    }

}

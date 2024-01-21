package io.bhex.broker.admin.util.qcloudcdn;

import org.json.JSONObject;

import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {

        //params
        String serverHost = "cdn.api.qcloud.com";
        String serverUri = "/v2/index.php";
        String secretId = "XXX";
        String secretKey = "XXX";
        String requestMethod = "GET";
        String defaultRegion = "gz";
        String action = "RefreshCdnUrl";

        /*add interface params,e.g.DescribeHosts*/
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("urls.0", "https://static.nucleex.com/token/jqluyj0UKJTAKoLz4vlWsfXKELr8jT7mpjyutgajvFs.png");
        if (params == null) {
            params = new TreeMap<String, Object>();
        }
        action = ucFirst(action);
        params.put("Action", action);
        System.out.println(params);
        try {
            String response = Request.send(params, secretId, secretKey, requestMethod, serverHost, serverUri, null);
            /*use generateUrl to get url*/
            String url = Request.generateUrl(params, secretId, secretKey, requestMethod, serverHost, serverUri);
            System.out.println("correct request url:[" + url + "]");
            JSONObject result = new JSONObject(response);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("error..." + e.getMessage());
        }

    }
    private static String ucFirst(String word) {
        return word.replaceFirst(word.substring(0, 1), word.substring(0, 1).toUpperCase());
    }
}

package io.bhex.broker.admin.util;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

    /**
     * 是否是自然数
     * @param str
     * @return
     */
    public static boolean isDigits(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return  NumberUtils.isDigits(str);
    }

    public static boolean isLong(String str) {
        if (!isDigits(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * 是否是数字 如 123 123.12
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return  NumberUtils.isCreatable(str);
    }

    /**
     * 正则匹配userid，如 \u2048123 ab123 123ret456 都会得到 123
     * @param userIdStr
     * @return
     */
    public static Long getUserId(String userIdStr) {
        Pattern pattern = Pattern.compile("\\d{1,}");
        Matcher matcher = pattern.matcher(userIdStr);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(0));
        }
        return null;
    }



    public static void main(String[] args) {
        System.out.println(isDigits("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        System.out.println(isDigits("12312334"));

        System.out.println(isNumber("123123123123123123"));

        System.out.println(getUserId("8^#asb12&ew1Agfvn;’p"));
    }

}

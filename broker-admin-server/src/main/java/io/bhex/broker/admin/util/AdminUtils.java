package io.bhex.broker.admin.util;

import com.google.common.base.Strings;

/**
 * Admin Utils
 */
public class AdminUtils {

    /**
     * email encrypt
     *
     * @param email
     * @return
     */
    public static String emailEncrypt(String email){
        email = Strings.nullToEmpty(email);
        return email.replaceAll("(?<=\\w{2}).*?(?=\\w{2}@)", "*");
    }
}

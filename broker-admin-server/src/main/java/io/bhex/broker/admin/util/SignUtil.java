package io.bhex.broker.admin.util;

import com.google.common.hash.Hashing;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;
import com.google.crypto.tink.subtle.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Logger;


/**********************************
 * @项目名称: bhex-common-lib
 * @文件名称: SignUtil
 * @Date 2018/10/24
 * @Author liweiwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/

/**
 * 签名辅助类
 * 私钥解密(AES pkcs7==pkcs5) +签名(ed25519 key=privateKey+publicKey)
 * AES(key=sha256(key), iv=random(16)) -> iv + encrypt = encryptKey
 */
@Component
public class SignUtil {

    /**
     *
     */
    private static final String ENV_KEY = "ENCRYPT_PRIVATEKEY_PASSWORD";
    /**
     * Encrypt password for private key
     */
    @Getter
    private static final String ENCRYPT_PASSWORD = System.getenv(ENV_KEY);

    private static Logger logger = Logger.getLogger(io.bhex.base.crypto.SignUtil.class.getSimpleName());

    /**
     * 签名中加上密钥对版本信息,用来回溯历史数据
     * @param version
     * @param key
     * @param data
     * @return
     */

    public static String signWithVersion(int version, byte[] key, byte[] data) {
        return String.format("%d_%s", version, Base64.getEncoder().encodeToString(sign(key, data)));
    }


    /**
     * @param key is privateKey + publicKey encrypt by aes
     * @param data
     * @return
     */
    public static byte[] sign(byte[] key, byte[] data) {
        byte[] privateKey = Arrays.copyOfRange(key, 0, 32);
        byte[] signBytes = new byte[0];
        try {
            signBytes = (new Ed25519Sign(privateKey)).sign(data);
        } catch (GeneralSecurityException e) {
            logger.warning("Sign fail !" + e.toString());
        }

        return signBytes;
    }

    /**
     * verify
     *
     * @param publicKey
     * @param signature
     * @param data
     * @return
     */
    public static void verify(byte[] publicKey, byte[] signature, byte[] data) throws GeneralSecurityException {
        (new Ed25519Verify(publicKey)).verify(signature, data);
    }


    /**
     *
     * @param key
     * @param priKey
     * @param pubKey
     * @return
     */
    public static byte[] encryptPrivateKey(String key, byte[] priKey, byte[] pubKey) {
        byte[] keyBytes = Hashing.sha256().hashString(key, StandardCharsets.UTF_8).asBytes();
        priKey = byteMerger(priKey, pubKey);

        byte[] randBytes = Random.randBytes(16);
        IvParameterSpec iv = new IvParameterSpec(randBytes);
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return byteMerger(randBytes, cipher.doFinal(priKey));
        } catch (Exception ex) {
            logger.warning(String.format("Decrypt key fail! encrypted string %s", new String(priKey)));
            ex.printStackTrace();
        }
        return null;
    }

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte1, byte[] byte2){
        byte[] byte3 = new byte[byte1.length+byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }


    /**
     * @param key
     * @param encrypted
     * @return
     */
    public static byte[] decrypt(String key, byte[] encrypted) {
        byte[] keyBytes = Hashing.sha256().hashString(key, StandardCharsets.UTF_8).asBytes();
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(encrypted, 0, 16));
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(Arrays.copyOfRange(encrypted, 16, encrypted.length));
        } catch (Exception ex) {
            logger.warning(String.format("Decrypt key fail! encrypted string %s", new String(encrypted)));
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 生成broker的公私钥
     * @param key 加密秘钥的密码
     * @return
     */
    public static BrokerKey getBrokerKey(String key) {
        try {
            Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] privateKey = keyPair.getPrivateKey();
            byte[] publicKey = keyPair.getPublicKey();
            byte[] encrypt = encryptPrivateKey(key, privateKey, publicKey);
            BrokerKey brokerKey = new BrokerKey(Base64.getEncoder().encodeToString(encrypt), Base64.getEncoder().encodeToString(publicKey));
            return brokerKey;
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    @Data
    public static class BrokerKey {

        private String privateKey;
        private String publicKey;

        public BrokerKey(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    /**
     * testing
     */
    public static void test() {
        try {
            // 1.密码 sha256
            // 2.获得privatekey，publickey
            // 3.privateKey += publickey
            // 4.en_str = AES(privateKey, key)
            // 5.base64
            String priKey;
            String pubKey;
            String key = "123456";
            Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] privateKey = keyPair.getPrivateKey();
            byte[] publicKey = keyPair.getPublicKey();
            byte[] encrypt = encryptPrivateKey(key, privateKey, publicKey);

            priKey = Base64.getEncoder().encodeToString(encrypt);
            pubKey = Base64.getEncoder().encodeToString(publicKey);


            byte[] test = "hello".getBytes();

            // decrypt private key
            byte[] desBytes = decrypt(key, Base64.getDecoder().decode(priKey));


            // sign
            byte[] signBytes = sign(Arrays.copyOfRange(Objects.requireNonNull(desBytes),0, 32), test);

            // verify
            try {
                verify(Base64.getDecoder().decode(pubKey), signBytes, test);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        test();
    }
}

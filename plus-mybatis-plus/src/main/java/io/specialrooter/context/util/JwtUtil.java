package io.specialrooter.context.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;

/**
 * 统一解析用户token
 *@ClassName JwtUtil
 *@Author tian_ye
 *@Date 2020/7/14 11:17
 */
@Slf4j
public class JwtUtil {

    /**
     * token 前缀
     */
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * JWT 秘钥
     */
    private static final String JWT_SECRET = "xUjhQxMuwoE+KpbqWH7DNVU1AMBHeg7VjuKo0ZJnSqI=";

    /**
     * JWT主题 key
     */
    private static final String JWT_SUBJECT_KEY = "sub";

    /**
     * JWT主题 value
     */
    private static final String JWT_SUBJECT_VAL = "cuc_jwt_subject";

    /**
     * JWT过期key
     */
    private static final String KEY_EXP = "exp";

    /**
     * token Header name
     */
    private static final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * 解析用户token
     * @param request
     * @return
     */
    public static JSONObject parseUserToken(HttpServletRequest request) {
        String userToken = request.getHeader(TOKEN_HEADER_NAME);
        return parseUserToken(userToken);
    }

    /**
     * 解析用户token
     * @param userToken
     * @return
     */
    public static JSONObject parseUserToken(String userToken) {
        JWTVerifyResult jwtVerifyResult = _verify(userToken);
        if (JWTVerifyEnum.SUCCESS.getHttpResponseCode() != jwtVerifyResult.getResult().getHttpResponseCode()) {
            log.error("解密失败，userToken:[{}],msg:[{}]", userToken, jwtVerifyResult.getMessage());
        }
        return jwtVerifyResult.getPayload();
    }

    private static JWTVerifyResult _verify(String jwt) {
        JWSObject jwsObject = parse(jwt);
        if (jwsObject == null) {
            return JWTVerifyResult.fail();
        }
        Payload payload = jwsObject.getPayload();
        if (payload == null) {
            return JWTVerifyResult.fail();
        }
        // 验证时间的有效性
        JSONObject json = payload.toJSONObject();
        String subject = json.getAsString(JWT_SUBJECT_KEY);
        // 如果是必须有exp的属性
        if (!json.containsKey(KEY_EXP)) {
            return JWTVerifyResult.fail("jwt token中必须有exp属性");
        }
        // 此处不同逻辑处理用于区分友店token的过期时间单位为毫秒，cuc为秒
        if (subject != null && subject.equals(JWT_SUBJECT_VAL)) {
            // 判断exp是否超时
            if (json.containsKey(KEY_EXP) && json.getAsNumber(KEY_EXP).longValue() * 1000 < System.currentTimeMillis()) {
                return JWTVerifyResult.exp();
            }
        } else {
            // 判断exp是否超时
            if (json.containsKey(KEY_EXP) && json.getAsNumber(KEY_EXP).longValue() < System.currentTimeMillis()) {
                return JWTVerifyResult.exp();
            }
        }
        JWSVerifier verifier;
        try {
            verifier = new MACVerifier(Base64.decodeBase64(JWT_SECRET));

            boolean verify = jwsObject.verify(verifier);
            if (verify) {
                JWTVerifyResult result;
                result = JWTVerifyResult.success();
                result.setPayload(json);
                return result;
            }
            return JWTVerifyResult.fail();
        } catch (JOSEException e) {
            log.error("解析token异常", e);
        }
        return JWTVerifyResult.fail();
    }

    private static JWSObject parse(String jwt) {
        if (StringUtils.isBlank(jwt)) {
            return null;
        }
        if (StringUtils.startsWith(jwt, JWT_TOKEN_PREFIX)) {
            jwt = jwt.substring(JWT_TOKEN_PREFIX.length());
        }
        JWSObject jwsObject;
        try {
            jwsObject = JWSObject.parse(jwt);
            return jwsObject;
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return null;
    }


    @Data
    public static class JWTVerifyResult implements Serializable {
        private static final long serialVersionUID = -6990128954601706499L;

        private JWTVerifyEnum result;
        private int code;
        private String message;
        private JSONObject payload;

        static JWTVerifyResult fail() {
            return fail("验证失败");
        }

        static JWTVerifyResult fail(String msg) {
            JWTVerifyResult result = new JWTVerifyResult();
            result.setCode(0);
            result.setResult(JWTVerifyEnum.FAIL);
            result.setMessage(msg);
            return result;
        }

        static JWTVerifyResult exp() {
            JWTVerifyResult result = new JWTVerifyResult();
            result.setCode(0);
            result.setResult(JWTVerifyEnum.EXPIRED);
            result.setMessage("JWT过时");
            return result;
        }

        static JWTVerifyResult success() {
            JWTVerifyResult result = new JWTVerifyResult();
            result.setCode(0);
            result.setResult(JWTVerifyEnum.SUCCESS);
            result.setMessage("验证成功");
            return result;
        }
    }


    public enum JWTVerifyEnum implements Serializable {
        /**
         * JWT验证成功
         */
        SUCCESS(200),
        /**
         * 建议更换tocken
         */
        JWT_SUGGEST_TOKEN(207),
        /**
         * 超时 JWT过期
         */
        FAIL(601),

        /**
         * 格式错误
         */
        FORMAT_ERROR(803),
        /**
         * 签名错误
         */
        EXPIRED(801);

        private int httpResponseCode;

        JWTVerifyEnum(int code) {
            httpResponseCode = code;
        }

        public int getHttpResponseCode() {
            return httpResponseCode;
        }
    }
}

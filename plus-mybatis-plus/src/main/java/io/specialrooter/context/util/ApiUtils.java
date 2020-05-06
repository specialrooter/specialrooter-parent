package io.specialrooter.context.util;

import com.alibaba.fastjson.JSON;
import io.specialrooter.context.SpringContext;
import io.specialrooter.context.model.TokenDTO;
import io.specialrooter.context.model.UserDTO;
import io.specialrooter.context.model.UserDataAuthDTO;
import io.specialrooter.standard.component.exception.OauthException;
import io.specialrooter.util.M;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xinya.hou
 * @since 2019-07-21
 */
public class ApiUtils {
    private static String userTokenKey = "USERTOKEN_";
    @Autowired
    private RedisTemplate redisTemplate;
    private static ApiUtils apiUtils;
    public static ValueOperations<String, Object> operations;

    @PostConstruct
    public void init() {
        apiUtils = this;
        apiUtils.redisTemplate = this.redisTemplate;
        operations = apiUtils.redisTemplate.opsForValue();
    }

    public static boolean delete(Object key){
        return apiUtils.redisTemplate.delete(key);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static UserDTO getCurrentUser() {
        //获取当前HTTP Header
        TokenDTO tokenDTO = getHeaderToken();
        if (tokenDTO == null) {
            return null;
        }
        String appGid = tokenDTO.getAppGid();
        String o = (String) operations.get(userTokenKey + tokenDTO.getUserGid());
        if (o != null) {
            UserDTO user = JSON.parseObject(o, UserDTO.class);
            Integer userType = user.getUserType();
            if ("MS8xL1NMLUNUUC1XRUItUE1D".equals(appGid) || "MS8yL1NMLUNUUC1XRUItQk1D".equals(appGid)) {
                if (userType == null || userType < 0) {
                    throw new OauthException("请重新登录!");
                } else {
                    if (userType != 4) {
                        Long dataAuth = user.getDataAuth();
                        if (dataAuth == null || dataAuth < 0) {
                            throw new OauthException("请重新登录!");
                        }
                    }
                }
            }
            return user;
        } else {
            if ("MS8xL1NMLUNUUC1XRUItUE1D".equals(appGid) || "MS8yL1NMLUNUUC1XRUItQk1D".equals(appGid)) {
                throw new OauthException("请重新登录!");
            }

        }
        return null;
    }

    /**
     * 刷新当前用户的缓存信息
     *
     * @return
     */
    public static void refreshCurrentUser(UserDTO userDTO) {
        TokenDTO tokenDTO = getHeaderToken();
        if (tokenDTO != null) {
            operations.set(userTokenKey + tokenDTO.getUserGid(), JSON.toJSONString(userDTO));
        }

    }

    /**
     * 获取当前用户的数据权限
     *
     * @return
     */
    public static UserDataAuthDTO getCurrentUserDataAuths() {
        TokenDTO tokenDTO = getHeaderToken();
        if(tokenDTO==null){
            return null;
        }
//        ValueOperations<String, Object> operations = apiUtils.redisTemplate.opsForValue();
        String o = (String) operations.get("USER_DATA_AUTHS_" + tokenDTO.getUserGid());
        UserDataAuthDTO userDataAuthDTO = new UserDataAuthDTO();
        if (o != null) {
            userDataAuthDTO = JSON.parseObject(o, UserDataAuthDTO.class);
        }
        return userDataAuthDTO;
//        String jsonStr = "{\"orgIds\":[1001],\"storeIds\":[1001],\"userIds\":[38]}";
//        UserDataAuthDTO userDataAuthDTO = new UserDataAuthDTO();
//        if (jsonStr != null) {
//            userDataAuthDTO = JSON.parseObject(jsonStr, UserDataAuthDTO.class);
//        }
//        return userDataAuthDTO;
    }


    /**
     * 刷新当前用户的数据权限
     *
     * @return
     */
    public static void refreshCurrentUserDataAuths(UserDataAuthDTO userDataAuthDTO) {
        TokenDTO tokenDTO = getHeaderToken();
        if(tokenDTO!=null)
        operations.set("USER_DATA_AUTHS_" + tokenDTO.getUserGid(), JSON.toJSONString(userDataAuthDTO));
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUserId(null);
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     */
    public static Long getCurrentUserId(Long defaultVal) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            return defaultVal;
        }
    }


    /**
     * 取得当前用户是否会员
     *
     * @return 用户是否会员
     */
    public static boolean getCurrentUserIsMember() {
        UserDTO currentUser = getCurrentUser();
        if (currentUser != null) {
            return 99 == currentUser.getUserType() ? true : false;
        }
        return false;
    }

    /**
     * 获取当前用户区域
     *
     * @return
     */
    public static Integer getCurrentUserRegionCode() {
        UserDTO currentUser = getCurrentUser();
        if(currentUser!=null){
            return currentUser.getRegionCode();
        }
        return null;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取默认时间
     *
     * @return
     */
    public static LocalDateTime getDefaultDateTime() {

        LocalDateTime defaultDateTime = LocalDateTime.of(1900, 01, 01, 0, 0, 0);
        return defaultDateTime;
    }

    public static TokenDTO getHeaderToken() {
        HttpServletRequest request = SpringContext.getRequest();
        if (request == null) {
            return null;
        }
        String appGid = request.getHeader("appGid");
        String appToken = request.getHeader("appToken");
        String funcGid = request.getHeader("funcGid");
        String funcToken = request.getHeader("funcToken");
        String userGid = request.getHeader("userGid");
        String userToken = request.getHeader("userToken");
        String dvid = request.getHeader("DVID");//设备ID
        String dvua = request.getHeader("DVUA");//设备名称
        String os = request.getHeader("OS");//设备版本
        String nt = request.getHeader("NT");//网络
        String region = request.getHeader("REGION");//地区
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAppGid(appGid);
        tokenDTO.setAppToken(appToken);
        tokenDTO.setFuncGid(funcGid);
        tokenDTO.setFuncToken(funcToken);
        tokenDTO.setUserGid(userGid);
        tokenDTO.setUserToken(userToken);
        tokenDTO.setLoginDevice(dvid);
        tokenDTO.setLoginDeviceName(dvua);
        String ip = getLocalIp(request);
        tokenDTO.setLoginIp(ip);
        if (region != null) {
            tokenDTO.setLoginRegion(Integer.valueOf(region));
        } else {
//            if (ip != null) {
//                try {
//                    URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
//                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
//                    urlCon.connect();
//                    InputStream inputStream = urlCon.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                    String ipJson = bufferedReader.readLine();
//                    JSONObject ipMap = JSONObject.parseObject(ipJson);
//                    JSONObject data = (JSONObject) ipMap.get("data");
//                    region = (String) data.get("city_id");
//                    tokenDTO.setLoginRegion(Integer.valueOf(region));
//                } catch (Exception e) {
//
//                }
//            }
        }
        return tokenDTO;
    }

    /**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     *
     * @param request
     * @return ip
     */
    private static String getLocalIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if (forwarded != null) {
                    forwarded = forwarded.split(",")[0];
                }
                ip = forwarded;
            }
        }
        return ip;
    }

    public static List<Map<String, Object>> listToTree(List<Map<String, Object>> data, String idName, String pIdName, Integer regionLevel) {
        Map<Integer, List<Map<String, Object>>> supper = new HashMap<>();
        Map<Integer, Map<Object, List<Map<String, Object>>>> supperKeyMaps = new HashMap<>();

        for (int i = 0; i <= regionLevel; i++) {
            int finalI = i;
            List<Map<String, Object>> items = data.stream().filter(map -> map != null && !map.isEmpty() && M.integer(map, "regionLevel").equals(finalI)).collect(Collectors.toList());
            supper.put(i, items);
            Map<Object, List<Map<String, Object>>> itemKeyMaps = items.stream().collect(Collectors.groupingBy(o -> o.get(pIdName)));
            supperKeyMaps.put(i, itemKeyMaps);
        }

        for (int i = regionLevel; i > 0; i--) {
            int finalI = i;
            supper.get(finalI - 1).forEach(map -> {
                map.put("children", supperKeyMaps.get(finalI).get(map.get(idName)));
            });
        }
        return supper.get(1);
    }

}

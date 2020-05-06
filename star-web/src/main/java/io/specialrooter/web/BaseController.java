package io.specialrooter.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.specialrooter.context.util.ApiUtils;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.specialrooter.context.model.UserDTO;
import io.specialrooter.web.request.RequestList;
import io.specialrooter.web.request.RequestPage;
import io.specialrooter.web.request.SimpleRequestList;
import io.specialrooter.web.request.SimpleRequestPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.specialrooter.plus.mybatisplus.util.DateUtils.DATE;

public class BaseController<T> {

    //    public RequestPage getRequestPage(RequestModel requestModel){
//        return ZBeanUtils.copyProperties(requestModel.getCmdPostData(), RequestPage.class);
//    }
    @Autowired
    protected JdbcTemplate jdbcTemplate;

//    @Autowired
//    protected ServletServerHttpRequest request;

    public QueryWrapper<T> queryExpress(RequestPage requestPage) {
        return queryExpressList(requestPage);
    }


    public QueryWrapper<T> queryExpress(SimpleRequestPage simpleRequestPage){
        return queryExpressList(simpleRequestPage);
    }

    public QueryWrapper<T> queryExpressList(SimpleRequestList simpleRequestList){
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        String regex = "^(I|E)(C|D|N)(EQ|CP|GT|LT|GE|LE|BT|IN)(\\w).*(\\w_)*";

        simpleRequestList.getWhereConditions().forEach((x,y) -> {
            String m = x;
            Object k = y;
            boolean matches = Pattern.matches(regex, m);
            if (matches) {
                String sign = m.substring(0, 1), type = m.substring(1, 2), option = m.substring(2, 4), field = m.substring(4);
                if ("I".equals(sign)) {
                    switch (option) {
                        case "EQ":
                            queryWrapper.eq(field, String.valueOf(k));
                            break;
                        case "IN":
                            List list = (List) k;
                            queryWrapper.in(field, list.toArray());
                            break;
                        case "CP":
                            String val = String.valueOf(k).replace("*", "%");
                            if (val.indexOf("%") == -1) {
                                val = "%" + val + "%";
                            }
                            queryWrapper.like(field, val);
                            break;
                        case "BT":
//                            String[] k2 = (String[]) k;
//                            System.out.println(k.getClass().getName());
                            ArrayList k2 = (ArrayList) k;
                            Assert.notEmpty(k2, "时间区间查询不能为空");
                            String start = String.valueOf(k2.get(0));
                            String end = String.valueOf(k2.get(1));
                            if(start.matches(DATE)){
                                start+=" 00:00:00";
                            }

                            if(end.matches(DATE)){
                                end+=" 23:59:59";
                            }
                            queryWrapper.between(field, convert(type, start), convert(type, end));
                            break;
                        case "GT":
                            queryWrapper.gt(field, k);
                            break;
                        case "LE":
                            queryWrapper.le(field, k);
                            break;
                        case "GE":
                            queryWrapper.ge(field, k);
                            break;
                        case "LT":
                            queryWrapper.lt(field, k);
                            break;
                        default:
                            break;
                    }
                } else if ("E".equals(sign)) {
                    switch (option) {
                        case "EQ":
                            queryWrapper.ne(field, String.valueOf(k));
                            break;
                        case "IN":
                            queryWrapper.notIn(field, k);
                            break;
                        case "CP":
                            String val = String.valueOf(k).replace("*", "%");
                            if (val.indexOf("%") == -1) {
                                val = "%" + val + "%";
                            }
                            queryWrapper.notLike(field, val);
                            break;
                        case "BT":
                            ArrayList k2 = (ArrayList) k;
                            Assert.notEmpty(k2, "时间区间查询不能为空");
                            String start = String.valueOf(k2.get(0));
                            String end = String.valueOf(k2.get(1));
                            if(start.matches(DATE)){
                                start+=" 00:00:00";
                            }

                            if(end.matches(DATE)){
                                end+=" 23:59:59";
                            }

                            queryWrapper.notBetween(field, convert(type, start), convert(type, end));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                queryWrapper.like(m, k);
            }
        });

        simpleRequestList.getSortKeyModes().forEach((x,y) -> {
            String m = x;
            Object k = y;

            String orderBy = String.valueOf(k).toUpperCase();
            switch (orderBy) {
                case "ASC":
                    queryWrapper.orderByAsc(m);
                    break;
                case "DESC":
                    queryWrapper.orderByDesc(m);
                    break;
            }
        });

        return queryWrapper;
    }

    public QueryWrapper<T> queryExpressList(RequestList requestList) {
        return (QueryWrapper<T>) requestList.queryExpressList();

        /*QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        String regex = "^(I|E)(C|D|N)(EQ|CP|GT|LT|GE|LE|BT|IN)(\\w).*(\\w_)*";

        //支持List<Standard> 与 Map
        ArrayList<LinkedHashMap> whereConditions = null;
        ArrayList<LinkedHashMap> sortKeyModes = null;
        Map<String,Object> whereConditionsForMap = null;
        Map<String,Object> sortKeyModesMap = null;
        if(requestList.getWhereConditions() instanceof Map){
            whereConditionsForMap = (Map<String, Object>) requestList.getWhereConditions();
        }else if(requestList.getWhereConditions() instanceof ArrayList){
            whereConditions = (ArrayList<LinkedHashMap>)requestList.getWhereConditions();
        }

        if(requestList.getSortKeyModes() instanceof Map){
            sortKeyModesMap = (Map<String, Object>) requestList.getSortKeyModes();
        }else if(requestList.getSortKeyModes() instanceof ArrayList){
            sortKeyModes = (ArrayList<LinkedHashMap>) requestList.getSortKeyModes();
        }

        if(whereConditions!=null){
            whereConditions.stream().filter(map->!ObjectUtils.isEmpty(map.get("value"))
                    && !ObjectUtils.isEmpty(map.get("column"))
                    && requestList.getLogicalMappers().contains(String.valueOf(map.get("column")))
                    && requestList.getMultipleTableJoins().contains(String.valueOf(map.get("column")))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");
                boolean matches = Pattern.matches(regex, m);
                if (matches) {
                    String sign = m.substring(0, 1), type = m.substring(1, 2), option = m.substring(2, 4), field = m.substring(4);
                    //requestList.getColumnPrefixMappers().get("")
                    if ("I".equals(sign)) {
                        switch (option) {
                            case "EQ":
                                queryWrapper.eq(field, String.valueOf(k));
                                break;
                            case "IN":
                                List list = (List) k;
                                queryWrapper.in(field, list.toArray());
                                break;
                            case "CP":
                                String val = String.valueOf(k).replace("*", "%");
                                if (val.indexOf("%") == -1) {
                                    val = "%" + val + "%";
                                }
                                queryWrapper.like(field, val);
                                break;
                            case "BT":
//                            String[] k2 = (String[]) k;
//                            System.out.println(k.getClass().getName());
                                ArrayList k2 = (ArrayList) k;
                                Assert.notEmpty(k2, "时间区间查询不能为空");
                                queryWrapper.between(field, convert(type, String.valueOf(k2.get(0))), convert(type, String.valueOf(k2.get(1))));
                                break;
                            case "GT":
                                queryWrapper.gt(field, k);
                                break;
                            case "LE":
                                queryWrapper.le(field, k);
                                break;
                            case "GE":
                                queryWrapper.ge(field, k);
                                break;
                            case "LT":
                                queryWrapper.lt(field, k);
                                break;
                            default:
                                break;
                        }
                    } else if ("E".equals(sign)) {
                        switch (option) {
                            case "EQ":
                                queryWrapper.ne(field, String.valueOf(k));
                                break;
                            case "IN":
                                queryWrapper.notIn(field, k);
                                break;
                            case "CP":
                                String val = String.valueOf(k).replace("*", "%");
                                if (val.indexOf("%") == -1) {
                                    val = "%" + val + "%";
                                }
                                queryWrapper.notLike(field, val);
                                break;
                            case "BT":
                                ArrayList k2 = (ArrayList) k;
                                Assert.notEmpty(k2, "时间区间查询不能为空");
                                queryWrapper.notBetween(field, convert(type, String.valueOf(k2.get(0))), convert(type, String.valueOf(k2.get(1))));
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    queryWrapper.like(m, k);
                }
            });
        }

        if(sortKeyModes!=null){
            sortKeyModes.stream().filter(map->!ObjectUtils.isEmpty(map.get("value"))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");

                String orderBy = String.valueOf(k).toUpperCase();
                switch (orderBy) {
                    case "ASC":
                        queryWrapper.orderByAsc(m);
                        break;
                    case "DESC":
                        queryWrapper.orderByDesc(m);
                        break;
                }
            });
        }

        if(whereConditionsForMap!=null) {
            whereConditionsForMap.forEach((x, y) -> {
                String m = x;
                Object k = y;
                if(!ObjectUtils.isEmpty(k)){
                    boolean matches = Pattern.matches(regex, m);
                    if (matches) {
                        String sign = m.substring(0, 1), type = m.substring(1, 2), option = m.substring(2, 4), field = m.substring(4);
                        if ("I".equals(sign)) {
                            switch (option) {
                                case "EQ":
                                    queryWrapper.eq(field, String.valueOf(k));
                                    break;
                                case "IN":
                                    List list = (List) k;
                                    queryWrapper.in(field, list.toArray());
                                    break;
                                case "CP":
                                    String val = String.valueOf(k).replace("*", "%");
                                    if (val.indexOf("%") == -1) {
                                        val = "%" + val + "%";
                                    }
                                    queryWrapper.like(field, val);
                                    break;
                                case "BT":
//                            String[] k2 = (String[]) k;
//                            System.out.println(k.getClass().getName());
                                    ArrayList k2 = (ArrayList) k;
                                    Assert.notEmpty(k2, "时间区间查询不能为空");
                                    queryWrapper.between(field, convert(type, String.valueOf(k2.get(0))), convert(type, String.valueOf(k2.get(1))));
                                    break;
                                case "GT":
                                    queryWrapper.gt(field, k);
                                    break;
                                case "LE":
                                    queryWrapper.le(field, k);
                                    break;
                                case "GE":
                                    queryWrapper.ge(field, k);
                                    break;
                                case "LT":
                                    queryWrapper.lt(field, k);
                                    break;
                                default:
                                    break;
                            }
                        } else if ("E".equals(sign)) {
                            switch (option) {
                                case "EQ":
                                    queryWrapper.ne(field, String.valueOf(k));
                                    break;
                                case "IN":
                                    queryWrapper.notIn(field, k);
                                    break;
                                case "CP":
                                    String val = String.valueOf(k).replace("*", "%");
                                    if (val.indexOf("%") == -1) {
                                        val = "%" + val + "%";
                                    }
                                    queryWrapper.notLike(field, val);
                                    break;
                                case "BT":
                                    ArrayList k2 = (ArrayList) k;
                                    Assert.notEmpty(k2, "时间区间查询不能为空");
                                    queryWrapper.notBetween(field, convert(type, String.valueOf(k2.get(0))), convert(type, String.valueOf(k2.get(1))));
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        queryWrapper.like(m, k);
                    }
                }
            });
        }

        if(sortKeyModesMap!=null) {
            sortKeyModesMap.forEach((x,y) -> {
                String m = x;
                Object k = y;
                if(!ObjectUtils.isEmpty(k)){
                    String orderBy = String.valueOf(k).toUpperCase();
                    switch (orderBy) {
                        case "ASC":
                            queryWrapper.orderByAsc(m);
                            break;
                        case "DESC":
                            queryWrapper.orderByDesc(m);
                            break;
                    }
                }

            });
        }

        return queryWrapper;*/
    }

    public Object convert(String type, String value) {
        switch (type) {
            case "C":
                return String.valueOf(value);
            case "D":
                return DateUtils.date(value);
            case "N":
                return new BigDecimal(value);
            case "B":
                return Boolean.valueOf(value);
            default:
                return null;
        }
    }

//    public <D> D getParameter(RequestModel requestModel,String name){
//        return (D) requestModel.getCmdPostData();
//    }

//    public Serializable getDataForId(RequestModel requestModel){
//        return getParameter(requestModel,"id");
//    }

//    public List<String> getDataForIds(RequestModel requestModel){
//        String[] id = getParameter(requestModel, "id");
//        return Arrays.asList(id);
//    }

//    public Map<String, Object> getDataForMap(RequestModel requestModel){
//        return getParameter(requestModel,"data");
//    }

//    public <T> T getDataForEntity(RequestModel requestModel){
//        Map<String, Object> dataMap = getDataForMap(requestModel);
//        Object obj = ZBeanUtils.copyProperties(dataMap, getEntityClass(), true);
//        return (T) obj;
//    }

    public Class getEntityClass() {
        return (Class) getParameterizedType()[0];
    }

    private Type[] getParameterizedType() {
        Type[] types = null;
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            types = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        }
        Assert.notNull(types, "请配置BaseController<Entity>参数，才能直接使用标准模板");
        return types;
    }

    /**
     * 通过资源ID取得资源的索引对象
     *
     * @param id 资源ID
     * @return BdResourceIndex对象或null
     */
    public Map<String, String> getResourceIndex(long id) {
        return null;
    }

    /**
     * 通过资源ID取得资源的数据对象
     *
     * @param id 资源ID
     * @return BdResourceData对象或null
     */
    public Map<String, String> getResourceData(long id) {
        return null;
    }

    /**
     * 通过资源ID取得资源的URL地址
     *
     * @param id 资源ID
     * @return 资源的URL地址或空字符串
     */
    public String getResourceUrl(long id) {
        return "http://www.380star.com/img/xinglian_logo.png";
    }

    /**
     * 取得当前用户令牌集合
     *
     * @return 当前用户令牌集合
     */
    public Map<String, String> getCurrentUserToken() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("appGid", "MS8yL1NMLUNUUC1XRUItQk1D");
        result.put("appToken", "4901821f59ea13497003e8187d589c5d");
        result.put("funcGId", "MSxTTC1DVFAtV0VCLVBNQw==");
        result.put("funcToken", "92D10CD8074DE27243DC3C64066F467D");
        result.put("userGid", "239f6199a44d11e985790050568eca1c");
        result.put("userToken", "DEB89CAF2D9F295CD7327508D6D48BB33224CF053049573915FD4CAD956F55A1");

        return result;
    }



    /**
     * 取得当前会员信息(最终用户oa_member)
     *
     * @return 当前会员对象信息
     */
//    public UserDTO getCurrentMemberData() {
//        return ApiUtils.getCurrentUser();
//    }

    /**
     * 取得当前会员编号(最终用户oa_member)
     *
     * @return 当前会员ID
     */
//    public long getCurrentMemberId() {
//        return getCurrentMemberData().getId();
//    }

    /**
     * 获取当前登录用户
     * @return
     */
    public UserDTO getCurrentUserData() {
        return ApiUtils.getCurrentUser();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     */
    public Long getCurrentUserId() {
        return ApiUtils.getCurrentUserId();
    }
    /**
     * 取得当前用户是否会员
     *
     * @return 用户是否会员
     */
    public boolean getCurrentUserIsMember() {
        return ApiUtils.getCurrentUserIsMember();
    }

    /**
     * 获取当前用户区域
     * @return
     */
    public Integer getRegionCode(){
        return ApiUtils.getCurrentUserRegionCode();
    }


    /**
     * 数据库唯一值验证通用方法
     *
     * @param tableName   验证的表名
     * @param columnName  验证的列名
     * @param columnValue 验证的列值
     * @param ignoreCase  是否忽略大小写 是为忽略大小写
     * @param excludeId   条件判断时排除的ID,通常用于更新时
     * @return 是否重复  true 重复(值已经在数据库在存在)   false 不重复 (值不在数据库中存在)
     */
    public boolean sqlUniqueCheck(String tableName, String columnName, String columnValue, boolean ignoreCase, long excludeId) {
        boolean result = false;
        if (null == tableName) {
            tableName = "";
        }
        if (null == columnName) {
            columnName = "";
        }
        if (null == columnValue) {
            columnValue = "";
        }
        tableName = tableName.trim().toLowerCase();
        columnName = columnName.trim().toLowerCase();
        columnValue = columnValue.trim();
        if (ignoreCase) {
            columnValue = columnValue.toLowerCase();
        }
        if (tableName.length() > 0 && columnName.length() > 0 && columnValue.length() > 0) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(id) from ");
            sql.append(tableName);
            sql.append(" where state_deleted=0 and ");
            if (ignoreCase) {
                sql.append("lower(");
            }
            sql.append(columnName);
            if (ignoreCase) {
                sql.append(")");
            }
            sql.append("=\'");
            sql.append(columnValue);
            sql.append("\'");
            if (excludeId > 0) {
                sql.append(" and id<>");
                sql.append(excludeId);
            }
            result = jdbcTemplate.queryForObject(sql.toString(), Integer.class) > 0;
        }
        return result;
    }

    /**
     * 数据库唯一值验证通用方法(不排除任何ID)
     *
     * @param tableName   验证的表名
     * @param columnName  验证的列名
     * @param columnValue 验证的列值
     * @param ignoreCase  是否忽略大小写 是为忽略大小写
     * @return 是否重复  true 重复(值已经在数据库在存在)   false 不重复 (值不在数据库中存在)
     */
    public boolean sqlUniqueCheck(String tableName, String columnName, String columnValue, boolean ignoreCase) {
        return sqlUniqueCheck(tableName, columnName, columnValue, ignoreCase, 0L);
    }

    /**
     * 数据库唯一值验证通用方法(默认严格区别大小写)
     *
     * @param tableName   验证的表名
     * @param columnName  验证的列名
     * @param columnValue 验证的列值
     * @param excludeId   条件判断时排除的ID,通常用于更新时
     * @return 是否重复  true 重复(值已经在数据库在存在)   false 不重复 (值不在数据库中存在)
     */
    public boolean sqlUniqueCheck(String tableName, String columnName, String columnValue, long excludeId) {
        return sqlUniqueCheck(tableName, columnName, columnValue, false, excludeId);
    }


}

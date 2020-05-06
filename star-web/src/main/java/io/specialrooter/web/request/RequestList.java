package io.specialrooter.web.request;

import com.alibaba.fastjson.util.TypeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.google.common.base.CaseFormat;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

import static io.specialrooter.plus.mybatisplus.util.DateUtils.DATE;

@ApiModel("查询参数")
@Data
public class RequestList /*extends JSONObject*/ {
    @ApiModelProperty(value = "组态查询条件，追加查询条件")
    protected Object whereConditions;
    @ApiModelProperty(value = "排序字段")
    protected Object sortKeyModes;
    @ApiModelProperty(value = "脱敏字段")
    protected Object desenKeyVals;
    @ApiModelProperty(value = "多表字段前缀映射", example = "{aid=a.id,bid=bid}", hidden = true)
    protected Map<String, String> columnPrefixMappers = new HashMap<>();
    @ApiModelProperty(value = "多表连接动态条件", hidden = true)
    protected List<String> multipleTableJoins = new ArrayList<>();
    @ApiModelProperty(value = "映射逻辑判断条件", hidden = true)
    protected List<String> logicalMappers = new ArrayList<>();

    public void setLogicalMappers(String... columns) {
        for (String column : columns) {
            logicalMappers.add(column);
        }
    }

    public void setMultipleTableJoins(String... columns) {
        for (String column : columns) {
            multipleTableJoins.add(column);
        }
    }

    @ApiModelProperty(hidden = true)
    public Map<String, Object> getWhereConditionsForMap() {
        ArrayList<LinkedHashMap> whereConditions = null;
        Map<String, Object> whereConditionsForMap = new HashMap<>();

        if (this.getWhereConditions() instanceof Map) {
            whereConditionsForMap = (Map<String, Object>) this.getWhereConditions();
        } else if (this.getWhereConditions() instanceof ArrayList) {
            whereConditions = (ArrayList<LinkedHashMap>) this.getWhereConditions();
            Map<String, Object> finalWhereConditionsForMap = whereConditionsForMap;
            whereConditions.stream().filter(map -> !ObjectUtils.isEmpty(map.get("value"))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");
                finalWhereConditionsForMap.put(m, k);
            });
        }
        return whereConditionsForMap;
    }

    @ApiModelProperty(hidden = true)
    public Map<String, Object> getSortKeyModesForMap() {
        ArrayList<LinkedHashMap> sortKeyModes = null;
        Map<String, Object> sortKeyModesMap = new HashMap<>();

        if (this.getSortKeyModes() instanceof Map) {
            sortKeyModesMap = (Map<String, Object>) this.getSortKeyModes();
        } else if (this.getSortKeyModes() instanceof ArrayList) {
            sortKeyModes = (ArrayList<LinkedHashMap>) this.getSortKeyModes();
            Map<String, Object> finalSortKeyModesMap = sortKeyModesMap;
            sortKeyModes.stream().filter(map -> !ObjectUtils.isEmpty(map.get("value"))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");
                finalSortKeyModesMap.put(m, k);
            });
        }

        return sortKeyModesMap;
    }

    @ApiModelProperty(hidden = true)
    public void setLogicSqlInjector(QueryWrapper queryWrapper, String... tableAlias) {
        for (String alias : tableAlias) {
            queryWrapper.eq(alias + ".state_deleted", 0);
        }
    }

    public Object getObject(Object key) {
        Object val = this.getWhereConditionsForMap().get(key);
        if (val == null && key instanceof Number) {
            val = this.getWhereConditionsForMap().get(key.toString());
        }

        return val;
    }

    public <T> T get(String column) {
        return (T) this.getWhereConditionsForMap().get(column);
    }

    public Integer getInteger(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToInt(value);
    }

    public int getIntValue(String key) {
        Object value = this.getObject(key);
        Integer intVal = TypeUtils.castToInt(value);
        return intVal == null ? 0 : intVal;
    }

    public Long getLong(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToLong(value);
    }

    public long getLongValue(String key) {
        Object value = this.getObject(key);
        Long longVal = TypeUtils.castToLong(value);
        return longVal == null ? 0L : longVal;
    }

    public Float getFloat(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToFloat(value);
    }

    public float getFloatValue(String key) {
        Object value = this.getObject(key);
        Float floatValue = TypeUtils.castToFloat(value);
        return floatValue == null ? 0.0F : floatValue;
    }

    public Double getDouble(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToDouble(value);
    }

    public double getDoubleValue(String key) {
        Object value = this.getObject(key);
        Double doubleValue = TypeUtils.castToDouble(value);
        return doubleValue == null ? 0.0D : doubleValue;
    }

    public BigDecimal getBigDecimal(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToBigInteger(value);
    }

    public String getString(String key) {
        Object value = this.getObject(key);
        return value == null ? null : value.toString();
    }

    public Date getDate(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToDate(value);
    }

    public java.sql.Date getSqlDate(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToSqlDate(value);
    }

    public Timestamp getTimestamp(String key) {
        Object value = this.getObject(key);
        return TypeUtils.castToTimestamp(value);
    }

    //替换字段
    String replaceField(String field) {
        //替换映射字段
        String fieldPre = columnPrefixMappers.get(field);
        if (fieldPre != null) {
            field = fieldPre;
        }
        String repStr = "abcdefghijklmnopqrstuvwxyz";
        if (field.length() > 2) {
            String fieldOne = field.substring(0, 1);
            String fieldTwo = field.substring(1, 2);
            String fieldEnd = field.substring(2);
            if ("_".equals(fieldTwo)) {
                field = fieldOne + "." + fieldEnd;
            }
        }

        return field;
    }

    public QueryWrapper queryExpressList() {
        QueryWrapper queryWrapper = new QueryWrapper<>();

        String regex = "^(I|E)(C|D|N)(EQ|CP|GT|LT|GE|LE|BT|IN)(\\w).*(\\w_)*";

        //支持List<Standard> 与 Map
        ArrayList<LinkedHashMap> whereConditions = null;
        ArrayList<LinkedHashMap> sortKeyModes = null;
        Map<String, Object> whereConditionsForMap = null;
        Map<String, Object> sortKeyModesMap = null;
        if (getWhereConditions() instanceof Map) {
            whereConditionsForMap = (Map<String, Object>) getWhereConditions();
        } else if (getWhereConditions() instanceof ArrayList) {
            whereConditions = (ArrayList<LinkedHashMap>) getWhereConditions();
        }

        if (getSortKeyModes() instanceof Map) {
            sortKeyModesMap = (Map<String, Object>) getSortKeyModes();
        } else if (getSortKeyModes() instanceof ArrayList) {
            sortKeyModes = (ArrayList<LinkedHashMap>) getSortKeyModes();
        }

        if (whereConditions != null) {
            whereConditions.stream().filter(map -> !ObjectUtils.isEmpty(map.get("value"))
                    && !ObjectUtils.isEmpty(map.get("column"))
                    && !getLogicalMappers().contains(String.valueOf(map.get("column")))
                    && !getMultipleTableJoins().contains(String.valueOf(map.get("column")))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");
                boolean matches = Pattern.matches(regex, m);
                if (matches) {
                    String sign = m.substring(0, 1), type = m.substring(1, 2), option = m.substring(2, 4), field = m.substring(4);
                    field = replaceField(field);
                    field = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);
                    //requestList.getColumnPrefixMappers().get("")
                    if ("I".equals(sign)) {
                        switch (option) {
                            case "EQ":
                                queryWrapper.eq(field, k);
                                break;
                            case "IN":
                                List list = (List) k;
                                queryWrapper.in(field, list.toArray());
                                break;
                            case "CP":
                                String val = String.valueOf(k).replace("*", "%");
//                                if (val.indexOf("%") == -1) {
//                                    val = "%" + val + "%";
//                                }
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
                                queryWrapper.ne(field, k);
                                break;
                            case "IN":
                                queryWrapper.notIn(field, k);
                                break;
                            case "CP":
                                String val = String.valueOf(k).replace("*", "%");
//                                if (val.indexOf("%") == -1) {
//                                    val = "%" + val + "%";
//                                }
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
        }

        if (sortKeyModes != null) {
            sortKeyModes.stream().filter(map -> !ObjectUtils.isEmpty(map.get("value")) && !ObjectUtils.isEmpty(map.get("column")) && !getLogicalMappers().contains(String.valueOf(map.get("column")))
                    && !getMultipleTableJoins().contains(String.valueOf(map.get("column")))).forEach(map -> {
                String m = String.valueOf(map.get("column"));
                Object k = map.get("value");
                //替换映射字段
                m = replaceField(m);
                m = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, m);
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

        if (whereConditionsForMap != null) {
            whereConditionsForMap.forEach((x, y) -> {
                String m = x;
                Object k = y;

                if (!ObjectUtils.isEmpty(k) && !getLogicalMappers().contains(m)
                        && !getMultipleTableJoins().contains(m)) {

                    boolean matches = Pattern.matches(regex, m);
                    if (matches) {
                        String sign = m.substring(0, 1), type = m.substring(1, 2), option = m.substring(2, 4), field = m.substring(4);
                        //替换映射字段
                        field = replaceField(field);
                        field = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);
                        if ("I".equals(sign)) {
                            switch (option) {
                                case "EQ":
                                    queryWrapper.eq(field, k);
                                    break;
                                case "IN":
                                    List list = (List) k;
                                    queryWrapper.in(field, list.toArray());
                                    break;
                                case "CP":
                                    String val = String.valueOf(k).replace("*", "%");
//                                    if (val.indexOf("%") == -1) {
//                                        val = "%" + val + "%";
//                                    }
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
                                    queryWrapper.ne(field, k);
                                    break;
                                case "IN":
                                    queryWrapper.notIn(field, k);
                                    break;
                                case "CP":
                                    String val = String.valueOf(k).replace("*", "%");
//                                    if (val.indexOf("%") == -1) {
//                                        val = "%" + val + "%";
//                                    }
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
                }
            });
        }

        if (sortKeyModesMap != null) {
            sortKeyModesMap.forEach((x, y) -> {
                String m = x;
                Object k = y;
                //替换映射字段
                m = replaceField(m);
                m = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, m);
                if (!ObjectUtils.isEmpty(k)) {
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

        return queryWrapper;
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
}

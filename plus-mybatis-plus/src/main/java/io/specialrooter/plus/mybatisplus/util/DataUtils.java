package io.specialrooter.plus.mybatisplus.util;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.specialrooter.plus.mybatisplus.model.BaseModel;
import io.specialrooter.plus.mybatisplus.model.SupperClass;
import io.specialrooter.util.M;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Ai
 */
public class DataUtils extends org.springframework.beans.BeanUtils {

    public static List<Map<String, Object>> toMaps(List beans) {
        return BeanUtils.beansToMaps(beans);
    }

    public static Map<String, Object> toMap(Object bean) {
        return BeanUtils.beanToMap(bean);
    }

    public static <T> List<T> toBeans(List<Map<String, Object>> maps, Class<T> clazz) {
        return BeanUtils.mapsToBeans(maps, clazz);
    }

    public static <T> T toBean(Map<String, Object> map, Class<T> clazz) {
        return BeanUtils.mapToBean(map, clazz);
    }

    public static Map<String, List<Map<String, Object>>> toGroup(List<Map<String, Object>> maps, String groupField) {
        return toGroup(maps, Collectors.groupingBy((o) -> {
            return String.valueOf(((Map) o).get(groupField));
        }));
    }

    public static <T> List<T> convert(List<?> list, Class<T> clazz) {
        List<T> newList = list.stream().map(obj -> {
            T o = copyProperties(obj, clazz);
            return o;
        }).collect(Collectors.toList());
        return newList;
    }

    /**
     * 字符串 转换为对应的 UTF-8编码
     * 实现方式：将字符串转10进制数侯转化成16进制数
     *
     * @param s
     * @return
     */
    public static String convertStringToUTF8(String s) {
        StringBuffer sb = new StringBuffer();
        try {
            char c;
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                byte[] b;

                b = Character.toString(c).getBytes("utf-8");

                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append(Integer.toHexString(k).toUpperCase());
                }
            }
            System.err.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UTF-8编码 转换为对应的 字符串
     * 实现方式：将16进制数转化成有符号的十进制数
     *
     * @param s
     * @return
     */
    public static String convertUTF8ToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        try {
            s = s.toUpperCase();
            int total = s.length() / 2;
            //标识字节长度
            int pos = 0;
            byte[] buffer = new byte[total];
            for (int i = 0; i < total; i++) {
                int start = i * 2;
                //将字符串参数解析为第二个参数指定的基数中的有符号整数。
                buffer[i] = (byte) Integer.parseInt(s.substring(start, start + 2), 16);
                pos++;
            }
            //通过使用指定的字符集解码指定的字节子阵列来构造一个新的字符串。
            //新字符串的长度是字符集的函数，因此可能不等于子数组的长度。
            return new String(buffer, 0, pos, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 显示 maven 引入 groupIds，用于优化spring boot 项目打包体积
     * <build>
     * <plugins>
     * <plugin>
     * <groupId>org.springframework.boot</groupId>
     * <artifactId>spring-boot-maven-plugin</artifactId>
     * <executions>
     * <execution>
     * <goals>
     * <goal>repackage</goal>
     * </goals>
     * </execution>
     * </executions>
     * <configuration>
     * <fork>true</fork>
     * <layout>ZIP</layout>
     * <includes>
     * <include>
     * <!-- 排除所有Jar -->
     * <groupId>nothing</groupId>
     * <artifactId>nothing</artifactId>
     * </include>
     * </includes>
     * <!--去除在生产环境中不变的依赖-->
     * <excludeGroupIds>
     * <!-- mvnDependencyTreeGroupIds 逗号进行分隔 -->
     * <excludeGroupIds>
     * </configuration>
     * <plugin>
     * <plugins>
     * <build>
     */
    public static void mvnDependencyTreeGroupIds() {
        String commandStr = "mvn dependency:tree";
        BufferedReader br = null;
        Set<String> set = new HashSet<>();
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[INFO]") && (line.indexOf("\\-") != -1 || line.indexOf("+-") != -1)) {
                    set.add(line.substring(line.indexOf("-") + 2, line.indexOf(":")));
                }

            }
            set.forEach(s -> {
                System.out.println(s + ",");
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void mvnDependencyTree() {
        String commandStr = "mvn dependency:tree";
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                //if(line.startsWith("[INFO]") && (line.indexOf("\\-")!=-1 || line.indexOf("+-")!=-1))
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行cmd命令，显示cmd命令结果
     *
     * @param commandStr
     */
    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Map<String, List<Map<String, Object>>> toGroup(List<Map<String, Object>> maps, Collector collector) {
        return (Map) maps.stream().collect(collector);
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     *
     * @param o 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map toMapPlus(Object o)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = o.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                JsonProperty annotation = ReflectionUtils.getDeclaredField(o, propertyName).getAnnotation(JsonProperty.class);
                if (annotation != null) {
                    propertyName = annotation.value();
                }

                Object result = readMethod.invoke(o, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

    public static List<Map<String, Object>> toTreePlus(List<? extends SupperClass> data) {
        return toTreePlus(data, "id", "parentId", false);
    }

    public static List<Map<String, Object>> toTreePlus(List<? extends SupperClass> data, boolean showKey) {
        return toTreePlus(data, "id", "parentId", showKey);
    }

    public static List<Map<String, Object>> toTreePlus(List<? extends SupperClass> data, String id, String parentId) {
        return toTreePlus(data, id, parentId, false);
    }

    public static List<Map<String, Object>> toTreePlus(List<? extends SupperClass> data, String id, String parentId, boolean showKey) {
        List<Map<String, Object>> maps = (List<Map<String, Object>>) data.stream().map(x -> {
            Map<String, Object> stringObjectMap = null;
            try {
                stringObjectMap = toMapPlus(x);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return stringObjectMap;
        }).collect(Collectors.toList());
        return toTreeUltimate(maps, id, parentId, showKey);
    }


    public static List<Map<String, Object>> toTreeUltimate(List<Map<String, Object>> data) {
        return toTreeUltimate(data, "id", "parentId", false);
    }

    public static List<Map<String, Object>> toTreeUltimate(List<Map<String, Object>> data, boolean showKey) {
        return toTreeUltimate(data, "id", "parentId", showKey);
    }

    public static List<Map<String, Object>> toTreeUltimate(List<Map<String, Object>> data, String id, String parentId) {
        return toTreeUltimate(data, id, parentId, false);
    }

    private static int setTreeNode(TreeMap<Object, List<Map<String, Object>>> treeMap, Map<Object, Integer> levelMap, Object start, int i, String id) {
        if (treeMap != null && treeMap.size() > 0) {
            //从父节点0开始转
            List<Map<String, Object>> maps = treeMap.get(start);
            if (maps != null && maps.size() > 0) {
//                if(i>0){
//                    levelMap.put(start, i);
//                }
                for (Map<String, Object> objectMap : maps) {
                    levelMap.put(objectMap.get(id), i);
                    List<Map<String, Object>> o = treeMap.get(objectMap.get(id));
//                    setTreeNodeShowKey(treeMap,levelMap,objectMap.get(id),i+1,id);
//                    if (o != null && o.size() > 0) {
//                        AtomicInteger ii = new AtomicInteger(i + 1);
//                        int andDecrement = ii.getAndDecrement();
//                        System.out.println(objectMap.get(id)+"-->"+andDecrement);
//                        levelMap.put(objectMap.get(id),andDecrement);
//                        o.forEach(objectMap2 -> {
//                            //System.out.println(objectMap.get("name")+(i+"")+"-->"+objectMap2.get("name")+andDecrement);
//                            //objectMap2.put("level", andDecrement);
//                            //objectMap.put("key", objectMap2.get(id));
//                            //levelMap.put(objectMap2.get(id),andDecrement + 1);
//                            //setTreeNodeShowKey(treeMap,levelMap, objectMap2.get(id), andDecrement + 1, id);
//                        });
//                        return andDecrement + 1;
//                    } else {
//                        return i;
//                    }
                }
                for (Map<String, Object> objectMap : maps) {
                    //List<Map<String, Object>> o = treeMap.get(objectMap.get(id));
                    setTreeNode(treeMap, levelMap, objectMap.get(id), i + 1, id);
                }

                //);
//                maps.forEach(objectMap -> {
//
//                });
            }
        }
        return i;
    }

//    private static int setTreeNode(TreeMap<Object, List<Map<String, Object>>> treeMap, Object start, int i, String id) {
//        if (treeMap != null && treeMap.size() > 0) {
//            //从父节点0开始转
//            List<Map<String, Object>> maps = treeMap.get(start);
//            if (maps != null && maps.size() > 0) {
//                for (Map<String, Object> objectMap : maps) {
//                    objectMap.put("level", i);
//                    List<Map<String, Object>> o = treeMap.get(objectMap.get(id));
//                    if (o != null && o.size() > 0) {
//                        AtomicInteger ii = new AtomicInteger(i + 1);
//                        int andDecrement = ii.getAndDecrement();
//                        o.forEach(objectMap2 -> {
//                            //System.out.println(objectMap.get("name")+(i+"")+"-->"+objectMap2.get("name")+andDecrement);
//                            objectMap2.put("level", andDecrement);
//                            setTreeNode(treeMap, objectMap2.get(id), andDecrement + 1, id);
//                        });
//                        return andDecrement + 1;
//                    } else {
//                        return i;
//                    }
//                }//);
//            }
//        }
//        return i;
//    }

    public static List<Map<String, Object>> toTreeUltimate(List<Map<String, Object>> data, String id, String parentId, boolean showKey) {
        TreeMap<Object, List<Map<String, Object>>> treeMap = data.stream().collect(Collectors.groupingBy(o -> o.get(parentId), TreeMap::new, Collectors.toList()));
        List<Map<String, Object>> all = new ArrayList<>();
        AtomicInteger i = new AtomicInteger();
        Map<Object, Integer> levelMap = new HashMap<>();
        //获取数据ID的类型
        Object start = data.get(0).get(id).getClass() == Long.class ? 0L : "0";
        //levelMap.put(0L,0);
        //获取层级
        setTreeNode(treeMap, levelMap, start, i.intValue(), id);
        //获取最大层级
        Optional<Map.Entry<Object, Integer>> first = levelMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).findFirst();
        int level = first.get().getValue();
        if (showKey) {
            treeMap.forEach((o, maps1) -> {
                maps1.forEach(map -> {
                    //System.out.println(levelMap.get(map.get(id))+"-->"+);
                    map.put("level", levelMap.get(map.get(id)));
                    map.put("key", map.get(id));
                    map.put("isLeaf", true);
                });
                all.addAll(maps1);
            });
        } else {
            treeMap.forEach((o, maps1) -> {
                maps1.forEach(map -> {
                    map.put("level", levelMap.get(map.get(id)));
                    map.put("isLeaf", true);
                });
                all.addAll(maps1);
            });
        }
//        System.out.println(level);

        return toTree(all, id, parentId, "level", level, 0);

//        if(showKey){
//            setTreeNode(treeMap,0L,i.intValue(),id,showKey);
//            xxx.forEach((o, maps1) -> {
//                int andIncrement = i.getAndIncrement();
//                maps1.forEach(map->{
//                    map.put("level",andIncrement);
//                    map.put("key",map.get(id));
//                });
//                all.addAll(maps1);
//            });
//        }else{
//            treeMap.forEach((o, maps1) -> {
//                int andIncrement = i.getAndIncrement();
//                maps1.forEach(map->{
//                    map.put("level",andIncrement);
//                });
//                all.addAll(maps1);
//            });
//        }


    }


    @Data
    @NoArgsConstructor
    static class TreePlusModel extends BaseModel {
        long parentId;
        @JsonProperty("title")
        String name;

        public TreePlusModel(long id, long parentId, String name) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TreePlusModel2 extends SupperClass {
        String id;
        String parentId;
        @JsonProperty("title")
        String name;
    }

    public static void main(String[] args) {
        List<TreePlusModel> modelList = new ArrayList<>();
        modelList.add(new TreePlusModel(1L, 0L, "大屏"));
//        modelList.add(new TreePlusModel(2L, 1L, "大屏1"));
//        modelList.add(new TreePlusModel(3L, 1L, "大屏2"));
//        modelList.add(new TreePlusModel(4L, 1L, "大屏3"));
//        modelList.add(new TreePlusModel(5L, 0L, "系统设置"));
//        modelList.add(new TreePlusModel(6L, 14L, "功能1"));
//        modelList.add(new TreePlusModel(7L, 14L, "功能2"));
//        modelList.add(new TreePlusModel(8L, 14L, "功能3"));
//        modelList.add(new TreePlusModel(9L, 5L, "报表"));
//        modelList.add(new TreePlusModel(10L, 9L, "报表1"));
//        modelList.add(new TreePlusModel(11L, 9L, "报表2"));
//        modelList.add(new TreePlusModel(12L, 9L, "报表3"));
//        modelList.add(new TreePlusModel(13L, 12L, "报表子层1"));
//        modelList.add(new TreePlusModel(14L, 5L, "功能"));

        List<Map<String, Object>> maps = toTreePlus(modelList, true);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }

//        List<TreePlusModel2> model2List = new ArrayList<>();
//        model2List.add(new TreePlusModel2("1", "0", "大屏"));
//        model2List.add(new TreePlusModel2("2", "1", "大屏1"));
//        model2List.add(new TreePlusModel2("3", "1", "大屏2"));
//        model2List.add(new TreePlusModel2("4", "1", "大屏3"));
//        model2List.add(new TreePlusModel2("5", "0", "系统设置"));
//        model2List.add(new TreePlusModel2("6", "14", "功能1"));
//        model2List.add(new TreePlusModel2("7", "14", "功能2"));
//        model2List.add(new TreePlusModel2("8", "14", "功能3"));
//        model2List.add(new TreePlusModel2("9", "5", "报表"));
//        model2List.add(new TreePlusModel2("10", "9", "报表1"));
//        model2List.add(new TreePlusModel2("11", "9", "报表2"));
//        model2List.add(new TreePlusModel2("12", "9", "报表3"));
//        model2List.add(new TreePlusModel2("13", "12", "报表子层1"));
//        model2List.add(new TreePlusModel2("14", "5", "功能"));
//
//        List<Map<String, Object>> maps2 = toTreePlus(model2List, true);
//        for (Map<String, Object> map : maps2) {
//            System.out.println(map);
//        }
        mvnDependencyTree();
    }

    /**
     * 默认三级联动(下标从0开始)
     *
     * @param data
     * @return
     */
    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data) {
        return toTree(data, "id", "parentId", "lv", 2, 0);
    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, String idName) {
        return toTree(data, idName, "parentId", "lv", 2, 0);
    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, String idName, Integer level) {
        return toTree(data, idName, "parentId", "lv", level, 0);
    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, Integer lookLevel) {
        return toTree(data, "id", "parentId", "lv", 2, lookLevel);
    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, Integer level, Integer lookLevel) {
        return toTree(data, "id", "parentId", "lv", level, lookLevel);
    }

//    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, String levelName, Integer level) {
//        return toTree(data, "id", "parentId", levelName, level, 0);
//    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, String idName, String pIdName, String levelName, Integer level) {
        return toTree(data, idName, pIdName, levelName, level, 0);
    }

//    public static List<Map<String, Object>> toLevel(List<Map<String, Object>> data,String id,String parentId,boolean showKey){
//        TreeMap<Object, List<Map<String, Object>>> xxx = data.stream().collect(Collectors.groupingBy(o -> o.get(parentId), TreeMap::new, Collectors.toList()));
//        List<Map<String, Object>> all = new ArrayList<>();
//        if(showKey){
//            xxx.forEach((o, maps1) -> {
//                maps1.forEach(map->{
//                    map.put("level",o);
//                    map.put("key",map.get(id));
//                });
//                all.addAll(maps1);
//            });
//        }else{
//            xxx.forEach((o, maps1) -> {
//                maps1.forEach(map->{
//                    map.put("level",o);
//                });
//                all.addAll(maps1);
//            });
//        }
//        return all;
//    }

    public static List<Map<String, Object>> toTree(List<Map<String, Object>> data, String idName, String pIdName, String levelName, Integer level, Integer lookLevel) {
        if (data == null || (data != null && data.size() == 0)) {
            return new ArrayList<>();
        }

//        data = toLevel(data,idName,pIdName,showKey);

        Map<Integer, List<Map<String, Object>>> supper = new HashMap();
        Map<Integer, Map<String, List<Map<String, Object>>>> supperKeyMaps = new HashMap();

        int i;
        for (i = 0; i <= level; ++i) {
            int finalI = i;
            List<Map<String, Object>> items = (List) data.stream().filter((map) -> {
                return map != null && !map.isEmpty() && M.integer(map, levelName) != null && M.integer(map, levelName).equals(finalI);
            }).collect(Collectors.toList());
            supper.put(i, items);
//            Map<String, List<Map<String, Object>>> itemKeyMaps = (Map) items.stream().collect(Collectors.groupingBy((o) -> {
//                return String.valueOf(o.get(pIdName));
//            }));
            Map<String, List<Map<String, Object>>> itemKeyMaps = toGroup(items, pIdName);
            supperKeyMaps.put(i, itemKeyMaps);
        }

        for (i = level; i > 0; --i) {
            int finalI1 = i;
            ((List<Map<String, Object>>) supper.get(i - 1)).forEach((map) -> {
                Map<String, List<Map<String, Object>>> objectListMap = supperKeyMaps.get(finalI1);

                List<Map<String, Object>> maps = objectListMap.get(String.valueOf(map.get(idName)));
                if (maps != null && maps.size() > 0) {
                    map.put("children", maps);
                    map.put("isLeaf", false);
                    if (finalI1 < 2) {
                        map.put("expanded", true);
                    }
                } else {
                    map.put("isLeaf", true);
                }
//                map.remove(levelName);
//                System.out.println(map.get(idName).getClass());
//                System.out.println(maps);

//                map.put("children", ((Map) supperKeyMaps.get(finalI1)).get(String.valueOf(map.get(idName))));
            });
        }

        return (List) supper.get(lookLevel);
    }

    public static <T> T copyProperties(Map<String, Object> map, Class clazz) {
        T obj = (T) org.springframework.beans.BeanUtils.instantiateClass(clazz);
        BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
        beanWrapper.setPropertyValues(map);
        return obj;
    }

    public static <T> T copyProperties(Map<String, Object> map, Class clazz, boolean bool) {
        if (bool) {
            T obj = (T) org.springframework.beans.BeanUtils.instantiateClass(clazz);

            try {
                for (String s : map.keySet()) {
                    PropertyDescriptor id = getPropertyDescriptor(clazz, s);
                    Class<?> parameterType = id.getWriteMethod().getParameterTypes()[0];
                    Object id1 = map.get(s);
                    Object val = id1;
//                    System.out.println("map.val="+id1.getClass());
//                    System.out.println("obj.target="+parameterType);
                    if (parameterType == Long.class && id1.getClass() == Integer.class) {
                        val = ((Integer) id1).longValue();
                    } else if (parameterType == Date.class && id1.getClass() == String.class) {
//                        String regexp1 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
//                        String regexp2 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
//                        String regexp3 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])";
                        String dateStr = String.valueOf(id1);
//                        DateFormat sdf = null;
//                        if(dateStr.matches(regexp1)){
//                            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
//                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                        }else if(dateStr.matches(regexp2)){
//                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                        }else if(dateStr.matches(regexp3)){
//                            sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        }

//                        val = sdf.parse(dateStr);
                        val = DateUtils.date(dateStr);
                        ;
//                    }else if(parameterType== LocalDateTime.class && id1.getClass()==String.class){
//                        String regexp1 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
//                        String regexp2 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
//                        String regexp3 = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])";
                        dateStr = String.valueOf(id1);
//                        DateTimeFormatter sdf = null;
//                        if(dateStr.matches(regexp1)){
//                            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
//                            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//                        }else if(dateStr.matches(regexp2)){
//                            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//                        }else if(dateStr.matches(regexp3)){
//                            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                        }
//
//                        val = LocalDateTime.parse(dateStr,sdf);

                        val = DateUtils.localDateTime(dateStr);
                    }
                    id.getWriteMethod().invoke(obj, val);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return obj;
        } else {
            return copyProperties(map, clazz);
        }

    }

    public static <T> T copyProperties(Object object, Class clazz) {
        T obj = (T) org.springframework.beans.BeanUtils.instantiateClass(clazz);
        copyProperties(object, obj);
        return obj;
    }


    public static Field[] getAllFields(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

}

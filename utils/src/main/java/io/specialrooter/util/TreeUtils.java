package io.specialrooter.util;

import java.util.*;

/**
 *
 * @Author: Ai
 * @Date: 2019/1/11 22:40
 */
@Deprecated
public class TreeUtils {

    public static List<Map<String, Object>> nodes(List<Map<String, Object>> list, String parentId) {
        return nodes(list, parentId, "id", "pid",true);
    }

    public static List<Map<String, Object>> nodes(List<Map<String, Object>> list, String parentId,boolean hideParent) {
        return nodes(list, parentId, "id", "pid",hideParent);
    }

    public static List<Map<String, Object>> nodesParentId(List<Map<String, Object>> list, String parentId) {
        return nodes(list, parentId, "id", "parentId",true);
    }

    public static List<Map<String, Object>> nodesParentId(List<Map<String, Object>> list, String parentId,boolean hideParent) {
        return nodes(list, parentId, "id", "parentId",hideParent);
    }

    public static List<Map<String, Object>> nodesByKey(List<Map<String, Object>> list, String parentId) {
        return nodes(list, parentId, "key", "pid",true);
    }

    public static List<Map<String, Object>> nodesByKey(List<Map<String, Object>> list, String parentId,boolean hideParent) {
        return nodes(list, parentId, "key", "pid",hideParent);
    }

    public static List<Map<String, Object>> nodes(List<Map<String, Object>> list, String parentId, String _id, String _pid) {
        return nodes(list, parentId, _id, _pid,true);
    }

    public static List<Map<String, Object>> nodes(List<Map<String, Object>> list, String parentId, String _id, String _pid,boolean hideParent) {
        long start = System.nanoTime();
        List<Map<String, Object>> parentList = new ArrayList();
        List<Map<String, Object>> newList = new ArrayList();

        for (Map<String, Object> map : list) {
            //Map<String, Object> mapNew = transformUpperCase(map);
            String pid = M.str(map, _pid);
            if ((parentId == null && pid == null) || (parentId != null && pid != null && parentId.equals(pid))) {
                if(hideParent) {
                    map.remove(_pid);
                }
                map.put("expanded", true);
                parentList.add(map);
            } else {
                newList.add(map);
            }
        }
        recursionChildren(parentList, newList, _id, _pid,hideParent);
        long end = System.nanoTime();
        System.out.println("tree 解析耗时：" + (end - start / 1000000 * 1000000) / 1000 + "纳秒(1毫秒=1000纳秒)");
        return parentList;
    }

    // 递归获取子节点数据
    public static void recursionChildren(List<Map<String, Object>> parentList, List<Map<String, Object>> allList, String _id, String _pid,boolean hideParent) {

        for (Map<String, Object> parentMap : parentList) {
            List<Map<String, Object>> childrenList = new ArrayList();
            ;
            List<Map<String, Object>> childrenNewList = new ArrayList();
            ;
            for (Map<String, Object> allMap : allList) {
                String pid = M.str(allMap, _pid);
                String id = M.str(parentMap, _id);
                if (pid != null && pid.equals(id)) {
                    if(hideParent) {
                        allMap.remove(_pid);
                    }
                    childrenList.add(allMap);
                } else {
                    childrenNewList.add(allMap);
                }
            }
            if (!childrenList.isEmpty()) {
                parentMap.put("children", childrenList);
                parentMap.put("isLeaf", false);
                parentMap.put("expanded", true);

                recursionChildren(childrenList, childrenNewList, _id, _pid,hideParent);
            } else {
                parentMap.put("isLeaf", true);
            }
        }
    }

    public static Map<String, Object> transformUpperCase(Map<String, Object> orgMap) {
        Map<String, Object> resultMap = new HashMap<>();

        if (orgMap == null || orgMap.isEmpty()) {
            return resultMap;
        }

        Set<String> keySet = orgMap.keySet();
        for (String key : keySet) {
            String newKey = key.toLowerCase();
            //newKey = newKey.replace("_", "");
            newKey = ZStringUtils.col2Field(newKey);
            resultMap.put(newKey, orgMap.get(key));
        }

        return resultMap;
    }
}

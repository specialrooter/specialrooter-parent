package io.specialrooter.plus.jackson;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import io.specialrooter.context.model.DictItemModel;
import io.specialrooter.context.model.SimpleDictItemModel;
import io.specialrooter.context.support.Constants;
import io.specialrooter.plus.elasticsearch.ElasticsearchTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.specialrooter.plus.elasticsearch.ElasticsearchTemplate.OBJECTMAPPER;

public class DictHelper {

    @Autowired(required = false)
    protected ElasticsearchTemplate elasticsearchTemplate;

    @Value("${io.specialrooter.dict-source:MEMORY}")
    protected String dictSource;

    public void init(List items) {
        init(items,"dictItemIndex");
    }

    public void init(List items,String pName) {
        if ("MEMORY".equals(dictSource)) {
            try {
                initMemory(items,pName);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if ("ES".equals(dictSource)) {
            initElasticsearch(items);
        }
    }

    public void initMemory(List items) throws InvocationTargetException, IllegalAccessException {
        initMemory(items,"dictItemIndex");
    }

    /**
     * 初始化所有数据字典的值
     *
     * @param items
     */
    public void initMemory(List items,String pName) throws InvocationTargetException, IllegalAccessException {
        Dictionary<String, Dictionary<String, DictItemModel>> newDict = new Hashtable<>();

        for (Object item : items) {
            DictItemModel dictItemModel = new DictItemModel();
            BeanUtils.copyProperties(item, dictItemModel);
            Object dictItemIndex = BeanUtils.getPropertyDescriptor(item.getClass(), pName).getReadMethod().invoke(item);
            dictItemModel.setDictItemIndex(Long.valueOf(String.valueOf(dictItemIndex)));
            Dictionary dict = newDict.get(dictItemModel.getDictKey());
            if (dict == null) {
                dict = new Hashtable<String, Integer>();
                newDict.put(dictItemModel.getDictKey(), dict);
            }
            dict.put(dictItemModel.getDictItemIndex(), dictItemModel);
        }
        Constants.DICT = newDict;
    }

    public void initElasticsearch(List items) {
        elasticsearchTemplate.bulk(items, Constants.DEFAULT_DICT);
    }

    public Map<String,String> getDict(String dictKey) {
        return getDict(dictKey,null,null);
    }

    public Map<String,String> getDict(String dictKey, String id, String text) {
        if ("MEMORY".equals(dictSource)) {
            return getDictMemory(dictKey);
        } else if ("ES".equals(dictSource)) {
            return getDictElasticsearch(dictKey, id, text);
        }

        return null;
    }

    public void put(String dictKey, Long id, String text){
        if ("MEMORY".equals(dictSource)) {
            setDictMemory(dictKey,id,text);
        } else if ("ES".equals(dictSource)) {
            //return getDictElasticsearch(dictKey, id, text);
        }
    }

    public void remove(String dictKey){
        if ("MEMORY".equals(dictSource)) {
            removeDictMemory(dictKey);
        } else if ("ES".equals(dictSource)) {
            //return getDictElasticsearch(dictKey, id, text);
        }
    }

    public void removeDictMemory(String dictKey){
        Dictionary dict = Constants.DICT.get(dictKey);
        if (dict != null) {
            Constants.DICT.remove(dictKey);
        }
    }

    public void setDictMemory(String dictKey,Long id,String text){
        Dictionary dict = Constants.DICT.get(dictKey);
        if (dict == null) {
            dict = new Hashtable<String, Integer>();
            Constants.DICT.put(dictKey, dict);
        }
        dict.put(id,new DictItemModel(dictKey,id,text));
    }

    public Map<String, ObjectNode> getDictForObjectNode(String dictKey) {
        return getDictForObjectNode(dictKey,null,null);
    }

    public Map<String, ObjectNode> getDictForObjectNode(String dictKey, String id, String text) {
        if ("MEMORY".equals(dictSource)) {
            return getDictMemoryForObjectNode(dictKey);
        } else if ("ES".equals(dictSource)) {
            return getDictElasticsearch(dictKey, id, text);
        }

        return null;
    }

    public List<SimpleDictItemModel> getDictForSelect(String dictKey){
        return getDictForSelect(dictKey,null,null,true);
    }

    public List<SimpleDictItemModel> getDictForSelect(String dictKey,boolean bool){
        return getDictForSelect(dictKey,null,null,bool);
    }
    public List<SimpleDictItemModel> getDictForSelect(String dictKey, String id, String text,boolean bool) {
        Map<String,String> data = null;
        if ("MEMORY".equals(dictSource)) {
            data = getDictMemory(dictKey);
        } else if ("ES".equals(dictSource)) {
            data = getDictElasticsearch(dictKey, id, text);
        }

        if(data!=null){
            List<SimpleDictItemModel> resData = new ArrayList<>();
            if(bool){
//                resData.add(SimpleDictItemModel.builder().label("请选择").value("").build());
                resData.add(new SimpleDictItemModel("请选择",""));
            }
            data.entrySet().forEach(item->{
//                resData.add(SimpleDictItemModel.builder().label(String.valueOf(item)).value(item.getKey()).build());
                resData.add(new SimpleDictItemModel(String.valueOf(item.getValue()),item.getKey()));
            });
            return resData;
        }

        return null;
    }

    public Map getDictElasticsearch(String dictKey, String id, String text) {
        return elasticsearchTemplate.dicts(Constants.DEFAULT_DICT, id, text, 0);
    }

    public Map<String,ObjectNode> getDictMemoryForObjectNode(String dictKey) {
        Dictionary<String, DictItemModel> items = Constants.DICT.get(dictKey);
        if (items != null) {
            Map<String,ObjectNode> map = new HashMap<>();
            Enumeration<DictItemModel> elements = items.elements();
            while (elements.hasMoreElements()){
                DictItemModel next = elements.nextElement();
                ObjectNode node = OBJECTMAPPER.createObjectNode();
                node.put("id", String.valueOf(next.getDictItemIndex()));
                node.put("txt", next.getDictItemValue());
                map.put(String.valueOf(next.getDictItemIndex()),node);
            }
            return map;
        }
        return null;
    }

    public Map<String,String> getDictMemory(String dictKey) {
        Dictionary<String, DictItemModel> items = Constants.DICT.get(dictKey);
        if (items != null) {
            Map<String,String> map = new HashMap<>();
            Enumeration<DictItemModel> elements = items.elements();
            while (elements.hasMoreElements()){
                DictItemModel next = elements.nextElement();
                map.put(String.valueOf(next.getDictItemIndex()),next.getDictItemValue());
            }
            return map;
        }
        return null;
    }

    public List<SimpleDictItemModel> getSimpleDictMemory(String dictKey) {
        Dictionary<String, DictItemModel> items = Constants.DICT.get(dictKey);

        if (items != null) {
            List<SimpleDictItemModel> sdm = new ArrayList<>();
            ArrayList<DictItemModel> list = Collections.list(items.elements());

            list.forEach(item -> {
                sdm.add(new SimpleDictItemModel(item.getDictItemValue(),item.getDictItemIndex()));
            });
            return sdm;
        }
        return Lists.newArrayList();
    }


    public Long getItemIndexMemory(String dictKey, String itemKey) {
        Dictionary<String, DictItemModel> items = Constants.DICT.get(dictKey);
        if (items == null) {
            return null;
        }
        DictItemModel item = items.get(itemKey);
        if (item == null) {
            return null;
        }
        return item.getDictItemIndex();
    }
}

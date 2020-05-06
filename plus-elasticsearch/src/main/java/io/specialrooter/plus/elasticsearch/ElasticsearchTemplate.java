package io.specialrooter.plus.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ai
 */
@Slf4j
public class ElasticsearchTemplate {
    @Autowired
    RestHighLevelClient restHighLevelClient;


    private static final String KEYWORDS_INDEX_NOT_FOUND_EXCEPTION = "index_not_found_exception";
    /**
     * @param from
     * @param size
     * @param orderMap
     * @param orderBySort
     * @return Page
     * @Description TODO
     * @author shen.liang
     * @since 2019/8/17 9:47
     **/
    public Page queryAll(int from, int size, LinkedHashMap<String, SortOrder> orderMap, boolean orderBySort, String index, Class entityClass) {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        return search(queryBuilder, from, size, orderMap, orderBySort, index, entityClass);
    }


    /**
     * @param entity
     * @Description TODO 写入
     * @author shen.liang
     * @since 2019/8/16 13:49
     **/
    public boolean save(Object entity, Serializable id, String index){
        try {
            String source = JSON.toJSONString(entity);
            IndexRequest indexRequest = new IndexRequest(index).setRefreshPolicy("true").source(source, XContentType.JSON);
            if(id!=null){
                indexRequest.id(String.valueOf(id));
            }
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(response.getResult());
            log.debug("es 结果 :", response.getResult());
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 写入 失败！", e);
        }
        return false;
    }

    /**
     * 批量写入
     * @param list
     * @param index
     * @return
     */
    public boolean bulk(List list,String index){
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (Object o : list) {
                Object id = BeanUtils.getPropertyDescriptor(o.getClass(), "id").getReadMethod().invoke(o);
                String source = JSON.toJSONString(o);
                IndexRequest indexRequest = new IndexRequest(index).source(source, XContentType.JSON);
                if(id!=null){
                    indexRequest.id(String.valueOf(id));
                }
                bulkRequest.add(indexRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.debug("bulk es 结果 :", response.status());
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 批量写入 失败！", e);
        }
        return false;
    }

    /**
     * @param id
     * @Description TODO 根据id删除
     * @author shen.liang
     * @since 2019/8/16 16:59
     **/
    public boolean deleteById(Serializable id, String index)  {
        try {
            DeleteRequest deleteRequest = new DeleteRequest().index(index).id(String.valueOf(id));
            DeleteResponse res = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.debug("删除动作执行状态：" + res.status());
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 删除 失败！", e);
        }
        return false;
    }

    public boolean deleteByIndex(String index){
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            log.debug("删除索引动作执行状态：" + response.toString());
            return true;
        } catch (Exception e) {
            log.error("Elasticsearch 删除“"+index+"”索引失败！", e);
        }
        return false;
    }

    /**
     * @param queryBuilder
     * @param from
     * @param size
     * @param orderMap     为了保证排序字段的顺序，用有序的LinkHashMap
     * @param orderBySort  是否最后以积分倒序排列
     * @param index        这里是单index查询，如果要
     * @Description TODO 用mysql思维，构造的es查询及排序
     * @author shen.liang
     * @since 2019/8/17 8:47
     **/
    protected Page search(QueryBuilder queryBuilder, int from, int size,
                                 LinkedHashMap<String, SortOrder> orderMap, boolean orderBySort
            , String index, Class<?> entityClass) {
        try {
            final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.from(from);
            sourceBuilder.size(size);
            if (orderMap != null) {
                orderMap.forEach((k, v) -> {
                    sourceBuilder.sort(SortBuilders.fieldSort(k).order(v));
                });
            }
            if (orderBySort) {
                sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            }

            SearchRequest rq = new SearchRequest();
            rq.indices(index);
            rq.source(sourceBuilder);
            log.debug(rq.source().toString());

            SearchResponse response = restHighLevelClient.search(rq, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            Page page = new Page<>();
            long total = hits.getTotalHits().value;
            page.setTotal(total);
            page.setSize(size);
            int resultSize = response.getHits().getHits().length;
            if (resultSize == 0) {
                page.setRecords(new ArrayList(1));
                return page;
            }
            List list = new ArrayList<>(response.getHits().getHits().length);
            page.setRecords(list);

            for (int i = 0; i < resultSize; i++) {
                Object entity = JSON.parseObject(response.getHits().getAt(i).getSourceAsString(), (Type) entityClass);
                log.debug("第" + (i + 1) + "条记录：" + entity);
                list.add(entity);
            }
            return page;
        } catch (Exception e) {
            log.error("elasticsearch 查询失败 : ", e);
            return new Page();
        }
    }


    /**
     * 批量删除
     * @param idList
     * @param index
     * @return
     * @throws IOException
     */
    public boolean deleteBatchIds(Collection<? extends Serializable> idList,String index)  {

        try{
            BulkRequest bulkDeleteRequest = new BulkRequest();
            for (Serializable id : idList) {
                DeleteRequest deleteRequest = new DeleteRequest().index(index).id(String.valueOf(id));
                bulkDeleteRequest.add(deleteRequest);
            }
            //bulkDeleteRequest
            //bulkDeleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            BulkResponse bulkDeleteResponse = restHighLevelClient.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
            log.debug(JSON.toJSONString(bulkDeleteResponse));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * @param id
     * @return T
     * @Description 根据id查Elasticsearch对应的index
     * @author shen.liang
     * @since 2019/8/16 13:24
     **/
    public <T> T selectById(Serializable id, String index, Class<T> entityClass) {
        Assert.notNull(id, "Cannot find entity with id 'null'.");
        try {
            GetRequest getRequest = new GetRequest().index(index).id(String.valueOf(id));
            GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            if (response.isExists()) {
                String source = response.getSourceAsString();
                T entity = JSON.parseObject(source, (Type) entityClass);
                return entity;
            }
        } catch (org.elasticsearch.ElasticsearchStatusException e) {
            if (e.toString().indexOf(KEYWORDS_INDEX_NOT_FOUND_EXCEPTION) != -1) {
                log.debug("Elasticsearch 根据ID查询记录失败！,未找到index : ", index);
            }
        } catch (Exception e) {
            log.error(" Elasticsearch 根据ID查询记录失败！", e);
        }
        return null;
    }

    public static ObjectMapper OBJECTMAPPER = new ObjectMapper();

    /**
     * 列表转Map字典方式，主要用于展示数据
     * @param index
     * @param fieldId
     * @param fieldText
     * @return
     * @throws IOException
     */
    public Map<String,Object> dicts(String index,String fieldId,String fieldText,int type) {
        List<Map> list = list(index);
        if(list==null){
            return new HashMap<>();
        }
        if(type==0){
            return list.stream().collect(Collectors.toMap(map -> String.valueOf(map.get(fieldId)), map -> {

                ObjectNode node = OBJECTMAPPER.createObjectNode();

                node.put("id", String.valueOf(map.get(fieldId)));
                node.put("txt", String.valueOf(map.get(fieldText)));
                return node;
            }));
        }else if(type==1){
            return list.stream().collect(Collectors.toMap(map -> String.valueOf(map.get(fieldId)), map -> {
                Map m = new HashMap<>();
                m.put("id", String.valueOf(map.get(fieldId)));
                m.put("txt", String.valueOf(map.get(fieldText)));
                return m;
            }));
        }

        return null;

    }

    /**
     * 查询所有
     * @param index 索引编号
     * @return
     * @throws IOException
     */
    public List list(String index){
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 添加 match_all 查询
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();
        //sourceBuilder.query(matchAllQueryBuilder);
        sourceBuilder.size(10000);
        // 设置超时时间
        sourceBuilder.timeout(TimeValue.timeValueSeconds(2));
        request.source(sourceBuilder);
        List<Map<String,Object>> result = null;
        try{
            result = new ArrayList<>();
            SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();

            for (SearchHit hit : hits) {
                result.add(hit.getSourceAsMap());
            }
        }catch (Exception e){
            System.out.println("未找到缓存数据.");
        }

        return result;
    }
}

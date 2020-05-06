package io.specialrooter.standard.component.service;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.specialrooter.context.util.ApiUtils;
import io.specialrooter.plus.mybatisplus.handler.IdStrategyGenerator;
import io.specialrooter.plus.mybatisplus.handler.MetaBatchSequenceData;
import io.specialrooter.standard.component.mapper.StandardMapper;
import io.specialrooter.util.Converter;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 已经合并到ServiceImpl
 * </p>
 *
 * @author Ai
 * @since 2019-07-16
 */
@Service
public class StandardServiceImpl implements IStandardService {

    protected Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private StandardMapper standardMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IdStrategyGenerator idStrategyGenerator;
    @Value("${spring.datasource.url:#{null}}")
    private String databaseURL;

    @Value("${spring.datasource.dynamic.primary:#{null}}")
    private String primary;

    private String database=null;

    @Autowired
    private Environment environment;

    @Override
    public boolean saveBatchPlus(Collection entityList, Class clazz) {
        return saveBatchPlus(entityList,clazz,primary);
    }

    @Override
    public boolean saveBatchPlus(Collection entityList, Class clazz,String datasource) {
        log.debug("进入批量SQL写入...");
        long start = System.currentTimeMillis();
        if (entityList != null && entityList.size() > 0) {
            Map map = new HashMap();

            TableInfo table = SqlHelper.table(clazz);

            List<TableFieldInfo> fieldList = table.getFieldList();

            List<String> fields = new ArrayList<>();
            // 主键为特殊字段
            fields.add("id");
            for (TableFieldInfo tableFieldInfo : fieldList) {
                fields.add(tableFieldInfo.getColumn());
            }
            findDataBaseNameByUrl(datasource);

            MetaBatchSequenceData metaBatchSequenceData = idStrategyGenerator.batchIds(clazz, entityList.size());
            long go = metaBatchSequenceData.getStart();
            Long currentUserId = ApiUtils.getCurrentUserId(0L);

            // 获取表字段默认值
            List<Map<String, Object>> fieldDefaultList = jdbcTemplate.queryForList("select COLUMN_NAME,COLUMN_DEFAULT from information_schema.COLUMNS where table_name=? and table_schema=?", table.getTableName(), database);
            Map<String, Object> fieldDefault = fieldDefaultList.stream().collect(Collectors.toMap(m -> String.valueOf(m.get("COLUMN_NAME")), m -> m.get("COLUMN_DEFAULT") == null ? "" : m.get("COLUMN_DEFAULT")));
            log.debug("进入批量SQL写入..." + (System.currentTimeMillis() - start) + " ms...前期整理完成");
            List<List<Object>> data = new ArrayList<>();
            for (Object bdFunction : entityList) {
                List<Object> item = new ArrayList<>();
                PropertyDescriptor id = BeanUtils.getPropertyDescriptor(bdFunction.getClass(), "id");
                try {
                    Object invoke = id.getReadMethod().invoke(bdFunction);
                    if (invoke == null) {
                        long nextId = go;
                        go++;
                        item.add(nextId);
                        id.getWriteMethod().invoke(bdFunction, nextId);
                    } else {
                        item.add(invoke);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                LocalDateTime now = LocalDateTime.now();
                for (TableFieldInfo tableFieldInfo : fieldList) {
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(bdFunction.getClass(), tableFieldInfo.getProperty());
                    try {
                        Object invoke = propertyDescriptor.getReadMethod().invoke(bdFunction);
                        if (invoke == null && tableFieldInfo.getInsertStrategy() == FieldStrategy.NOT_NULL) {
                            Object o = fieldDefault.get(tableFieldInfo.getColumn());
                            if (o != null && o.toString().equals("CURRENT_TIMESTAMP(3)")) {
                                item.add(now);
                                propertyDescriptor.getWriteMethod().invoke(bdFunction, now);
                            } else {
                                if (tableFieldInfo.getProperty().equals("createUserId") || tableFieldInfo.getProperty().equals("modifyUserId")) {
                                    item.add(currentUserId == null ? o : currentUserId);
                                    propertyDescriptor.getWriteMethod().invoke(bdFunction, currentUserId == null ? o : currentUserId);
                                } else if (tableFieldInfo.getProperty().equals("createTime") || tableFieldInfo.getProperty().equals("modifyTime")) {
                                    item.add(now);
                                    propertyDescriptor.getWriteMethod().invoke(bdFunction, now);
                                } else {
                                    item.add(fieldDefault.get(tableFieldInfo.getColumn()));
                                    propertyDescriptor.getWriteMethod().invoke(bdFunction, Converter.cast(o, tableFieldInfo.getPropertyType()));
                                }
//                                item.add(fieldDefault.get(tableFieldInfo.getColumn()));
                            }

//                            System.out.println(tableFieldInfo.getColumn()+"->"+fieldDefault.get(tableFieldInfo.getColumn()));
                        } else {
                            item.add(invoke);
//                            System.out.println(tableFieldInfo.getColumn()+"->"+invoke);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                data.add(item);
            }
            log.debug("进入批量SQL写入..." + (System.currentTimeMillis() - start) + " ms...数据整理完成");
            map.put("table", table.getTableName());
            map.put("fields", fields);
            map.put("list", data);
            int i = standardMapper.saveOrUpdate(map);
            log.debug("进入批量SQL写入..." + entityList.size() + "->" + (System.currentTimeMillis() - start) + " ms...数据写入完成");
            return i > 0;
        }

        return false;
    }

    public void findDataBaseNameByUrl(String datasource) {
        if(database==null) {
            String database_temp = null;
            int pos, pos1;
            String connUri;

            if (org.apache.commons.lang3.StringUtils.isBlank(databaseURL)) {
                throw new IllegalArgumentException("Invalid JDBC url.");
            }

            if(StringUtils.isNotBlank(datasource)){
                databaseURL = environment.getProperty("spring.datasource.dynamic." + datasource + ".url");
            }else if(StringUtils.isNotBlank(primary)){
                databaseURL = environment.getProperty("spring.datasource.dynamic." + primary + ".url");
            }

            databaseURL = databaseURL.toLowerCase();

            if (databaseURL.startsWith("jdbc:impala")) {
                databaseURL = databaseURL.replace(":impala", "");
            }

            if (!databaseURL.startsWith("jdbc:")
                    || (pos1 = databaseURL.indexOf(':', 5)) == -1) {
                throw new IllegalArgumentException("Invalid JDBC url.");
            }

            connUri = databaseURL.substring(pos1 + 1);

            if (connUri.startsWith("//")) {
                if ((pos = connUri.indexOf('/', 2)) != -1) {
                    database_temp = connUri.substring(pos + 1);
                }
            } else {
                database_temp = connUri;
            }

            if (database_temp.contains("?")) {
                database_temp = database_temp.substring(0, database_temp.indexOf("?"));
            }

            if (database_temp.contains(";")) {
                database_temp = database_temp.substring(0, database_temp.indexOf(";"));
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(database_temp)) {
                throw new IllegalArgumentException("Invalid JDBC url.");
            }
            this.database = database_temp;
        }
    }
}

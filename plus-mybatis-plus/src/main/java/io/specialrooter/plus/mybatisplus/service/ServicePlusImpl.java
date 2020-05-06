package io.specialrooter.plus.mybatisplus.service;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.specialrooter.context.util.ApiUtils;
import io.specialrooter.plus.jackson.DictHelper;
import io.specialrooter.plus.jackson.DictSentry;
import io.specialrooter.plus.mybatisplus.handler.IdStrategyGenerator;
import io.specialrooter.plus.mybatisplus.handler.MetaBatchSequenceData;
import io.specialrooter.standard.component.mapper.StandardMapper;
import io.specialrooter.util.Converter;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 扩展支持CURD ES、redis等缓存
 *
 * @param <M>
 * @param <T>
 * @author Ai
 * @since 3.2.0 plus
 */
public class ServicePlusImpl<M extends BaseMapper<T>, T> implements IServicePlus<T> {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;
    @Autowired
    protected DictHelper dictHelper;
    @Autowired
    protected StandardMapper standardMapper;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private IdStrategyGenerator idStrategyGenerator;
    @Value("${spring.datasource.url}")
    protected String databaseURL;
    private String database=null;

    @Override
    public M getBaseMapper() {
        return baseMapper;
    }



    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    protected String currentModelClassSimpleName() {
        return currentModelClass().getSimpleName();
    }

    protected DictSentry dictSentry(){
        return currentModelClass().getAnnotation(DictSentry.class);
    }

    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }


    protected void setDict(T entity){
        DictSentry dictSentry = dictSentry();
        if(dictSentry!=null) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                Object label = null,value = null;
                try {
                    value = BeanUtils.getPropertyDescriptor(currentModelClass(),dictSentry.value()).getReadMethod().invoke(entity);
                    label = BeanUtils.getPropertyDescriptor(currentModelClass(),dictSentry.label()).getReadMethod().invoke(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if(value!=null){
                    dictHelper.put(currentModelClassSimpleName(),Long.valueOf(String.valueOf(value)),String.valueOf(label));
                }
            });
        }
    }

    protected void removeDict(Collection<? extends Serializable> idList){
        DictSentry dictSentry = dictSentry();
        if(dictSentry!=null && idList!=null && idList.size()>0){
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                for (Serializable serializable : idList) {
                    dictHelper.remove(String.valueOf(serializable));
                }
            });
        }
    }

    @Override
    public boolean save(T entity) {
        boolean b = retBool(baseMapper.insert(entity));
        if (b) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                setDict(entity);
            });
        }
        return b;
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        int size = entityList.size();
        executeBatch(sqlSession -> {
            int i = 1;
            for (T entity : entityList) {
                sqlSession.insert(sqlStatement, entity);
                if ((i % batchSize == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        });
        return true;
    }

    public void findDataBaseNameByUrl() {
        if(database==null) {
            String database_temp = null;
            int pos, pos1;
            String connUri;

            if (org.apache.commons.lang3.StringUtils.isBlank(databaseURL)) {
                throw new IllegalArgumentException("Invalid JDBC url.");
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

    @Override
    public boolean saveBatchPlus(Collection entityList){
        return saveBatchPlus(entityList,currentModelClass());
    }
    /**
     * 批量插入SQL直写
     *
     * @param entityList ignore
     * @return ignore
     */
    @Override
    public boolean saveBatchPlus(Collection entityList, Class clazz) {
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
            findDataBaseNameByUrl();

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

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        int size = entityList.size();
        executeBatch(sqlSession -> {
            int i = 1;
            for (T entity : entityList) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, keyProperty);
                if (StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal))) {
                    sqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                } else {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
                }
                // 不知道以后会不会有人说更新失败了还要执行插入 😂😂😂
                if ((i % batchSize == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        });
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean b = SqlHelper.retBool(baseMapper.deleteById(id));
        if (b) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                removeDict(Arrays.asList(id));
            });
            //elasticsearchTemplate.deleteById(id, indexName());
        }
        return b;
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        Assert.notEmpty(columnMap, "error: columnMap must not be empty");
        boolean b = SqlHelper.retBool(baseMapper.deleteByMap(columnMap));
        if(b){
            //无法预判删除的数据，进行单个字典重新赋值
            //System.out.println("已预判的字典赋值异常，请联系架构师，未来解决方案：无法预判删除的数据，进行单个字典重新赋值");
        }
        return b;
    }

    @Override
    public boolean remove(Wrapper<T> wrapper) {
        boolean b = SqlHelper.retBool(baseMapper.delete(wrapper));
        if(b){
            //无法预判删除的数据，进行单个字典重新赋值
            //System.out.println("已预判的字典赋值异常，请联系架构师，未来解决方案：无法预判删除的数据，进行单个字典重新赋值");
        }
        return b;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return false;
        }
        boolean b = SqlHelper.retBool(baseMapper.deleteBatchIds(idList));
        if (b) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                removeDict(idList);
            });
            //elasticsearchTemplate.deleteBatchIds(idList, indexName());
        }
        return b;
    }

    @Override
    public boolean updateById(T entity) {
        boolean b = retBool(baseMapper.updateById(entity));
        if (b) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                setDict(entity);
            });
        }
        return b;
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        boolean b = retBool(baseMapper.update(entity, updateWrapper));
        if (b) {
            //异步调用不影响主线任务，主线会立即返回，异步处理成功或者失败都不会卡住线程
            CompletableFuture.runAsync(()->{
                setDict(entity);
            });
        }
        return b;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        int size = entityList.size();
        executeBatch(sqlSession -> {
            int i = 1;
            for (T anEntityList : entityList) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                sqlSession.update(sqlStatement, param);
                if ((i % batchSize == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        });
        return true;
    }

    @Override
    public T getById(Serializable id) {
        T t = null;//elasticsearchTemplate.selectById(id, indexName(), currentModelClass());

        if (t == null) {
            t = baseMapper.selectById(id);
        }
        return t;
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return baseMapper.selectBatchIds(idList);
    }

    @Override
    public List<T> listByMap(Map<String, Object> columnMap) {
        return baseMapper.selectByMap(columnMap);
    }


    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        if (throwEx) {
            return baseMapper.selectOne(queryWrapper);
        }
        return SqlHelper.getObject(log, baseMapper.selectList(queryWrapper));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, baseMapper.selectMaps(queryWrapper));
    }

    @Override
    public int count(Wrapper<T> queryWrapper) {
        return SqlHelper.retCount(baseMapper.selectCount(queryWrapper));
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        return baseMapper.selectMaps(queryWrapper);
    }

    @Override
    public <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return baseMapper.selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<T> queryWrapper) {
        return baseMapper.selectMapsPage(page, queryWrapper);
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    /**
     * 执行批量操作
     *
     * @param fun fun
     * @since 3.3.0
     */
    protected void executeBatch(Consumer<SqlSession> fun) {
        Class<T> tClass = currentModelClass();
        SqlHelper.clearCache(tClass);
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(tClass);
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            fun.accept(sqlSession);
            sqlSession.commit();
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof RuntimeException) {
                MyBatisExceptionTranslator myBatisExceptionTranslator
                        = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true);
                throw Objects.requireNonNull(myBatisExceptionTranslator.translateExceptionIfPossible((RuntimeException) unwrapped));
            }
            throw ExceptionUtils.mpe(unwrapped);
        } finally {
            sqlSession.close();
        }
    }
}

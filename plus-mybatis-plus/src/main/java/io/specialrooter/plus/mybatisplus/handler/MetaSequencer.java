package io.specialrooter.plus.mybatisplus.handler;

import io.specialrooter.context.SpringContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class MetaSequencer {

    /**
     * 静态加载的序列管理对象
     */
    private static MetaSequencer[] managers;

    static {// 初始化
        managers = new MetaSequencer[256];
        for (int i = 0; i < managers.length; i++) {
            managers[i] = new MetaSequencer(i);
        }
    }

    /**
     * 通过实体编号取得新序列值
     *
     * @param entityId 实体编号，表索引号
     * @return 新序列值
     */
    public static long nextID(int entityId) {
        return managers[entityId].nextUniqueID();
    }

    public static MetaBatchSequenceData batchIds(int entityId, long num) {
        return managers[entityId].batchUniqueID(num);
    }

    public synchronized static MetaBatchSequenceData batchIds(String entityName, long num) {
        MetaBatchSequenceData result = new MetaBatchSequenceData();
        result.setStart(-1);
        result.setEnd(-1);
        result.setNum(0);
        if (null != entityName) {
            if (entityName.length() > 0) {
                int entityId = jdbcTemplate().queryForObject(String.format("select ifnull(entity_id,0) from mt_meta where entity_name='%s'", entityName), Integer.class);
                if (entityId > 0) {
                    result = managers[entityId].batchUniqueID(num);
                }
            }
        }
        return result;
    }

    public static JdbcTemplate jdbcTemplate() {
        return SpringContext.getBean("jdbcTemplate");
    }

    public synchronized static long nextID(String entityName) {

        long result = 1L;
        if (null != entityName) {
            if (entityName.length() > 0) {
                int entityId = jdbcTemplate().queryForObject(String.format("select ifnull(entity_id,0) from mt_meta where entity_name='%s'", entityName), Integer.class);
                if (entityId > 0) {
                    result = nextID(entityId);
                }
            }
        }
        return result;
    }

    /**
     * 当前序列值
     */
    private long _currentid;
    /**
     * 序列最大值
     */
    private long _maxid;
    /**
     * 表索引号
     */
    private int _entity_id;

    /**
     * 实例化序列管理器
     *
     * @param entityId 表索引号
     */
    public MetaSequencer(int entityId) {
        this._entity_id = entityId;
        this._currentid = 1;
        this._maxid = 1;
    }

    private MetaSequencer() {

    }

    /**
     * 取得下一个可用ID
     */
    private void getNextBlock() {
        try {
            long currentSequence = jdbcTemplate().queryForObject(String.format("select ifnull(entity_pk_sequence,0) from mt_meta where entity_id=%d", this._entity_id), Long.class);
            this._currentid = currentSequence + 1;
            jdbcTemplate().update(String.format("update mt_meta set entity_pk_sequence=%d where entity_id=%d", this._currentid, this._entity_id));
        } catch (Exception e) {
            System.err.println("警告：在线程中取得ID号号失败，请重试...");
            getNextBlock();
        }
    }

    /**
     * 取得下一个可用ID
     */
    private long getNextBatch(long num) {
        long result = this._currentid;
        try {
            long currentSequence = jdbcTemplate().queryForObject(String.format("select ifnull(entity_pk_sequence,0) from mt_meta where entity_id=%d", this._entity_id), Long.class);
            this._currentid = currentSequence + num;
            jdbcTemplate().update(String.format("update mt_meta set entity_pk_sequence=%d where entity_id=%d", this._currentid, this._entity_id));
        } catch (Exception e) {
            System.err.println("警告：在线程中取得ID号号失败，请重试...");
            getNextBatch(num);
        }
        return result;
    }

    /**
     * 同步取得下一个非重复ID
     *
     * @return 取得的非重复ID
     */
    public synchronized long nextUniqueID() {
        if (!(this._currentid < this._maxid)) {
            getNextBlock();
        }
        long id = this._currentid;
        this._currentid++;
        return id;
    }

    public synchronized MetaBatchSequenceData batchUniqueID(long num) {
        MetaBatchSequenceData result = new MetaBatchSequenceData();
        result.setNum(num);
        if (!(this._currentid < this._maxid)) {
            result.setStart(getNextBatch(num));
        }
        result.setEnd(this._currentid);
        return result;
    }

}

package io.specialrooter.web.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.specialrooter.context.model.Between;
import io.specialrooter.web.test.model.TestBean;
import io.specialrooter.web.util.QueryWrapperUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class QueryParamsUtilsTest {

    public static void main(String[] args) {
        QueryParamsUtilsTest test = new QueryParamsUtilsTest();
        test.test1();
    }

    public void test1() {
        TestBean testBean = new TestBean();

        testBean.setNotIn(List.of(1L,2L,3L));
//        testBean.setString("字符串");
//        testBean.setLocalDateTimes(new LocalDateTime[]{DateUtils.todayMin(), DateUtils.todayMax()});
//        testBean.setLocalDateTimes(new LocalDateTime[]{DateUtils.todayMin()});
//        testBean.setTestEnum(TestEnum.STYLE_CODE_1);
//        List list = new ArrayList();
//        list.add("3333");
//        list.add("444");
//        testBean.setStringList(list);

//        testBean.getTestEnum().getCode();
        // 多列排序，用 Lambda 表达式，需要继承 PageRequest<范型>
//        testBean.orderBy(Sort.ASC, TestBean::getPageIndex, TestBean::getDouble1);
        // 逻辑删除，单表自动加上(不要手动添加)，多表请填写别名
//        testBean.logic("a","b");
//        testBean.setSort("name.ascend-double1.descend");

        // 非时间字段区间查询
//        BigDecimal b1 = new BigDecimal(1.5);
//        BigDecimal b2 = new BigDecimal(3.5);
//        List<BigDecimal> bigDecimals = Arrays.asList(b1, b2);
//        testBean.setBtQuery(bigDecimals.toArray(new BigDecimal[]{}));
//        testBean.setBtQuery2(bigDecimals);

//        Between<Long> longBetween = new Between(333, 666);
//        testBean.setBetween(longBetween);
//
//        Between<String> between3 = new Between<>("","88");
//        testBean.setBetween33(between3);

        Between<LocalDateTime> between4 = new Between<>(LocalDateTime.now(),LocalDateTime.now());
        testBean.setBetween2(between4);
        QueryWrapper queryWrapper = QueryWrapperUtils.queryWrapper(testBean,false);
        queryWrapper.apply("spu.xxx = select *from sku xxxx={0} {1}",8888,9999);
        log.info(queryWrapper.getCustomSqlSegment());
        log.info(queryWrapper.getSqlSegment());
//        log.info(queryWrapper.getParamNameValuePairs().toString());
//        IPage<TestBean> page = QueryParamsUtils.page(null, null);
    }
}

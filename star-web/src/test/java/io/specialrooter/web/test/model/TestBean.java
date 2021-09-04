package io.specialrooter.web.test.model;

import io.specialrooter.context.annotation.Search;
import io.specialrooter.context.annotation.SearchOption;
import io.specialrooter.context.model.Between;
import io.specialrooter.web.request.PageRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestBean extends PageRequest {

    @Search(tableAlias = "a", option = SearchOption.EQ,columnAlias = "9999999",query = false)
    private String string;

    private String[] strings = new String[2];
    private List<String> stringList;
    private Long long1;
    //    private long long2;
    private Double double1;
    //    private double double2;
    private LocalDateTime localDateTime;
    @Search(tableAlias = "a", option = SearchOption.GT)
    private LocalDateTime[] localDateTimes;
    @Search(tableAlias = "timi")
    private TestEnum testEnum;

    @Search(option = SearchOption.BT)
    private BigDecimal[] btQuery;

    @Search(option = SearchOption.BT)
    private List<BigDecimal> btQuery2;

    @Search(start = "a.price1",end = "a.price2")
    private Between<Long> between;

    // 区间查询条件，不同字段
    // and ((a.time1 >= min and a.time1 <= max) or (a.time2 >= min and a.time2 <= max))
    @Search(start = "a.time1",end = "a.time2")
    private Between<LocalDateTime> between2;

    // 区间查询条件:默认
    // and between3 >=min and between3 <=max
    private Between<String> between3;

    // 区间查询条件：字段别名
    //and bt33 >=min and bt33 <=max
    @Search(columnAlias = "bt33")
    private Between<String> between33;

    @Search(option = SearchOption.NI)
    private List<Long> notIn;

//    and((b.field2>=1 and b.field2<=6) or (b.field1>=1 and b.field1<=6))
//
//    and b.field2>=1 and b.field2<=6
//
//    最低价 最高价
//      1      6

}

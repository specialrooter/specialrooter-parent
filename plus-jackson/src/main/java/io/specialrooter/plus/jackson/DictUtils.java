package io.specialrooter.plus.jackson;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.specialrooter.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DictUtils {
    private Map<Class, List<DictM>> dictTranslation = new HashMap<>();
    @Autowired
    private DictHelper dictHelper;
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;
    private static DictUtils dictUtils;

    @PostConstruct
    public void init() {
        dictUtils = this;
        dictUtils.dictHelper = this.dictHelper;
    }

    public static void exportExcel(List list, Class clazz, String fileName, String sheetName, HttpServletResponse response) throws IOException {
        exportExcel(list, clazz, fileName, sheetName, response,"xlsx");
    }

    public static void exportExcel(List list, Class clazz, String fileName, String sheetName, HttpServletResponse response, String fileType) throws IOException {
        // 翻译数据
        List translate = translate(list, clazz);

        // 组装Excel标题及取值映射列
        List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
        Field[] allField = dictUtils.getAllField(clazz);
        for (Field field : allField) {
            Excel annotation = field.getAnnotation(Excel.class);
            if(annotation!=null){
                ExcelExportEntity excelExportEntity = new ExcelExportEntity(annotation.name(), field.getName());
                excelExportEntity.setWidth(annotation.width());
                entity.add(excelExportEntity);
            }
        }
        // 装填Excel
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(fileName, sheetName, ExcelType.XSSF), entity, translate);
        // 导出Excel
        ExcelUtils.workbookExport(workbook, fileName, response, fileType);
    }

    public static List translate(List list, Class clazz) throws IOException {
        Map<Object, Map> dictFilterMap = new HashMap<>();
        Map<Object, Dict.Result> dictFilterTypeMap = new HashMap<>();

        dictUtils.getAllFieldDict(clazz);
        List<DictM> dictMS = dictUtils.dictTranslation.get(clazz);
        //获取所有字典项
        dictMS.forEach(x -> {
            //System.out.println(x.dict()+"|"+x.value());
            Map<String, ObjectNode> dicts = dictUtils.dictHelper.getDictForObjectNode(x.getDict(), null, null);
            //Map<String, Object> dicts = elasticsearchTemplate.dicts(x.dict(), x.id(), x.text(), 0);
            if (dicts != null) {
                dictFilterMap.put(x.getField(), dicts);
                dictFilterTypeMap.put(x.getField(), x.res);
            }
        });


        ObjectMapper objectMapper = new ObjectMapper();

        //处理时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dictUtils.pattern)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dictUtils.pattern)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        objectMapper.registerModule(javaTimeModule);

        JsonNode at = objectMapper.readTree(objectMapper.writeValueAsString(list));

        //JsonNode at = jsonNode.at(path);
        if (at != null) {
            //解析List数组
            if (at.isArray()) {
                Iterator<JsonNode> it = at.iterator();
                while (it.hasNext()) {
                    dictUtils.setDictData((ObjectNode) it.next(), dictFilterMap, dictFilterTypeMap);
                }
            }
            //解析对象
            if (at.isObject()) {
                dictUtils.setDictData((ObjectNode) at, dictFilterMap, dictFilterTypeMap);
            }
        }

        //List list1 = JSONArray.parseArray(at.toString(), clazz);
        return objectMapper.readValue(at.toString(), list.getClass());
    }

    public void getAllFieldDict(Class clazz) {
        if (dictTranslation.get(clazz) != null) {
            return;
        }
        List<DictM> dictMList = new ArrayList<>();
        Field[] declaredFields = getAllField(clazz);
        for (Field declaredField : declaredFields) {
            Dict dictAnnotation = declaredField.getAnnotation(Dict.class);
            if (dictAnnotation != null) {
                DictM dictM = DictM.builder().field(declaredField.getName()).leaf(true).build();
                dictM.setDict(dictAnnotation.value());
                dictM.setRes(dictAnnotation.res());
                if (StringUtils.isEmpty(dictAnnotation.mapper())) {
                    dictM.setMapper(dictAnnotation.mapper());
                }
                dictMList.add(dictM);
            }
        }
        dictTranslation.put(clazz, dictMList);
    }

    public Field[] getAllField(Class clazz) {
        //Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public void setDictData(ObjectNode node, Map<Object, Map> dictFilterMap, Map<Object, Dict.Result> dictFilterTypeMap) {
        for (Map.Entry<Object, Map> dm : dictFilterMap.entrySet()) {
            JsonNode jsonNode = node.get(String.valueOf(dm.getKey()));
            String s = jsonNode == null ? null : jsonNode.asText();
            Dict.Result result = dictFilterTypeMap.get(dm.getKey());
            if (StringUtils.isNotEmpty(s)) {
                ObjectNode o = (ObjectNode) dm.getValue().get(s);

                if (o != null) {
                    node.put(String.valueOf(dm.getKey()), o.get("txt").textValue());
                } else {
                    node.put(String.valueOf(dm.getKey()), (String) null);
                }
            } else {
                node.put(String.valueOf(dm.getKey()), (String) null);
            }
        }
    }
}

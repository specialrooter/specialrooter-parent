package io.specialrooter.plus.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class CodeGenerator {

    //    @Autowired
//    private GeneratorProps generatorProps;
    @Value("${spring.datasource.url:#{null}}")
    private String url;
    @Value("${spring.datasource.username:#{null}}")
    private String username;
    @Value("${spring.datasource.password:#{null}}")
    private String password;
    @Value("${spring.datasource.driver-class-name:#{null}}")
    private String driver;
    @Value("${spring.generator.parent}")
    private String parent;
    @Value("${spring.generator.module}")
    private String module;
    @Value("#{'${spring.generator.excludeTable:}'.split(',')}")
    private String[] excludeTable;
    @Autowired
    private Environment environment;
    @Value("${spring.datasource.dynamic.primary:#{null}}")
    private String primary;

    public void run(String tableName, String author, String datasource) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");// 生成文件的输出目录,默认D根目录
        gc.setFileOverride(true); // 是否覆盖已有文件
        gc.setAuthor(author);
        gc.setOpen(false);
        gc.setSwagger2(true);// 实体属性 Swagger2 注解
//        gc.setEntityName("%sVO");
        mpg.setGlobalConfig(gc);


        // 数据源配置
        // 添加多数据源支持
        DataSourceConfig dsc = new DataSourceConfig();

        // 单数据源
        if (url != null) {
            dsc.setUrl(url);
            // dsc.setSchemaName("public");
            dsc.setDriverName(driver);
            dsc.setUsername(username);
            dsc.setPassword(password);
            mpg.setDataSource(dsc);
        } else {
            if (StringUtils.isNotBlank(datasource)) {
                primary = datasource;
            }
            dsc.setUrl(environment.getProperty("spring.datasource.dynamic." + primary + ".url"));
            // dsc.setSchemaName("public");
            dsc.setDriverName(environment.getProperty("spring.datasource.dynamic." + primary + ".driver-class-name"));
            dsc.setUsername(environment.getProperty("spring.datasource.dynamic." + primary + ".username"));
            dsc.setPassword(environment.getProperty("spring.datasource.dynamic." + primary + ".password"));
            mpg.setDataSource(dsc);
        }


        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(module/*scanner("模块名")*/);
        pc.setParent(parent);
        pc.setEntity("model.entity");

        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
                Map<String, Object> map = new HashMap<>();
                map.put("ModelVO", pc.getParent() + ".model.vo");
                map.put("ModelDTO", pc.getParent() + ".model.dto");
                map.put("moduleName", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, pc.getModuleName()));
                this.setMap(map);
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName().replace(".", "/")
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        //VO
        String entityVO = "/templates/entityVO.java.ftl";
        focList.add(new FileOutConfig(entityVO) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/" + "/" + pc.getParent().replace(".", "/")
                        + "/model/vo/" + tableInfo.getEntityName() + "VO" + StringPool.DOT_JAVA;
            }
        });

        //DTO
        String entityDTO = "/templates/entityDTO.java.ftl";
        focList.add(new FileOutConfig(entityDTO) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/java/" + "/" + pc.getParent().replace(".", "/")
                        + "/model/dto/" + tableInfo.getEntityName() + "DTO" + StringPool.DOT_JAVA;
            }
        });

        /*String moduleNameHyphen = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, pc.getModuleName());
        //angular ng alain UI for page curd component ts
        String pageCurd = "/templates/component/page-curd.component.ts.ftl";
        focList.add(new FileOutConfig(pageCurd) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, tableInfo.getEntityName());
                return projectPath + "/app/routes" +"/"+moduleNameHyphen+"/"+to
                        + "/" + to +".component.ts";
            }
        });

        //angular ng alain UI for page curd component html
        String pageCurdHtml = "/templates/component/page-curd.component.html.ftl";
        focList.add(new FileOutConfig(pageCurdHtml) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, tableInfo.getEntityName());
                return projectPath + "/app/routes" +"/"+moduleNameHyphen+"/"+to
                        + "/" + to +".component.html";
            }
        });

        //angular ng alain UI for page curd edit component ts
        String pageCurdEdit = "/templates/component/edit/edit.component.ts.ftl";
        focList.add(new FileOutConfig(pageCurdEdit) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, tableInfo.getEntityName());
                return projectPath + "/app/routes" +"/"+moduleNameHyphen+"/"+to
                        + "/edit/edit.component.ts";
            }
        });

        //angular ng alain UI for page curd edit component html
        String pageCurdEditHtml = "/templates/component/edit/edit.component.html.ftl";
        focList.add(new FileOutConfig(pageCurdEditHtml) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, tableInfo.getEntityName());
                return projectPath + "/app/routes" +"/"+moduleNameHyphen+"/"+to
                        + "/edit/edit.component.html";
            }
        });*/



        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录");
                return false;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        List<TableFill> tableFillList = getTableFills();
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel); // 数据库表映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel); // 数据库表字段映射到实体的命名策略, 未指定按照 naming 执行
//        strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
        strategy.setEntityLombokModel(true);

        strategy.setRestControllerStyle(true);
//        strategy.setTablePrefix("sys_"); // 去除前缀
        // 公共父类
        strategy.setSuperEntityClass("io.specialrooter.plus.mybatisplus.model.StandardModel");
        strategy.setSuperControllerClass("io.specialrooter.web.BaseController");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id", "create_user_id", "modify_user_id", "create_time", "modify_time", "sort_id", "state_deleted", "state_paused", "state_locked");
        strategy.setTableFillList(tableFillList);
        if (StringUtils.checkValNotNull(tableName)) {
            strategy.setInclude(tableName/*scanner("表名，多个英文逗号分割").split(",")*/);
        } else {
            strategy.setExclude(excludeTable);
        }
        strategy.setControllerMappingHyphenStyle(false);
//        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    /**
     * 获取TableFill策略
     *
     * @return
     */
    protected List<TableFill> getTableFills() {
        // 自定义需要填充的字段
        List<TableFill> tableFillList = new ArrayList<>();
        tableFillList.add(new TableFill("create_time", FieldFill.INSERT));
        tableFillList.add(new TableFill("modify_time", FieldFill.INSERT_UPDATE));
        tableFillList.add(new TableFill("create_user_id", FieldFill.INSERT));
        tableFillList.add(new TableFill("modify_user_id", FieldFill.INSERT_UPDATE));
        return tableFillList;
    }
}

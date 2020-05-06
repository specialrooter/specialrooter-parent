#Technical Middle-ground System
在线帮助文档：[技术中台体系](http://www.baidu.com) .  
`v5.2.1`  
计划：  
1.`ExcelUtils`工具类，支持动态模板，不需要编码级别的代码，目标实现零配置代码导出Excel。  
2.`OkHttpUtils`，支持更多返回类型。  
3.`ResultReponse`，支持错误代码集成。  
4.`DataUtils`,支持List->Tree、Map<->Object、List<Object><->List<Map>。

`v5.2.0`  
Scaffold：  
1.升级`mybatis-plus` ~~3.2.0~~ **3.3.0  
2.升级`mybatis-plus` ~~3.2.0~~ **3.3.0  

Bug Fixes：  

Features:  
1.增加`easypoi`支持，添加`ExcelUtils`工具类，实现一句代码生成代码。  
2.增加`elasticsearch 7`提供 ElasticsearchTemplate，注解开启`@EnableElasticsearchPlus`。  
3.增加`jackson`返回值Dict支持翻译键值对,使用注解开启`@EnableJacksonPlus`。  
4.增加`mybatisplus`普通查询也支持大小写转换，使用注解开启`@EnableMybatisPlusUltimate`。  
5.增加`okhttp`,实现`OkHttpUtils`，支持异步远程请求调用。

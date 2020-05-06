#Technical Middle-ground System
在线帮助文档：[技术中台体系](http://www.baidu.com) .  
`v5.2.1`  
模块及调用样例：  
1.`core`核心模块  
* converter 实体对象转换适配器
* plus
  + `@EnableElasticsearchPlus` 基于`elasticsearch 7.2.0`(可插拔)，新增`ElasticsearchTemplate`简化CURD操作。  
  ```java

```
  + `@EnableJacksonPlus` 基于`spring jackson`(可插拔)，新增Dict、扩展Json
  + `@EnableMybatisPlusUltimate` 基于`Mybatis-plus 3.x`(可插拔)，新增Map属性返回支持驼峰命名法
  + `@EnableOkHttpPlus` 基于`okhttp 3.x`(可插拔)，新增`OkHttpUtils`简化http交互。
  + `SpringContext` 基于`spring ApplicationContext`(工具类)，简化获取spring实例化的bean。
* doing……  


2.`utils`  
  有如下3点：  
  1. 无序  
  2. 有序  
  3. 缩进 
3.`bean`。  
4.`DataUtils`。
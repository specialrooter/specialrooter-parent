package ${package.Controller};

import org.springframework.web.bind.annotation.RequestMapping;
import ${package.Entity}.${table.entityName};
import ${cfg.ModelDTO}.${table.entityName}DTO;
import ${cfg.ModelQTO}.${table.entityName}QTO;
import ${package.Service}.${table.serviceName};
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
import io.specialrooter.message.MessageResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.specialrooter.plus.mybatisplus.model.BaseModel;
import io.specialrooter.web.util.QueryWrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import ${superControllerClassPackage};
import java.util.List;
</#if>

/**
*
* @author ${author}
* @since ${date}
*/
@Api(tags = "${table.comment!}")
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName?replace('.','/')}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
    class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass}<${table.entityName}> {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    private I${table.entityName}Service i${table.entityName}Service;

    @ApiOperation(value = "查询${table.comment!}")
    @PostMapping("/list/page")
    public MessageResponse<IPage<${table.entityName}>> list(@RequestBody ${table.entityName}QTO <#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>QTO) {
        QueryWrapper queryWrapper = QueryWrapperUtils.queryWrapper(<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>QTO);
        IPage<${table.entityName}> page = i${table.entityName}Service.page(<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>QTO.getPage(),queryWrapper);
        return MessageResponse.success(page);
    }

    @ApiOperation(value = "保存${table.comment!}", notes = "新增/更新${table.comment!}信息")
    @PostMapping("/save")
    public MessageResponse<${table.entityName}> save(@RequestBody @Validated ${table.entityName}DTO <#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>DTO) {
        ${table.entityName} <#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if> = <#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>DTO.convert(${table.entityName}.class);
        boolean bool = i${table.entityName}Service.saveOrUpdate(<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>);
        if(bool){
            return MessageResponse.success(<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>);
        }else{
            return MessageResponse.error(bool);
        }
    }

    @ApiOperation(value = "删除${table.comment!}", notes = "传入多个${table.comment!}ID删除")
    @PostMapping("/deletebatch")
    public MessageResponse<Boolean> deleteBatch(@RequestBody @ApiParam("多个ID")List<Long> id) {
        boolean save = i${table.entityName}Service.removeByIds(id);
        return MessageResponse.success(save);
    }

    @ApiOperation(value = "获取${table.comment!}", notes = "根据ID获取${table.comment!}")
    @PostMapping("/get")
    public MessageResponse<${table.entityName}> get(@RequestBody BaseModel model) {
        return MessageResponse.success(i${table.entityName}Service.getById(model.getId()));
    }
}
</#if>
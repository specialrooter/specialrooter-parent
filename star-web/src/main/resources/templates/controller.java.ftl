package ${package.Controller};

import org.springframework.web.bind.annotation.RequestMapping;
import ${package.Entity}.${table.entityName};
import ${cfg.ModelDTO}.${table.entityName}DTO;
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
import io.specialrooter.message.MessageState;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.specialrooter.web.request.RequestPage;
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

    @ApiOperation(value = "分页查询")
    @PostMapping("/list/page")
    public MessageResponse<IPage<${table.entityName}>> list(@RequestBody RequestPage requestPage) {
        QueryWrapper<${table.entityName}> queryWrapper = super.queryExpress(requestPage);
        IPage<${table.entityName}> page = i${table.entityName}Service.page(requestPage.getPage(), queryWrapper);
        return MessageResponse.success(page);
    }

    @ApiOperation(value = "分页查询Map")
    @PostMapping("/list/page/map")
    public MessageResponse<IPage> listMap(@RequestBody RequestPage requestPage) {
        QueryWrapper queryWrapper = super.queryExpress(requestPage);
//        queryWrapper.select("xxx","xxx","xxx");
        IPage page = i${table.entityName}Service.pageMaps(requestPage.getPage(), queryWrapper);
        return MessageResponse.success(page);
    }

    @ApiOperation(value = "新增/更新${table.comment!}", notes = "新增/更新${table.comment!}信息")
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

    @ApiOperation(value = "删除${table.comment!}(含多个)", notes = "传入多个${table.comment!}ID删除")
    @PostMapping("/deletebatch")
    public MessageResponse<Boolean> deleteBatch(@RequestBody @ApiParam("多个ID")List<Long> id) {
        boolean save = i${table.entityName}Service.removeByIds(id);
        return MessageResponse.success(save);
    }

    @ApiOperation(value = "获取${table.comment!}", notes = "根据ID获取${table.comment!}")
    @PostMapping("/get")
    public MessageResponse<${table.entityName}> get(@RequestBody @ApiParam("单个ID")Long id) {
        return MessageResponse.success(i${table.entityName}Service.getById(id));
    }
}
</#if>
package io.specialrooter.standard.component.controller;

import io.specialrooter.message.MessageResponse;
import io.specialrooter.model.GeneratorRequestModel;
import io.specialrooter.plus.mybatisplus.generator.CodeGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ai
 */
@Api(tags = "代码生成器控制器")
@RestController
public class GeneratorController {

    @Autowired
    private CodeGenerator codeGenerator;

    /**
     * 自动生成CRUD代码
     *
     * @param model
     * @return
     */
    @ApiOperation(value = "自动生成CRUD代码", hidden = false)
    @PostMapping("gen/code")
    public MessageResponse code(@RequestBody @ApiParam(value = "代码生成参数", required = true) GeneratorRequestModel model) {
        codeGenerator.run(model.getTable(), model.getAuthor(),model.getDatasource());
        return MessageResponse.success("自动生成是否成功，请返回控制台及IDEA查看");
    }
}

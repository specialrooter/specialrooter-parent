package io.specialrooter.web.util;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

public class SwaggerConfigUtils {

    public static Docket docket(String group, String basePackage) {
        return docket(group, basePackage, null, null);
    }

    public static Docket docket(String group, String basePackage, ApiInfo apiInfo) {
        return docket(group, basePackage, apiInfo, null);
    }

    public static Docket docket(String group, String basePackage, ApiInfo apiInfo, List<Parameter> parameters) {
        Docket build = new Docket(DocumentationType.SWAGGER_2).groupName(group)
                .select().apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any()).build();
        if (apiInfo != null) {
            build.apiInfo(apiInfo);
        } else {
            build.apiInfo(apiInfo("接口平台", "赢在速度 赢在执行力", "1.0.0"));
        }

        if (parameters != null) {
            build.globalOperationParameters(parameters);
        }

        return build;
    }

    public static List<Parameter> getHeaderParams() {
        java.util.List<springfox.documentation.service.Parameter> pars = new ArrayList<springfox.documentation.service.Parameter>();
        ParameterBuilder tokenPar = new ParameterBuilder();

        tokenPar.name("appGid").description("应用全局编号").modelRef(new ModelRef("string")).parameterType("header").defaultValue("MS8yL1NMLUNUUC1XRUItQk1D").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("appToken").description("应用身份令牌").modelRef(new ModelRef("string")).parameterType("header").defaultValue("4901821f59ea13497003e8187d589c5d").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("funcGid").description("功能全局编号").modelRef(new ModelRef("string")).parameterType("header").defaultValue("MSxTTC1DVFAtV0VCLVBNQw==").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("funcToken").description("功能全局令牌").modelRef(new ModelRef("string")).parameterType("header").defaultValue("92D10CD8074DE27243DC3C64066F467D").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("userGid").description("用户全局编号").modelRef(new ModelRef("string")).parameterType("header").defaultValue("239f6199a44d11e985790050568eca1c").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("userToken").description("用户身份令牌").modelRef(new ModelRef("string")).parameterType("header").defaultValue("DEB89CAF2D9F295CD7327508D6D48BB33224CF053049573915FD4CAD956F55A1").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }

    public static List<Parameter> getHeaderToken() {
        java.util.List<springfox.documentation.service.Parameter> pars = new ArrayList<springfox.documentation.service.Parameter>();
        ParameterBuilder tokenPar = new ParameterBuilder();

        tokenPar.name("Authorization").description("Token").modelRef(new ModelRef("string")).parameterType("header").defaultValue("").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("appId").description("应用ID").modelRef(new ModelRef("string")).parameterType("header").defaultValue("").required(true).build();
        pars.add(tokenPar.build());
        tokenPar.name("tenantId").description("租户ID").modelRef(new ModelRef("string")).parameterType("header").defaultValue("").required(true).build();
        pars.add(tokenPar.build());
        tokenPar.name("latitude").description("纬度").modelRef(new ModelRef("string")).parameterType("header").defaultValue("").required(false).build();
        pars.add(tokenPar.build());
        tokenPar.name("longitude").description("经度").modelRef(new ModelRef("string")).parameterType("header").defaultValue("").required(false).build();
        pars.add(tokenPar.build());

        return pars;
    }

    public static ApiInfo apiInfo(String title, String description, String version) {
        return apiInfo(title, description, version, null);
    }

    public static ApiInfo apiInfo(String title, String description, String version, Contact contact) {
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version);
        if (contact != null) {
            apiInfoBuilder.contact(contact);
        }

        return apiInfoBuilder.build();
    }
}

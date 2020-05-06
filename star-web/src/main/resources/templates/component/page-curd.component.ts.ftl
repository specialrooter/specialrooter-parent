import { Component, OnInit } from "@angular/core";
import { _HttpClient } from "@delon/theme";
import { STColumn } from "@delon/abc";
import { SFSchema } from "@delon/form";
import { ${cfg.moduleName}${entity}EditComponent } from "./edit/edit.component";
@Component({
selector: "app-${cfg.moduleName?uncap_first}-equipment-type",
templateUrl: "./equipment-type.component.html"
})
export class ${cfg.moduleName}${entity}Component implements OnInit {
  //后台模块名
  module = "base/tdDeviceType";
  //表单页面
  addFormPage = ${cfg.moduleName}${entity}EditComponent;

  //查询条件
  searchSchema: SFSchema = {
    properties: {
      ICCPid: {
        type: "string",
        title: "ID"
      }
    }
  };
  //查询列表显示
  columns: STColumn[] = [
  <#list table.fields as field>
    { title: "${field.comment}", index: "${field.name}"<#if field.propertyType == "double">,className: "text-right"</#if>},
  </#list>
  ];

constructor() {}

ngOnInit() {}
}

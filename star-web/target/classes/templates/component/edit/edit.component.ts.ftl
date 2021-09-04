import { Component, OnInit, ViewChild } from "@angular/core";
import { NzModalRef, NzMessageService } from "ng-zorro-antd";
import { _HttpClient } from "@delon/theme";
import { SFSchema, SFUISchema } from "@delon/form";

@Component({
  selector: "app-settings-equipment-type-edit",
  templateUrl: "./edit.component.html"
})
export class SettingsEquipmentTypeEditComponent implements OnInit {
  record: any = {};
  i: any;
  schema: SFSchema = {
    properties: {
      name: { type: "string", title: "设备名称" },
      remark: {
        type: "string",
        title: "简介",
        maxLength: 255,
        ui: {
          widget: "textarea",
          autosize: { minRows: 3, maxRows: 6 }
          // grid: {
          //   span: 24
          // }
        }
      },
      lifetime: { type: "number", title: "寿命(小时)" },
      typeLabel: { type: "number", title: "设备标签", maxLength: 8 }
    },
    required: ["name", "lifetime", "typeLabel"]
  };
  ui: SFUISchema = {
    "*": {
      spanLabelFixed: 100,
      grid: { span: 24 }
    }
    // $no: {
    //   widget: "text"
    // },
    // $href: {
    //   widget: "string"
    // },
    // $remark: {
    // widget: "textarea",
    // grid: { span: 24 }
    // }
  };

  constructor(
    private modal: NzModalRef,
    private msgSrv: NzMessageService,
    public http: _HttpClient
  ) {}

  ngOnInit(): void {
    if (this.record.id != null)
      this.http
        .get(`base/tdDeviceType/get`, this.record.id)
        .subscribe(res => (this.i = res));
  }

  save(value: any) {
    this.http.post(`base/tdDeviceType/save`, value).subscribe(res => {
      this.msgSrv.success("保存成功");
      this.modal.close(true);
    });
  }

  close() {
    this.modal.destroy();
  }
}

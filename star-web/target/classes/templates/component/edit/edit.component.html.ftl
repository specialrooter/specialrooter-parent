<div class="modal-header">
  <div class="modal-title">编辑 {{ record.id }} 信息</div>
</div>
<nz-spin *ngIf="!i" class="modal-spin"></nz-spin>
<sf
  *ngIf="i"
  #sf
  mode="edit"
  [schema]="schema"
  [ui]="ui"
  [formData]="i"
  button="none"
>
  <div class="modal-footer">
    <button nz-button type="button" (click)="close()">关闭</button>
    <button
      nz-button
      type="submit"
      nzType="primary"
      (click)="save(sf.value)"
      [disabled]="!sf.valid"
      [nzLoading]="http.loading"
    >
      保存
    </button>
  </div>
</sf>

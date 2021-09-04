/**
*
*${nodes?size}
*/
var CTPApi;
(function(CTPApi){
<#list nodes as node>
    /**
    * ${node.note}
    * 请求参数格式：<#if node.paramType==0>{</#if>
    <#list node.params as params>
    *      ${params}
    </#list>
    <#if node.paramType==0>
    * }
    </#if>
    <#if node.whereConditions??>
    * 查询参数whereConditions：{
    <#list node.whereConditions as params>
    *      ${params}
    </#list>
    * }
    </#if>
    <#if node.sortKeyModes??>
    * 排序参数sortKeyModes：{
    <#list node.sortKeyModes as params>
    *      ${params}
    </#list>
    * }
    </#if>
    */
    var ${node.method} = function(params,callback,header){
        CTPApi.Header(header);
        return new CTPApi.CAjax({url:"${node.uri}",params:params}).send(callback);
    }
    CTPApi.${node.method} = ${node.method};

</#list>

var me_ = this;
me_.uri = "http://${h}";
var Server = function(uri){
me_.uri = uri;
}
CTPApi.Server = Server;

me_.auth2 = {
funcGid:"",
funcToken:""
};

var Header = function(header){
me_.auth2 = header;
}
CTPApi.Header = Header;

var UploadFile = function(file,callback){
var formdata = new FormData();
formdata.append("file", file);
formdata.append("pixel", "300*300,500*300,800*600");
formdata.append("appname", "seller");
formdata.append("token", "p:sid:d8951375ab314b9bbd894118ce07689e0486");

return new CTPApi.CAjax({url:"http://test-market.380star.com:80/upload/file/multiUpload",type:2,params:formdata}).send(callback);
}
CTPApi.UploadFile = UploadFile;

var CAjax = (function () {
function CAjax(options) {
var me = this;
me.xmlhttp = null;
me.options = {
url:null,
type:1,
params: {}
};

for(var i in options) {
me.options[i] = options[i];
}

var hasCORS = typeof XMLHttpRequest !== "undefined" && "withCredentials" in new XMLHttpRequest();
if ("undefined" != typeof XMLHttpRequest && hasCORS) {
me.xmlhttp = new XMLHttpRequest();
}
else if ("undefined" != typeof XDomainRequest) {
me.xmlhttp = new XDomainRequest();
}
else {
me.xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
}
}
CAjax.prototype.send = function (callback) {
var me = this;
me.options.url || (me.options.url = "");
var fileDownload_ = me_['auth2']?me_.auth2['fileDownload']:false;
if(fileDownload_==true){
me.xmlhttp.responseType = 'blob';
}
me.xmlhttp.onreadystatechange = function () {
if (me.xmlhttp.readyState == 4) {
var contentType_ = me.xmlhttp.getResponseHeader('content-type');
if(contentType_=="application/vnd.ms-excel;charset=UTF-8"){
if(fileDownload_==undefined){
console.log('文件下载需要传递多一个参数属性：fileDownload:true 作为校验值,否则下载文件无法打开');
}else{
var pragma = decodeURIComponent(me.xmlhttp.getResponseHeader('pragma'));
let url = window.URL.createObjectURL(new Blob([me.xmlhttp.response]));
let link = document.createElement('a');
link.style.display = 'none';
link.href = url;
link.download = pragma;
document.body.appendChild(link);
link.click();
link.remove();
callback({data:{},"success":true,"status":200,"state":0,"msg":"文件导出成功!"});
}
}else if (me.options.type==1) {
if(me.xmlhttp.responseType == 'blob'){
var reader = new FileReader();
reader.readAsText(me.xmlhttp.response, 'utf-8');
reader.onload = function (e) {
callback(JSON.parse(reader.result));
}
}else{
callback(JSON.parse(me.xmlhttp.responseText));
}
}
else if(me.options.type==2){
var res_ = JSON.parse(me.xmlhttp.responseText);
if(res_['state']=='0'){
res_['success'] = true;
res_['status'] = 200;
}else{
res_['success'] = false;

res_['status'] = 513;
}
callback(res_);
}
}
};
var url_ = me.options.url;
if(me.options.type==1){
url_ = me_.uri+ url_;
}
me.xmlhttp.open("POST", url_, true);
me.xmlhttp.withCredentials = false;
if ("setRequestHeader" in me.xmlhttp) {
if (me.options.type==1) {
me.xmlhttp.setRequestHeader("Content-type", "application/json; charset=utf-8");
me.xmlhttp.setRequestHeader('appGid',sessionStorage.getItem("appGid") );
me.xmlhttp.setRequestHeader('appToken',sessionStorage.getItem("appToken"));
me.xmlhttp.setRequestHeader('funcGid',me_.auth2['funcGid'] );
me.xmlhttp.setRequestHeader('funcToken',me_.auth2['funcToken'] );
me.xmlhttp.setRequestHeader('fileDownload',me_.auth2['fileDownload'] );
me.xmlhttp.setRequestHeader('userGid', sessionStorage.getItem("userGid"));
me.xmlhttp.setRequestHeader('userToken',sessionStorage.getItem("userToken"));
}
else {
//me.xmlhttp.setRequestHeader("Content-type", "application/octet-stream");
//me.xmlhttp.setRequestHeader('Authorization', "token " + me.options.token);
}


}
if (me.options.type==1){
if((typeof me.options.params=='string') && me.options.params.constructor == String){
me.xmlhttp.send(me.options.params);
}else{
me.xmlhttp.send(JSON.stringify(me.options.params));
}
}else if(me.options.type==2){
me.xmlhttp.send(me.options.params);
}
};
return CAjax;
}());
CTPApi.CAjax = CAjax;
})(CTPApi || (CTPApi = {}));
//package io.specialrooter.web.processor;
//
//import com.netflix.appinfo.MyDataCenterInstanceConfig;
//import io.specialrooter.web.exec.LocalGitExecutor;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.Map;
//
//public class MyDataCenterInstanceConfig2 extends MyDataCenterInstanceConfig {
//
//    @Override
//    public String getInstanceId() {
//        String name = LocalGitExecutor.name();
//
//        if(StringUtils.isNotBlank(name)){
//            return super.getInstanceId()+":"+name;
//        }
//        return super.getInstanceId();
//    }
//
//    @Override
//    public Map<String, String> getMetadataMap() {
//        Map<String, String> metadataMap = super.getMetadataMap();
//        if(metadataMap!=null){
//            String name = LocalGitExecutor.name();
//            String email = LocalGitExecutor.email();
//            if(StringUtils.isNotBlank(name)){
//                metadataMap.put("name",name);
//            }
//
//            if(StringUtils.isNotBlank(email)){
//                metadataMap.put("email", email);
//            }
//
//            if(StringUtils.isNotBlank(email)){
//                metadataMap.put("ip",getIpAddress());
//            }
//            metadataMap.put("自定义", "自定义2");
//        }
//        return metadataMap;
//    }
//}

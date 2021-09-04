package io.specialrooter.plus.mybatisplus.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class MyTenantLineHandler implements TenantLineHandler {
    @Override
    public Expression getTenantId() {

        // 安全码 转换成 运营商ID(租户)
        HttpServletRequest request = request();
        if(request!=null){
            String sc = request.getHeader("sc");
            if (StringUtils.isNotEmpty(sc)) {
                return new StringValue(sc);
            }
        }

        return new NullValue();

    }

    @Override
    public String getTenantIdColumn() {
        return "operation_id";
    }

    public HttpServletRequest request(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if(servletRequestAttributes!=null){
            return servletRequestAttributes.getRequest();
        }else{
            return null;
        }
    }

    @Override
    public boolean ignoreTable(String tableName) {
        HttpServletRequest request = request();
        if(request!=null){
            // 未登录则忽略
            String authorization = request.getHeader("Authorization");
            if(StringUtils.isEmpty(authorization)){
                return true;
            }
        }

        // 不配置@TenantScan扫描器=>则不生效
        if (TenantScanFilter.domains.size() == 0) {
            return true;
        } else {
            // 如果在匹配项内找到任意一个元素成功，则返回false
            return !TenantScanFilter.domains.stream().anyMatch((t) -> t.equalsIgnoreCase(tableName));
        }
    }
}

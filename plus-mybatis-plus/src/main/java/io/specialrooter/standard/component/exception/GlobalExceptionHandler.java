package io.specialrooter.standard.component.exception;

import brave.Span;
import brave.Tracer;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import io.specialrooter.message.MessageResponse;
import io.specialrooter.message.MessageState;
import io.specialrooter.standard.component.log.PrettyLoggersCloudHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    Tracer tracer;
//    @Autowired
//    private PrettyLoggersCloudHandler prettyLoggersCloudHandler;

    /**
     * 服务器内部异常统一返回
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public MessageResponse handleException(HttpServletRequest request, Exception ex) {

        Throwable rootCause = Throwables.getRootCause(ex);
        String message = rootCause.getMessage();
        String msg = getMassage(message);

        if (ex instanceof NoHandlerFoundException) {
            log.error("404 Exception", ex);
            return MessageResponse.error(404, "请求地址错误");
        } else if (ex instanceof HttpMessageNotReadableException) {
            Throwable rootCause1 = ExceptionUtils.getRootCause(ex);
            log.error("400 Exception", ex);
            return MessageResponse.error(400, rootCause1.getMessage());
        } else if (ex instanceof GlobalException) {
            int code = ((GlobalException) ex).getCode();
            String msg1 = ((GlobalException) ex).getMsg();
            return MessageResponse.error(code, msg1);
        }
        return MessageResponse.error(MessageState.SERVER_LOGIC_ERROR, msg);
    }

    /**
     * 自定义服务运行异常统一返回
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public MessageResponse handleExceptionCustom(GlobalException ex) {
        log.error("GlobalException::" + ex.getCode(), ex);
        return MessageResponse.error(ex.getCode(), ex.getMsg());
    }


    /**
     * 服务运行异常统一返回
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public MessageResponse handleExceptionRuntime(RuntimeException ex) {

        Throwable rootCause = Throwables.getRootCause(ex);
        String message = rootCause.getMessage();
        String msg = getMassage(message);

        log.error("500Exception", ex);
        Span span = tracer.newChild(tracer.currentSpan().context()).name("exception").start();
        span.tag("error", MessageState.FORECASTING_ERROR.reasonPhrase());
        span.tag("message", getExceptionToString(ex));
        span.tag("class","");
        span.tag("method","");
        span.finish();

        // 设置出错接口异常点类的日志等级
//        prettyLoggersCloudHandler.open(ex);

        return MessageResponse.error(MessageState.FORECASTING_ERROR, msg);
    }

    /**
     * 服务运行异常统一返回
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AuthException.class)
    @ResponseBody
    public MessageResponse handleException3(AuthException ex) {

        Throwable rootCause = Throwables.getRootCause(ex);
        String message = rootCause.getMessage();
        String msg = getMassage(message);

        ex.printStackTrace();
        log.error("204Exception", ex);
        return MessageResponse.error(204, msg);
    }

    private String getMassage(String message) {
        String msg = "";
        if (message != null && message.startsWith("Duplicate entry")) {
            msg = "数据异常，数据已存在";
        } else {
            msg = message;
        }
        return msg;
    }

    /**
     * 参数异常统一返回
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class}/*{, ConstraintViolationException.class}*/)
    @ResponseBody
    public MessageResponse validatorHandleException(Exception ex) {
        Map<String, Object> map = new HashMap<>();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();

            for (ConstraintViolation<?> item : violations) {
                String path = String.valueOf(item.getPropertyPath());
                path = path.substring(path.lastIndexOf(".") + 1);
                map.put(path, item.getMessage());
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exs = (MethodArgumentNotValidException) ex;
            map = getErrors(exs.getBindingResult());
        } else if (ex instanceof UnexpectedTypeException) {
            Throwable rootCause = Throwables.getRootCause(ex);
            map.put("数据验证匹配错误", "开发小哥哥配置错误：" + rootCause.getMessage());
        }

        String s = JSON.toJSONString(map);

        return MessageResponse.error(505, s);

//        return MessageResponse.error(map, MessageState.FORECASTING_ERROR, "参数错误").setState(513);
    }

    /**
     * 801 错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(OauthException.class)
    @ResponseBody
    public MessageResponse oauthHandleException(Exception ex, HttpServletResponse response) {
        response.setStatus(801);
        return MessageResponse.error(801, null);
//        return MessageResponse.error(ex.getMessage()).setState(801).setStatus(801);
    }

    /***
     * 错误信息收集
     * @param result
     * @return
     */
    private Map<String, Object> getErrors(BindingResult result) {
        Map<String, Object> map = new HashMap<>();
        List<FieldError> list = result.getFieldErrors();
        for (FieldError error : list) {
            map.put(error.getField(), error.getDefaultMessage());
        }
        return map;
    }

    /**
     * 将 Exception 转化为 String
     */
    public static String getExceptionToString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}

package com.company.project.common.result;

import cn.hutool.core.util.ObjectUtil;
import com.company.project.common.interceptor.ResponseResultInterceptor;
import com.company.project.common.util.RequestContextHolderUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 响应体格式处理器（主要转换逻辑都在这里）
 * @author LErry.li
 * Date: 2018-06-15
 * Time: 14:41
 */
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ResponseResult responseResultAnn = (ResponseResult) RequestContextHolderUtil.getRequest().getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);
        return ObjectUtil.isNotNull(responseResultAnn);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ResponseResult responseResultAnn = (ResponseResult) RequestContextHolderUtil.getRequest().getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);

        Class<? extends Result> resultClazz = responseResultAnn.value();

        if (resultClazz.isAssignableFrom(Result.class)) {
            if (body instanceof DefaultErrorResult) {
                DefaultErrorResult defaultErrorResult = (DefaultErrorResult) body;
                return Result.builder()
                        .code(defaultErrorResult.getCode())
                        .message(defaultErrorResult.getMessage())
                        .data(defaultErrorResult.getErrors())
                        .build();
            }

            return Result.success(body);
        }

        return body;
    }
}

package com.bestvike.ocr.util;

import com.bestvike.ocr.reflect.GenericTypeReference;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

/**
 * Created by 许崇雷 on 2018-02-23.
 */
@Component
public final class RestTemplateUtils {
    private static final String ERROR_SERVER_MSG = "外部服务发生错误:HTTP-%s";
    private static final String ERROR_NULL_MSG = "外部服务未返回任何数据";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    //发起请求并接受响应
    public static <TResponse> TResponse exchange(RestTemplate restTemplate, String url, HttpMethod method, Object body, Type responseType, Map<String, ?> uriVariables, MultiValueMap<String, String> headers) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (MapUtils.isNotEmpty(uriVariables)) {
            for (Map.Entry<String, ?> entry : uriVariables.entrySet())
                uriBuilder.queryParam(entry.getKey(), Convert.toString(entry.getValue()));
        }
        URI uri = uriBuilder.build().encode().toUri();
        GenericTypeReference<TResponse> responseTypeReference = new GenericTypeReference<>(responseType);
        ResponseEntity<TResponse> responseEntity = restTemplate.exchange(uri, method, new HttpEntity<>(body, headers), responseTypeReference);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (responseEntity.getBody() == null)
                throw new RuntimeException(ERROR_NULL_MSG);
            return responseEntity.getBody();
        }
        throw new RuntimeException(ERROR_SERVER_MSG + responseEntity.getStatusCodeValue());
    }

    public static <TResponse> TResponse post(String url, Object body, HttpHeaders headers, Class<TResponse> responseClass) {
        return exchange(REST_TEMPLATE, url, HttpMethod.POST, body, responseClass, null, headers);
    }
}

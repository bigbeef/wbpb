package com.cppba.wbpd.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: winfed
 * @create: 2019-03-12 17:39
 **/
@Slf4j
public class JsonUtils {
    // 利用静态内部类特性实现外部类的单例
    private static class ObjectMapperBuilder {
        private static final ObjectMapper mapper;

        static {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    private static ObjectMapper getMapper() {
        return ObjectMapperBuilder.mapper;
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> cls) throws Exception {
        try {
            return getMapper().readValue(jsonStr, cls);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public static <T> T mapToBean(Map map, Class<T> cls) throws Exception {
        try {
            String json = getMapper().writeValueAsString(map);
            return getMapper().readValue(json, cls);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public static String beanToJson(Object src) throws Exception {
        try {
            return getMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage());
        }
    }
}

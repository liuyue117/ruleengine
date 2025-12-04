package com.ly.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.List;

/**
 * JSON工具类
 * 提供JSON字符串与Java对象之间的转换功能
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON字符串转换为Java对象
     * @param jsonString JSON字符串
     * @param clazz Java对象的Class类型
     * @param <T> Java对象的类型参数
     * @return Java对象
     * @throws RuntimeException 如果JSON解析失败
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为Map对象
     * @param jsonString JSON字符串
     * @return Map对象
     * @throws RuntimeException 如果JSON解析失败
     */
    public static Map<String, Object> fromJsonToMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为List对象
     * @param jsonString JSON字符串
     * @param clazz List中元素的Class类型
     * @param <T> List中元素的类型参数
     * @return List对象
     * @throws RuntimeException 如果JSON解析失败
     */
    public static <T> List<T> fromJsonToList(String jsonString, Class<T> clazz) {
        try {
            // 使用TypeReference来处理泛型类型
            return objectMapper.readValue(jsonString, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将Java对象转换为JSON字符串
     * @param object Java对象
     * @return JSON字符串
     * @throws RuntimeException 如果JSON序列化失败
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败: " + e.getMessage(), e);
        }
    }
}

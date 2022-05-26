package com.colelasticsearch.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author zhoujh
 */
public class JSONUtil {

    private static Gson gson = null;

    static {
        gson = new Gson();// yyyy-MM-dd HH:mm:ss
    }

    public static synchronized Gson newInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * 从对象生成JSON字符串
     *
     * @param obj 任意对象
     */
    public static String toJson( Object obj ) {
        return gson.toJson(obj);
    }

    public static String toJsonByFastJson( Object obj ) {
        return  JSON.toJSONString(obj);
    }

    /**
     * JSON字符串转为JavaBean
     *
     * @param json JSON字符串
     */
    public static <T> T jsonToBean( String json, Class<T> cls ) {
        return gson.fromJson(json, cls);
    }
    public static <T> T jsonToBeanByFastJson( String json, Class<T> cls ) {
        return JSON.parseObject(json, cls);
    }

    /**
     * JSON字符串转为JavaBean
     *
     * @param json JSON字符串
     */
    public static <T> List<T> jsonToList( String json, Type type ) {
        return gson.fromJson(json, type);
    }

    public static <T> List<T> jsonToListByFastJson( String json, Class<T> cls){
        return JSON.parseArray(json,cls);
    }
    /**
     * JSON字符串转为JavaBean
     *
     * @param json JSON字符串
     */
    public static <T> Map<String, T> jsonToMap( String json, Type type ) {
        return gson.fromJson(json, type);
    }

}

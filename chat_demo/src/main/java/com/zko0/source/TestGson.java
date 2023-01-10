package com.zko0.source;

import com.google.gson.*;
import com.zko0.protocol.Serializer;

import java.lang.reflect.Type;

/**
 * @author duanfuqiang
 * @date 2023/1/10 15:23
 * @description
 */
public class TestGson {
    //无法转化Class类转化为json
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }


}

package com.zko0.protocol;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;

public interface Serializer {
    <T> T deserialize(Class<T> clazz,byte[] bytes);

    //序列化
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer{

        Java{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    T t = (T) objectInputStream.readObject();
                    return t;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },

        Json{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                String json=new String(bytes,StandardCharsets.UTF_8);
                return new Gson().fromJson(json,clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                String json=new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }

}

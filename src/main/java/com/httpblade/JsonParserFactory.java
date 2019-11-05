package com.httpblade;

public interface JsonParserFactory {

    <T> T fromJson(String json, Class<T> type);

    String toJson(Object obj);

}

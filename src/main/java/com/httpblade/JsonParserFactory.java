package com.httpblade;

import java.lang.reflect.Type;

public interface JsonParserFactory {

    <T> T fromJson(String json, Type type);

    String toJson(Object obj);

}

package com.httpblade;

public interface XmlParserFactory {

    <T> T fromXml(String xml, Class<T> type);

    String toXml(Object obj);

}

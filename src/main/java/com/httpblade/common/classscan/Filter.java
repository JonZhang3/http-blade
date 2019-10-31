package com.httpblade.common.classscan;

@FunctionalInterface
public interface Filter {

    boolean accept(Class<?> clazz);

}

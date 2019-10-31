package com.httpblade.common.form;

public class Field {

    private String name;
    private String value;
    private boolean encoded;

    public Field(String name, String value) {
        this(name, value, false);
    }

    public Field(String name, String value, boolean encoded) {
        this.name = name;
        this.value = value;
        this.encoded = encoded;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public boolean encoded() {
        return encoded;
    }

}

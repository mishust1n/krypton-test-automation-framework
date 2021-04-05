package io.mishustin.krypton.api;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class ApiResponse<T> {

    private static final Gson GSON = new Gson();

    public final Type type;
    public final int code;
    public final String body;

    public ApiResponse(Type type, int code, String body) {
        this.type = type;
        this.code = code;
        this.body = body;
    }

    public <Type> T getObj() {
        if (body == null) {
            return null;
        } else {
            return GSON.fromJson(body, type);
        }
    }


}

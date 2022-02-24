package com.github.requestbin.utils;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.util.HashMap;
import java.util.Map;

public class GRPCUtils {

    private GRPCUtils() throws IllegalAccessException {
        throw new IllegalAccessException("For static use only");
    }

    public static Map<String, Value> mapToStruct(Map<String, Object> thisMap) {
        Map<String, Value> builder = new HashMap<>();
        thisMap.forEach((key, value) -> {
            if (value instanceof Map) {
                builder.put(key, Value.newBuilder().setStructValue(Struct.newBuilder().putAllFields(mapToStruct((Map<String, Object>) value)).build()).build());
            } else {
                builder.put(key, Value.newBuilder().setStringValue((String) value).build());
            }
        });
        return builder;
    }

    public static Map<String, Object> structToMap(Map<String, Value> thisStruct) {
        Map<String, Object> thisMap = new HashMap<>();
        thisStruct.forEach((key, value) -> {
            if (value.hasStructValue()) {
                thisMap.put(key, structToMap(value.getStructValue().getFieldsMap()));
            } else {
                thisMap.put(key, value.getStringValue());
            }
        });
        return thisMap;
    }
}

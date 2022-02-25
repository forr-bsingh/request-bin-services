package com.github.request.bin.services;

import com.github.request.bin.data.Attribute;

import java.util.List;
import java.util.Map;

public interface SchemaService {
    List<Attribute> attributes();

    Map<String, Object> resolve(Map<String, Object> schema);
}

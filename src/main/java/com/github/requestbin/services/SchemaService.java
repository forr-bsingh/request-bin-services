package com.github.requestbin.services;

import com.github.requestbin.data.Attribute;

import java.util.List;
import java.util.Map;

public interface SchemaService {
    List<Attribute> attributes();

    Map<String, Object> resolve(Map<String, Object> schema);
}

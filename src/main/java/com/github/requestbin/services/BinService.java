package com.github.requestbin.services;

import com.github.requestbin.data.Bin;
import com.github.requestbin.data.Ops;
import com.github.requestbin.exception.DataNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BinService {
    Bin create(String name, boolean autogenerate, Map<String, Object> schema);

    String delete(String name);

    Page<Bin> list(int p, int l);

    Bin get(String name) throws DataNotFoundException;

    Page<Ops> listOps(String name, int p, int l) throws DataNotFoundException;
}

package com.github.request.bin.services;

import com.github.request.bin.data.Bin;
import com.github.request.bin.data.Ops;
import com.github.request.bin.exception.DataNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BinService {
    Bin create(String name, boolean autogenerate, Map<String, Object> schema);

    String delete(String name);

    Page<Bin> list(int p, int l);

    Bin get(String name) throws DataNotFoundException;

    Page<Ops> listOps(String name, int p, int l) throws DataNotFoundException;
}

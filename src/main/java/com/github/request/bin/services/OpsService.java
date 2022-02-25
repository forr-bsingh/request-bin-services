package com.github.request.bin.services;

import com.github.request.bin.exception.DataNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface OpsService {

    Map<String, Object> postOps(String bin, Map<String, Object> payload, Map<String, String> headers) throws DataNotFoundException;

    Map<String, Object> getOps(String bin, String identifier, Map<String, String> headers) throws DataNotFoundException;

    Page<Map<String, Object>> listOps(String bin, int p, int l, Map<String, String> headers) throws DataNotFoundException;

    void deleteOps(String bin, String identifier, Map<String, String> headers) throws DataNotFoundException;

    Map<String, Object> patchOps(String bin, String identifier, Map<String, Object> partial, Map<String, String> headers) throws DataNotFoundException;

    Map<String, Object> putOps(String bin, String identifier, Map<String, Object> payload, Map<String, String> headers) throws DataNotFoundException;
}

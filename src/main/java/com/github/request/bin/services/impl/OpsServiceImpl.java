package com.github.request.bin.services.impl;

import com.github.request.bin.repos.BinRepo;
import com.github.request.bin.utils.PagedResource;
import com.github.request.bin.exception.DataNotFoundException;
import com.github.request.bin.repos.OpsRepo;
import com.github.request.bin.services.OpsService;
import com.github.request.bin.utils.LogThis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OpsServiceImpl implements OpsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsServiceImpl.class);

    private final BinRepo binRepo;

    private final OpsRepo opsRepo;

    public OpsServiceImpl(BinRepo binRepo, OpsRepo opsRepo) {
        this.binRepo = binRepo;
        this.opsRepo = opsRepo;
    }

    @LogThis
    @Override
    public Map<String, Object> postOps(String bin, Map<String, Object> payload, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Post operation.", bin));
            }
            return opsRepo.create(bin, payload);
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }

    @LogThis
    @Override
    public Map<String, Object> getOps(String bin, String identifier, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Get operation.", bin));
            }
            if (opsRepo.exists(bin, identifier)) {
                return opsRepo.get(bin, identifier);
            }
            throw new DataNotFoundException(String.format("Record not found for id: %s", identifier));
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }

    @LogThis
    @Override
    public PagedResource<Map<String, Object>> listOps(String bin, int p, int l, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with List operation.", bin));
            }
            return new PagedResource<>(opsRepo.values(bin).stream().filter(Objects::nonNull).skip((long) p * l)
                    .limit(l).collect(Collectors.toList()), PageRequest.of(p, l), opsRepo.count(bin));
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }

    @LogThis
    @Override
    public void deleteOps(String bin, String identifier, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Delete operation.", bin));
            }
            if (opsRepo.exists(bin, identifier)) {
                opsRepo.delete(bin, identifier);
                return;
            }
            throw new DataNotFoundException(String.format("Record not found for id: %s", identifier));
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }

    @LogThis
    @Override
    public Map<String, Object> patchOps(String bin, String identifier, Map<String, Object> partial, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Patch operation.", bin));
            }
            if (opsRepo.exists(bin, identifier)) {
                Map<String, Object> existing = opsRepo.get(bin, identifier);
                if (existing != null) {
                    Map<String, Object> merged = Stream.concat(existing.entrySet().stream(), partial.entrySet().stream())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue, (oldV, newV) -> newV));
                    return opsRepo.update(bin, identifier, merged);
                }
            }
            throw new DataNotFoundException(String.format("Record not found for id: %s", identifier));
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }

    @LogThis
    @Override
    public Map<String, Object> putOps(String bin, String identifier, Map<String, Object> payload, Map<String, String> headers) throws DataNotFoundException {
        if (binRepo.exists(bin)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Put operation.", bin));
            }
            if (opsRepo.exists(bin, identifier)) {
                payload.put("id", identifier);
                return opsRepo.update(bin, identifier, payload);
            }
            throw new DataNotFoundException(String.format("Record not found for id: %s", identifier));
        }
        throw new DataNotFoundException(String.format("Bin not found by the name of: %s, Create bin first.", bin));
    }
}

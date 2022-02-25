package com.github.request.bin.services.impl;

import com.github.request.bin.data.Bin;
import com.github.request.bin.data.Ops;
import com.github.request.bin.repos.AuditRepo;
import com.github.request.bin.repos.BinRepo;
import com.github.request.bin.repos.OpsRepo;
import com.github.request.bin.services.SchemaService;
import com.github.request.bin.utils.LogThis;
import com.github.request.bin.exception.DataNotFoundException;
import com.github.request.bin.services.BinService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BinServiceImpl implements BinService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinServiceImpl.class);
    private static final UnaryOperator<String> GET_BIN_URL = name -> "/bins/" + name;
    private static final String ID_FIELD = "id";

    @Value("${bin.default.max.list}")
    private int max;

    private final SchemaService schemaService;

    private final BinRepo binRepo;

    private final OpsRepo opsRepo;

    private final AuditRepo auditRepo;

    public BinServiceImpl(SchemaService schemaService, BinRepo binRepo, OpsRepo opsRepo, AuditRepo auditRepo) {
        this.schemaService = schemaService;
        this.binRepo = binRepo;
        this.opsRepo = opsRepo;
        this.auditRepo = auditRepo;
    }

    @LogThis
    @Override
    public Bin create(String name, boolean autogenerate, Map<String, Object> schema) {
        if (StringUtils.isBlank(name) && autogenerate) {
            name = UUID.randomUUID().toString();
        }
        Bin bin = new Bin(name, GET_BIN_URL.apply(name), schema);
        if (!binRepo.exists(name)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin does not exist by the name of: %s, Creation in progress.", name));
            }
            if (!schema.isEmpty()) {
                final String finalName = name;
                IntStream.range(0, max).boxed().map(i -> schemaService.resolve(schema)).forEach(resolved -> opsRepo.create(finalName, resolved));
            }
            return binRepo.create(name, schema);
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Skipping create operation.", name));
            }
        }
        return bin;
    }

    @LogThis
    @Override
    public String delete(String name) {
        if (binRepo.exists(name)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s, Deletion in progress.", name));
            }
            binRepo.delete(name);
            auditRepo.drop(name);
            opsRepo.drop(name);
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin does not exist by the name of: %s, Skipping delete operations.", name));
            }
        }
        return name;
    }

    @LogThis
    @Override
    public Page<Bin> list(int p, int l) {
        return new PageImpl<>(binRepo.keys().stream().map(s -> new Bin(s, GET_BIN_URL.apply(s)))
                .skip((long) p * l).limit(l).collect(Collectors.toList()),
                PageRequest.of(p, l), binRepo.count());
    }

    @LogThis
    @Override
    public Bin get(String name) throws DataNotFoundException {
        if (binRepo.exists(name)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s.", name));
            }
            Bin bin = binRepo.get(name);
            List<Map<String, Object>> multiOps = opsRepo.values(bin.getName());
            Map<String, Object> singleOps = multiOps.stream().findAny().orElse(Collections.emptyMap());
            String identifier = singleOps.isEmpty() ? "{identifier}" : (String) singleOps.get(ID_FIELD);

            Ops getOps = new Ops(identifier, "GET", StringUtils.EMPTY, singleOps.isEmpty() ? StringUtils.EMPTY
                    : singleOps, StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            Map<String, Object> post = Maps.newHashMap(singleOps);
            post.remove(ID_FIELD);
            Ops postOps = new Ops("", "POST", singleOps.isEmpty() ?
                    StringUtils.EMPTY : post, singleOps.isEmpty() ? StringUtils.EMPTY : singleOps, StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            Ops putOps = new Ops(identifier, "PUT", singleOps.isEmpty() ?
                    StringUtils.EMPTY : singleOps, singleOps.isEmpty() ? StringUtils.EMPTY : singleOps, StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            Map<String, Object> patch = Maps.newHashMap(singleOps);
            patch.put("KEY", "VALUE");
            Ops patchOps = new Ops(identifier, "PATCH", singleOps.isEmpty() ? StringUtils.EMPTY :
                    Collections.singletonMap("KEY", "VALUE"), singleOps.isEmpty() ? StringUtils.EMPTY : patch, StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            Ops deleteOps = new Ops(identifier, "DELETE", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            Ops listOps = new Ops("", "GET", StringUtils.EMPTY,
                    new PageImpl<>(multiOps.stream().skip(0).limit(10).collect(Collectors.toList()),
                    PageRequest.of(0, 10), multiOps.size()), StringUtils.EMPTY,
                    LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));

            bin.setOps(new PageImpl<>(Stream.of(getOps, postOps, putOps, patchOps, deleteOps, listOps)
                    .collect(Collectors.toList()), PageRequest.of(0, 10), 6));

            return bin;
        }
        throw new DataNotFoundException(String.format("Bin does not exist by the name of: %s.", name));
    }

    @Override
    public Page<Ops> listOps(String name, int p, int l) throws DataNotFoundException {
        if (binRepo.exists(name)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Bin found by the name of: %s.", name));
            }
            List<Ops> ops = auditRepo.values(name).stream().filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Ops::getCreatedAt).reversed())
                    .skip((long) p * l).limit(l).collect(Collectors.toList());
            return new PageImpl<>(ops, PageRequest.of(p, l), auditRepo.count(name));
        }
        throw new DataNotFoundException(String.format("Bin does not exist by the name of: %s.", name));
    }
}

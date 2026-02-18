package com.github.request.bin.controllers;

import com.github.request.bin.data.Bin;
import com.github.request.bin.data.Ops;
import com.github.request.bin.services.BinService;
import com.github.request.bin.utils.LogThis;
import com.github.request.bin.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/bins")
@Slf4j
public class BinController {

    @Autowired
    private BinService binService;

    @LogThis(details = false)
    @Operation(summary = "Create a bin", description = "Endpoint: Create a bin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Bin createBin(@RequestParam(defaultValue = "", required = false) String name,
                         @RequestParam(defaultValue = "false", required = false) boolean autogenerate,
                         @RequestBody(required = false) Map<String, Object> schema) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(name) || autogenerate, "Either provide name or set autogenerate to true.");
            return binService.create(name, autogenerate, schema);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }

    @LogThis(details = false)
    @Operation(summary = "Delete a bin", description = "Endpoint: Delete a bin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @DeleteMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteBin(@PathVariable String name) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(name), "Missing name of the bin to delete");
            binService.delete(name);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }

    @LogThis(details = false)
    @Operation(summary = "List all bins", description = "Endpoint: List all bins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Bin> listBins(@RequestParam(required = false, defaultValue = "1") int p,
                              @RequestParam(required = false, defaultValue = "10") int l) {
        try {
            return binService.list(p - 1, l);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }

    @LogThis(details = false)
    @Operation(summary = "Get a bin", description = "Endpoint: Get a bin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Bin getBin(@PathVariable String name) {
        try {
            return binService.get(name);
        } catch (DataNotFoundException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, iae.getLocalizedMessage());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }

    @LogThis(details = false)
    @Operation(summary = "Get all ops for a bin", description = "Endpoint: Get all ops for a bin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @GetMapping(value = "/{name}/ops", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Ops> getBinOps(@PathVariable String name, @RequestParam(required = false, defaultValue = "1") int p,
                               @RequestParam(required = false, defaultValue = "10") int l) {
        try {
            return binService.listOps(name, p - 1, l);
        } catch (DataNotFoundException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, iae.getLocalizedMessage());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }
}

package com.github.requestbin.controllers;

import com.github.requestbin.data.Bin;
import com.github.requestbin.data.Ops;
import com.github.requestbin.exception.DataNotFoundException;
import com.github.requestbin.services.BinService;
import com.github.requestbin.utils.LogThis;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Endpoint: Create a bin")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success response", response = Bin.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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
    @ApiOperation(value = "Endpoint: Delete a bin")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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
    @ApiOperation(value = "Endpoint: List all bins")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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
    @ApiOperation(value = "Endpoint: Get a bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response", response = Bin.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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
    @ApiOperation(value = "Endpoint: Get all ops for a  bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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

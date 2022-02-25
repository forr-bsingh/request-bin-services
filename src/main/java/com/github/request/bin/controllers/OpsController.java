package com.github.request.bin.controllers;

import com.github.request.bin.data.Ops;
import com.github.request.bin.exception.DataNotFoundException;
import com.github.request.bin.services.OpsService;
import com.github.request.bin.utils.LogThis;
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
@RequestMapping("/bins/{name}/requests")
@Slf4j
public class OpsController {

    @Autowired
    private OpsService opsService;

    @LogThis(details = false)
    @ApiOperation(value = "Endpoint: Post to bin")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success response", response = Ops.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Map<String, Object> post(@PathVariable String name,
                                    @RequestBody Map<String, Object> payload,
                                    @RequestHeader(required = false) Map<String, String> headers) {
        try {
            Assert.isTrue(!payload.isEmpty(), "Payload cannot be empty.");
            return opsService.postOps(name, payload, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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
    @ApiOperation(value = "Endpoint: Delete from bin")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @DeleteMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name, @PathVariable String identifier,
                       @RequestHeader(required = false) Map<String, String> headers) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(identifier), "Identifier cannot be empty.");
            opsService.deleteOps(name, identifier, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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
    @ApiOperation(value = "Endpoint: List from bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Map<String, Object>> list(@PathVariable String name,
                                          @RequestParam(defaultValue = "1") int p,
                                          @RequestParam(defaultValue = "10") int l,
                                          @RequestHeader(required = false) Map<String, String> headers) {
        try {
            return opsService.listOps(name, p - 1, l, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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
    @ApiOperation(value = "Endpoint: Get from bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response", response = Ops.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> get(@PathVariable String name,
                                   @PathVariable String identifier,
                                   @RequestHeader(required = false) Map<String, String> headers) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(identifier), "Identifier cannot be empty.");
            return opsService.getOps(name, identifier, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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
    @ApiOperation(value = "Endpoint: Put to bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response", response = Ops.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> put(@PathVariable String name,
                                   @PathVariable String identifier,
                                   @RequestBody Map<String, Object> payload,
                                   @RequestHeader(required = false) Map<String, String> headers) {
        try {
            Assert.isTrue(!payload.isEmpty(), "Payload cannot be empty.");
            Assert.isTrue(StringUtils.isNotBlank(identifier), "Identifier cannot be empty.");
            return opsService.putOps(name, identifier, payload, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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
    @ApiOperation(value = "Endpoint: Patch in bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response", response = Ops.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @PatchMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> patch(@PathVariable String name,
                                     @PathVariable String identifier,
                                     @RequestBody Map<String, Object> payload,
                                     @RequestHeader(required = false) Map<String, String> headers) {
        try {
            Assert.isTrue(!payload.isEmpty(), "Payload cannot be empty.");
            Assert.isTrue(StringUtils.isNotBlank(identifier), "Identifier cannot be empty.");
            return opsService.patchOps(name, identifier, payload, headers);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getLocalizedMessage());
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

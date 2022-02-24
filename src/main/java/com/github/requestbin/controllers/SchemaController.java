package com.github.requestbin.controllers;

import com.github.requestbin.data.Attribute;
import com.github.requestbin.services.SchemaService;
import com.github.requestbin.utils.LogThis;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/schema")
@Slf4j
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @LogThis(details = false)
    @ApiOperation(value = "Endpoint: List all attributes to be used to define schema")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @GetMapping(value = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Attribute> listAttributes() {
        try {
            return schemaService.attributes();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }

    @LogThis(details = false)
    @ApiOperation(value = "Endpoint: Resolve schema and generate object with dummy data.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
    })
    @PostMapping(value = "/resolve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> resolveSchema(@RequestBody Map<String, Object> schema) {
        try {
            return schemaService.resolve(schema);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred. StackTrace:", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }
}

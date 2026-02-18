package com.github.request.bin.controllers;

import com.github.request.bin.data.Attribute;
import com.github.request.bin.services.SchemaService;
import com.github.request.bin.utils.LogThis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "List all attributes to be used to define schema", description = "Endpoint: List all attributes to be used to define schema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
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
    @Operation(summary = "Resolve schema and generate object with dummy data", description = "Endpoint: Resolve schema and generate object with dummy data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
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

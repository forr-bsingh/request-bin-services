package com.github.request.bin.controllers;

import com.github.request.bin.services.PingService;
import com.github.request.bin.utils.LogThis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
public class PingController {

    @Autowired
    private PingService pingService;

    @LogThis(details = false)
    @Operation(summary = "PING", description = "Endpoint: PING")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success response"),
            @ApiResponse(responseCode = "500", description = "Generic exception. Please notify the team.")
    })
    @GetMapping(value = "/ping")
    public String ping() {
        try {
            return pingService.ping();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Generic exception. Please check stacktrace.", e);
        }
    }
}

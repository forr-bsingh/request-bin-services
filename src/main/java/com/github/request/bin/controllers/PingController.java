package com.github.request.bin.controllers;

import com.github.request.bin.services.PingService;
import com.github.request.bin.utils.LogThis;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Endpoint: PING")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success response", response = String.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the team.")
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

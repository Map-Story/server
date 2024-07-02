package com.team13.mapstory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Main", description = "Main Server")
public class MainController {

    @GetMapping("/")
    @Operation(summary = "Server Test", description = "Server Test")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    public String Main() {
        return "Server is running!";
    }
}
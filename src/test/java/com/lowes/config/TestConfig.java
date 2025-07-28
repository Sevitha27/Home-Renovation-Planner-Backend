package com.lowes.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.lowes.config.JWTAuthenticationFilter;
import com.lowes.service.JwtService;

@TestConfiguration
public class TestConfig {

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;
}
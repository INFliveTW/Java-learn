package com.example.weatherforecast.Config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Weather Forecast API", version = "1.0"))
public class SwaggerConfig {
    // SpringDoc 不需要額外 Docket 配置
}
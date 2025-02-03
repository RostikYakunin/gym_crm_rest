package com.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.Set;

@Configuration
public class ConversionServiceConfig {
    @Bean
    @Primary
    public ConversionService conversionService(Set<Converter<?, ?>> converters) {
        var conversionService = new GenericConversionService();
        converters.forEach(conversionService::addConverter);
        return conversionService;
    }
}

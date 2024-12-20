package com.project.multidb.configs;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BusinessDatasourceConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.business")
    public DataSourceProperties businessDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean
    public DataSource businessDataSource() {
        return businessDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
}
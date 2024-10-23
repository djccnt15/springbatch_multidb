package com.project.multidb.configs;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class BatchMetaDatasourceConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Primary
    @Bean
    public DataSource batchDataSource() {
        return batchDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
}

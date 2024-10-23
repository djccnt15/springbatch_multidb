package com.project.multidb.configs;

import com.project.multidb.model.TestDataEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackageClasses = TestDataEntity.class,
    entityManagerFactoryRef = "businessEntityManagerFactory",
    transactionManagerRef = "businessTransactionManager"
)
public class BusinessJpaConfiguration {
    
    @Bean
    public PlatformTransactionManager businessTransactionManager(
        @Qualifier("businessEntityManagerFactory") LocalContainerEntityManagerFactoryBean businessEntityManagerFactory
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(businessEntityManagerFactory.getObject()));
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean businessEntityManagerFactory(
        @Qualifier("businessDataSource") DataSource dataSource,
        EntityManagerFactoryBuilder builder
    ) {
        return builder
            .dataSource(dataSource)
            .packages(TestDataEntity.class)
            .build();
    }
}
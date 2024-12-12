package com.project.multidb.job;

import com.project.multidb.annotations.Batch;
import com.project.multidb.job.converter.TestDataConverter;
import com.project.multidb.job.model.TestDataDto;
import com.project.multidb.model.TestDataEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
// @Batch
@RequiredArgsConstructor
public class SimpleJobConfiguration {
    
    // @Qualifier("businessDataSource")  // IMPORTANT if using with Primary DataSource!!
    // private final DataSource businessDataSource;
    
    private final EntityManagerFactory businessEntityManagerFactory;
    private final TestDataConverter converter;
    
    private static final int CHUNK_SIZE = 100;  // chunk size and fetch size must be same if working with JPA
    
    /**
     * change job name everytime to get job parameter from OS command.
     * Spring Batch fetches job parameters from the Job metadata stored in its database, if a job instance with the same parameters already exists
     * @param jobRepository injection from spring batch
     * @param step Spring Batch step configuration
     * @return Spring Batch Job
     */
    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("simpleJob_%s".formatted(LocalDateTime.now().withNano(0)), jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }
    
    @Bean
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("step", jobRepository)
            .<TestDataEntity, TestDataDto>chunk(CHUNK_SIZE, transactionManager)
            // .reader(jdbcCursorItemReader())
            .reader(jpaPagingItemReader())
            .processor(itemProcessor(null))
            .writer(itemWriter())
            .build();
    }
    
    // @Bean
    // public JdbcCursorItemReader<TestDataEntity> jdbcCursorItemReader() {
    //     return new JdbcCursorItemReaderBuilder<UserLoginEntity>()
    //         .fetchSize(CHUNK_SIZE)
    //         .dataSource(businessDataSource)
    //         .rowMapper(new BeanPropertyRowMapper<>(TestDataEntity.class))
    //         .sql("SELECT * FROM TEST_DATA")
    //         .name("jdbcCursorItemReader")
    //         .build();
    // }
    
    @Bean
    public JpaPagingItemReader<TestDataEntity> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<TestDataEntity>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(businessEntityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT u FROM TestDataEntity u")  // logical name of the table from JPA package class
            .build();
    }
    
    @Bean
    @StepScope
    public ItemProcessor<TestDataEntity, TestDataDto> itemProcessor(
        @Value("#{jobParameters[baseDate]}") String baseDate
    ) {
        var parsedDatetime = LocalDate.now();
        if (baseDate != null) {
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            parsedDatetime = LocalDate.parse(baseDate, formatter);
        }
        var finalParsedDatetime = parsedDatetime;
        
        return it -> {
            var daysBetween = ChronoUnit.DAYS.between(it.getLastUpd(), finalParsedDatetime);
            return converter.toDto(it, daysBetween);
        };
    }
    
    @Bean
    public ItemWriter<TestDataDto> itemWriter() {
        return items -> {
            for (TestDataDto item : items) {
                log.info("user name: {}, days between: {}", item.getUserId(), item.getDaysBetween());
            }
        };
    }
}
package com.project.multidb.job;

import com.project.multidb.annotations.Batch;
import com.project.multidb.job.model.JoinedDataDto;
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
@Batch
@RequiredArgsConstructor
public class JoinedDataJobConfiguration {
    
    private final EntityManagerFactory businessEntityManagerFactory;
    
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
        return new JobBuilder("joinedDataJob_%s".formatted(LocalDateTime.now().withNano(0)), jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }
    
    @Bean
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("joinedDataStep", jobRepository)
            .<JoinedDataDto, JoinedDataDto>chunk(CHUNK_SIZE, transactionManager)
            .reader(jpaPagingItemReader())
            .processor(itemProcessor(null))
            .writer(itemWriter())
            .build();
    }
    
    @Bean
    public JpaPagingItemReader<JoinedDataDto> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<JoinedDataDto>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(businessEntityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString(
                "SELECT new com.project.multidb.job.model.JoinedDataDto(td.userId, td.lastUpd, td.cnt, jd.email) " +
                    "FROM TestDataEntity td JOIN JoinDataEntity jd ON td.userId = jd.userId"
            )  // logical name of the table from JPA package class
            .build();
    }
    
    @Bean
    @StepScope
    public ItemProcessor<JoinedDataDto, JoinedDataDto> itemProcessor(
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
            it.setDaysBetween(daysBetween);
            return it;
        };
    }
    
    @Bean
    public ItemWriter<JoinedDataDto> itemWriter() {
        return items -> {
            for (JoinedDataDto item : items) {
                log.info("user name: {}, days between: {}", item.getUserId(), item.getDaysBetween());
            }
        };
    }
}
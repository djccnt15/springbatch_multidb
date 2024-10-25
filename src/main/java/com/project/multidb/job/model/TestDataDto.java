package com.project.multidb.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDataDto {
    
    private String userId;
    
    private LocalDate pwLastUpd;
    
    private Integer failedCnt;
    
    private Long daysBetween;
}
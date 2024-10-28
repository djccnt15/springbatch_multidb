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
public class JoinedDataDto {
    
    private String userId;
    
    private LocalDate lastUpd;
    
    private Integer cnt;
    
    private String email;
    
    private Long daysBetween;
    
    public JoinedDataDto(String userId, LocalDate lastUpd, Integer cnt, String email) {
        this.userId = userId;
        this.lastUpd = lastUpd;
        this.cnt = cnt;
        this.email = email;
        this.daysBetween = null;
    }
}

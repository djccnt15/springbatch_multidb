package com.project.multidb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "TEST_DATA")
public class TestDataEntity {
    
    @Id
    @Column(name = "USER_ID")
    private String userId;
    
    @Column(name = "LAST_UPD")
    private LocalDate lastUpd;
    
    @Column(name = "CNT")
    private Integer cnt;
}

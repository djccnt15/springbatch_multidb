package com.project.multidb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "JOIN_DATA")
public class JoinDataEntity {
    
    @Id
    @Column(name = "USER_ID")
    private String userId;
    
    @Column(name = "EMAIL")
    private String email;
}

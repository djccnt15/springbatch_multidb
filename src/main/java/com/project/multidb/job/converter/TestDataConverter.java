package com.project.multidb.job.converter;

import com.project.multidb.annotations.Converter;
import com.project.multidb.job.model.TestDataDto;
import com.project.multidb.model.TestDataEntity;

@Converter
public class TestDataConverter {
    
    public TestDataDto toDto(TestDataEntity testDataEntity, Long daysBetween) {
        return TestDataDto.builder()
            .userId(testDataEntity.getUserId())
            .pwLastUpd(testDataEntity.getLastUpd())
            .failedCnt(testDataEntity.getCnt())
            .daysBetween(daysBetween)
            .build();
    }
}
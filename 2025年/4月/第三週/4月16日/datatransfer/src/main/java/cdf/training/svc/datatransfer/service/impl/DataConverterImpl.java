package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

@Component
public class DataConverterImpl {
    public List<EmployeeDataEntity> convertToEntities(List<EmployeeDataCSVDto> dtos, String COMPANY, LocalDateTime EXCUTETIME) {
        return dtos.stream().map(dto -> {
            EmployeeDataEntity entity = new EmployeeDataEntity();
            entity.setID(dto.getID());
            entity.setDEPARTMENT(dto.getDEPARTMENT());
            entity.setJOB_TITLE(dto.getJOB_TITLE());
            entity.setNAME(dto.getNAME());
            entity.setTEL(dto.getTEL());
            entity.setEMAIL(dto.getEMAIL());
            entity.setCOMPANY(COMPANY);
            entity.setEXCUTETIME(EXCUTETIME);
            return entity;
        }).collect(Collectors.toList());
    }
}
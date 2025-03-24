package cdf.training.svc.datatransfer.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeDataCSVDto {
    private String ID;
    private String DEPARTMENT;
    private String JOB_TITLE;
    private String NAME;
    private String TEL;
    private String EMAIL;
    private String COMPANY;      // 新增欄位
    private LocalDateTime EXCUTETIME; // 新增欄位
}
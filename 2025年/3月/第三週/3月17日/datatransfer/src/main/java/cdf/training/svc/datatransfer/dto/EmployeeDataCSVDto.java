package cdf.training.svc.datatransfer.dto;

import lombok.Data;

@Data
public class EmployeeDataCSVDto {
    private String ID;
    private String DEPARTMENT;
    private String JOB_TITLE;
    private String NAME;
    private String TEL;
    private String EMAIL;
}
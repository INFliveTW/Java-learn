package cdf.training.svc.datatransfer.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "employee_data")
public class EmployeeDataEntity {
    @Id
    private String ID;
    private String DEPARTMENT;
    private String JOB_TITLE;
    private String NAME;
    private String TEL;
    private String EMAIL;
    private String COMPANY;
    private LocalDateTime EXCUTETIME;
}
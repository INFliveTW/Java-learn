package cdf.training.svc.datatransfer.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeDataEntity {
    private String ID; // CSV 中的 ID，普通欄位
    private String DEPARTMENT;
    private String JOB_TITLE;
    private String NAME;
    private String TEL;
    private String EMAIL;
    private String COMPANY;
    private LocalDateTime EXCUTETIME;
}
//不需要 @Entity 或 @Id，因為 MyBatis 不依賴 JPA 的注解，而是一個純數據物件（POJO）。

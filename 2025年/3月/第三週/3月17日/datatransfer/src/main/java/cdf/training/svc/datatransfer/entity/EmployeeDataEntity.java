// package cdf.training.svc.datatransfer.entity;

// import java.time.LocalDateTime;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.Data;

// @Data
// @Entity
// @Table(name = "employee_data")
// public class EmployeeDataEntity {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主鍵
//     private Long recordId; // 新增欄位，僅用於滿足 JPA

//     private String ID; // CSV 中的 ID，普通欄位
//     private String DEPARTMENT;
//     private String JOB_TITLE;
//     private String NAME;
//     private String TEL;
//     private String EMAIL;
//     private String COMPANY;
//     private LocalDateTime EXCUTETIME;
// }
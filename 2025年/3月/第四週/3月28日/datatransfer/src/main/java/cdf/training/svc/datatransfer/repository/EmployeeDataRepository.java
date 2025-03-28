package cdf.training.svc.datatransfer.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

@Mapper
public interface EmployeeDataRepository {
    @Insert("INSERT INTO employee_data (ID, DEPARTMENT, JOB_TITLE, NAME, TEL, EMAIL, COMPANY, EXCUTETIME) " +
            "VALUES (#{ID}, #{DEPARTMENT}, #{JOB_TITLE}, #{NAME}, #{TEL}, #{EMAIL}, #{COMPANY}, #{EXCUTETIME})")
    void insert(EmployeeDataEntity entity);
}
//import org.springframework.data.jpa.repository.JpaRepository;
//@Mapper：標記這是一個 MyBatis Mapper 接口。
//@Insert：定義插入 SQL，#{} 用於從 EmployeeDataEntity 物件中取值。
//無主鍵：MyBatis 不要求主鍵，這與你的需求一致。


//public interface EmployeeDataRepository extends JpaRepository<EmployeeDataEntity, Long> {
//}
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
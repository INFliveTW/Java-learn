package cdf.training.svc.datatransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

public interface EmployeeDataRepository extends JpaRepository<EmployeeDataEntity, String> 
{

}
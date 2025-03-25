package cdf.training.svc.datatransfer.repository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

@SpringBootTest
class EmployeeDataRepositoryTest {
    @Autowired
    private EmployeeDataRepository repository;

    @Test
    void testInsert() {
        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("1");
        entity.setDEPARTMENT("IT");
        entity.setJOB_TITLE("Engineer");
        entity.setNAME("John");
        entity.setTEL("123456");
        entity.setEMAIL("john@example.com");
        entity.setCOMPANY("金控");
        entity.setEXCUTETIME(LocalDateTime.now());

        assertDoesNotThrow(() -> repository.insert(entity));
        System.out.println("測試成功"); // 測試通過時顯示
    }
}
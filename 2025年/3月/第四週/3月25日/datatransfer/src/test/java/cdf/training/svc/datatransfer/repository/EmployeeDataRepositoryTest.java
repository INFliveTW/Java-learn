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
    void testInsert() { //測試CSV資料寫入SQL資料庫
        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("TW234121");
        entity.setDEPARTMENT("資安");
        entity.setJOB_TITLE("工程師");
        entity.setNAME("陳亮");
        entity.setTEL("02-1234-5678");
        entity.setEMAIL("LightChen@gmail.com");
        entity.setCOMPANY("金控");
        entity.setEXCUTETIME(LocalDateTime.now());

        assertDoesNotThrow(() -> repository.insert(entity));
        System.out.println("測試CSV資料寫入SQL資料庫，測試成功"); // 測試通過時顯示
    }
}
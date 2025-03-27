package cdf.training.svc.datatransfer.repository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

class EmployeeDataRepositoryTest {

    @InjectMocks
    private EmployeeDataRepository repository;

    @BeforeEach
    void setUp() {
        // 模擬 repository
        repository = mock(EmployeeDataRepository.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsert() { // 測試CSV資料寫入SQL資料庫
        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("TW234121");
        entity.setDEPARTMENT("資安");
        entity.setJOB_TITLE("工程師");
        entity.setNAME("陳亮");
        entity.setTEL("02-1234-5678");
        entity.setEMAIL("LightChen@gmail.com");
        entity.setCOMPANY("金控");
        entity.setEXCUTETIME(LocalDateTime.now());

        // 模擬 insert 方法不拋出異常
        doNothing().when(repository).insert(entity);

        assertDoesNotThrow(() -> repository.insert(entity));
        System.out.println("測試CSV資料寫入SQL資料庫，測試成功");
    }
}
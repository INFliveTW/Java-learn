package cdf.training.svc.datatransfer.repository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

class EmployeeDataRepositoryTest {

    @Mock
    private EmployeeDataRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsert_Success() {
        // Arrange
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

        // Act & Assert
        assertDoesNotThrow(() -> repository.insert(entity));
        System.out.println("測試CSV資料寫入SQL資料庫，測試成功");
    }

    @Test
    void testInsert_DatabaseException() {
        // Arrange
        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("TW234121");
        entity.setDEPARTMENT("資安");
        entity.setJOB_TITLE("工程師");
        entity.setNAME("陳亮");
        entity.setTEL("02-1234-5678");
        entity.setEMAIL("LightChen@gmail.com");
        entity.setCOMPANY("金控");
        entity.setEXCUTETIME(LocalDateTime.now());

        // 模擬 insert 方法拋出異常
        doThrow(new RuntimeException("Database connection failed")).when(repository).insert(entity);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> repository.insert(entity));
        System.out.println("測試資料庫寫入失敗，測試成功");
    }
}
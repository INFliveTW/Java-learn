package cdf.training.svc.datatransfer.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;
import cdf.training.svc.datatransfer.service.impl.DataConverterImpl;

public class DataConverterImplTest {

    private DataConverterImpl dataConverter;
    private LocalDateTime excuteTime;
    private String company;

    @BeforeEach
    void setUp() {
        dataConverter = new DataConverterImpl();
        excuteTime = LocalDateTime.of(2025, 4, 9, 10, 0); // 假設一個執行時間
        company = "TestCompany";
    }

    @Test
    void testConvertToEntities_Success() {
        // 準備測試數據
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE("Engineer");
        dto.setNAME("John");
        dto.setTEL("12345678");
        dto.setEMAIL("john@example.com");
        List<EmployeeDataCSVDto> dtos = Arrays.asList(dto);

        // 執行轉換
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(dtos, company, excuteTime);

        // 驗證結果
        assertNotNull(entities);
        assertEquals(1, entities.size());
        EmployeeDataEntity entity = entities.get(0);
        assertEquals("1", entity.getID());
        assertEquals("IT", entity.getDEPARTMENT());
        assertEquals("Engineer", entity.getJOB_TITLE());
        assertEquals("John", entity.getNAME());
        assertEquals("12345678", entity.getTEL());
        assertEquals("john@example.com", entity.getEMAIL());
        assertEquals(company, entity.getCOMPANY());
        assertEquals(excuteTime, entity.getEXCUTETIME());
        System.out.println("成功將 DTO 轉換為 Entity，測試成功");
    }

    @Test
    void testConvertToEntities_EmptyList() {
        // 準備空的 DTO 列表
        List<EmployeeDataCSVDto> dtos = Collections.emptyList();

        // 執行轉換
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(dtos, company, excuteTime);

        // 驗證結果
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
        System.out.println("空的 DTO 列表轉換為空的 Entity 列表，測試成功");
    }

    @Test
    void testConvertToEntities_MultipleDtos() {
        // 準備多個 DTO
        EmployeeDataCSVDto dto1 = new EmployeeDataCSVDto();
        dto1.setID("1");
        dto1.setDEPARTMENT("IT");
        dto1.setJOB_TITLE("Engineer");
        dto1.setNAME("John");
        dto1.setTEL("12345678");
        dto1.setEMAIL("john@example.com");

        EmployeeDataCSVDto dto2 = new EmployeeDataCSVDto();
        dto2.setID("2");
        dto2.setDEPARTMENT("HR");
        dto2.setJOB_TITLE("Manager");
        dto2.setNAME("Jane");
        dto2.setTEL("87654321");
        dto2.setEMAIL("jane@example.com");

        List<EmployeeDataCSVDto> dtos = Arrays.asList(dto1, dto2);

        // 執行轉換
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(dtos, company, excuteTime);

        // 驗證結果
        assertNotNull(entities);
        assertEquals(2, entities.size());

        EmployeeDataEntity entity1 = entities.get(0);
        assertEquals("1", entity1.getID());
        assertEquals("IT", entity1.getDEPARTMENT());
        assertEquals("Engineer", entity1.getJOB_TITLE());
        assertEquals("John", entity1.getNAME());
        assertEquals("12345678", entity1.getTEL());
        assertEquals("john@example.com", entity1.getEMAIL());
        assertEquals(company, entity1.getCOMPANY());
        assertEquals(excuteTime, entity1.getEXCUTETIME());

        EmployeeDataEntity entity2 = entities.get(1);
        assertEquals("2", entity2.getID());
        assertEquals("HR", entity2.getDEPARTMENT());
        assertEquals("Manager", entity2.getJOB_TITLE());
        assertEquals("Jane", entity2.getNAME());
        assertEquals("87654321", entity2.getTEL());
        assertEquals("jane@example.com", entity2.getEMAIL());
        assertEquals(company, entity2.getCOMPANY());
        assertEquals(excuteTime, entity2.getEXCUTETIME());

        System.out.println("多個 DTO 轉換為多個 Entity，測試成功");
    }

    @Test
    void testConvertToEntities_NullFields() {
        // 準備包含 null 欄位的 DTO
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID(null); // ID 為 null
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE(null); // JOB_TITLE 為 null
        dto.setNAME("John");
        dto.setTEL(null); // TEL 為 null
        dto.setEMAIL("john@example.com");
        List<EmployeeDataCSVDto> dtos = Arrays.asList(dto);

        // 執行轉換
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(dtos, company, excuteTime);

        // 驗證結果
        assertNotNull(entities);
        assertEquals(1, entities.size());
        EmployeeDataEntity entity = entities.get(0);
        assertEquals(null, entity.getID());
        assertEquals("IT", entity.getDEPARTMENT());
        assertEquals(null, entity.getJOB_TITLE());
        assertEquals("John", entity.getNAME());
        assertEquals(null, entity.getTEL());
        assertEquals("john@example.com", entity.getEMAIL());
        assertEquals(company, entity.getCOMPANY());
        assertEquals(excuteTime, entity.getEXCUTETIME());
        System.out.println("DTO 包含 null 欄位，轉換為 Entity 後保持 null，測試成功");
    }

    @Test
    void testConvertToEntities_NullInputList() {
        // 準備 null 的 DTO 列表
        List<EmployeeDataCSVDto> dtos = null;

        // 執行轉換，預期拋出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            dataConverter.convertToEntities(dtos, company, excuteTime);
        });
        System.out.println("傳入 null 的 DTO 列表，拋出 NullPointerException，測試成功");
    }
}
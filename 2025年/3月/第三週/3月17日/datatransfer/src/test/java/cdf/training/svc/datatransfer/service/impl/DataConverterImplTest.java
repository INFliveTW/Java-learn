package cdf.training.svc.datatransfer.service.impl;

import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataConverterImplTest {
    private DataConverterImpl dataConverter = new DataConverterImpl();

    @Test
    void testConvertToEntities() {
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE("Engineer");
        dto.setNAME("John");
        dto.setTEL("123456");
        dto.setEMAIL("john@example.com");

        LocalDateTime now = LocalDateTime.now();
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(List.of(dto), "金控", now);
        assertEquals(1, entities.size());
        EmployeeDataEntity entity = entities.get(0);
        assertEquals("1", entity.getID());
        assertEquals("IT", entity.getDEPARTMENT());
        assertEquals("Engineer", entity.getJOB_TITLE());
        assertEquals("John", entity.getNAME());
        assertEquals("123456", entity.getTEL());
        assertEquals("john@example.com", entity.getEMAIL());
        assertEquals("金控", entity.getCOMPANY());
        assertEquals(now, entity.getEXCUTETIME());
        System.out.println("測試成功"); // 測試通過時顯示
    }
}
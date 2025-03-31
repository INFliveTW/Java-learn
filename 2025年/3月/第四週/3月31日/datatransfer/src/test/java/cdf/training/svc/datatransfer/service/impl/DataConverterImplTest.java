package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;

class DataConverterImplTest {
    private DataConverterImpl dataConverter = new DataConverterImpl();

    @Test
    void testConvertToEntities_SingleRecord() {
        // Arrange
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("TW234121");
        dto.setDEPARTMENT("資安");
        dto.setJOB_TITLE("工程師");
        dto.setNAME("陳亮");
        dto.setTEL("02-1234-5678");
        dto.setEMAIL("LightChen@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(List.of(dto), "金控", now);

        // Act & Assert
        assertEquals(1, entities.size());
        EmployeeDataEntity entity = entities.get(0);
        assertEquals("TW234121", entity.getID());
        assertEquals("資安", entity.getDEPARTMENT());
        assertEquals("工程師", entity.getJOB_TITLE());
        assertEquals("陳亮", entity.getNAME());
        assertEquals("02-1234-5678", entity.getTEL());
        assertEquals("LightChen@gmail.com", entity.getEMAIL());
        assertEquals("金控", entity.getCOMPANY());
        assertEquals(now, entity.getEXCUTETIME());
        System.out.println("單筆資料轉換，測試成功");
    }

    @Test
    void testConvertToEntities_MultipleRecords() {
        // Arrange
        EmployeeDataCSVDto dto1 = new EmployeeDataCSVDto();
        dto1.setID("TW234121");
        dto1.setDEPARTMENT("資安");
        dto1.setJOB_TITLE("工程師");
        dto1.setNAME("陳亮");
        dto1.setTEL("02-1234-5678");
        dto1.setEMAIL("LightChen@gmail.com");

        EmployeeDataCSVDto dto2 = new EmployeeDataCSVDto();
        dto2.setID("TW234122");
        dto2.setDEPARTMENT("財務");
        dto2.setJOB_TITLE("分析師");
        dto2.setNAME("李明");
        dto2.setTEL("02-9876-5432");
        dto2.setEMAIL("MingLi@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(List.of(dto1, dto2), "金控", now);

        // Act & Assert
        assertEquals(2, entities.size());
        EmployeeDataEntity entity1 = entities.get(0);
        assertEquals("TW234121", entity1.getID());
        assertEquals("金控", entity1.getCOMPANY());
        assertEquals(now, entity1.getEXCUTETIME());

        EmployeeDataEntity entity2 = entities.get(1);
        assertEquals("TW234122", entity2.getID());
        assertEquals("財務", entity2.getDEPARTMENT());
        assertEquals("金控", entity2.getCOMPANY());
        assertEquals(now, entity2.getEXCUTETIME());
        System.out.println("多筆資料轉換，測試成功");
    }
}
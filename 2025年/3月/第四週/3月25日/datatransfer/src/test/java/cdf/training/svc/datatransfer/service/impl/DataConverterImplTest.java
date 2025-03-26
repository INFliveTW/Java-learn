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
    void testConvertToEntities() {
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("TW234121");
        dto.setDEPARTMENT("資安");
        dto.setJOB_TITLE("工程師");
        dto.setNAME("陳亮");
        dto.setTEL("02-1234-5678");
        dto.setEMAIL("LightChen@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        List<EmployeeDataEntity> entities = dataConverter.convertToEntities(List.of(dto), "金控", now);
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
        System.out.println("資料轉換，測試成功"); // 測試通過時顯示
    }
}
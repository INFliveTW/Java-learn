package cdf.training.svc.datatransfer.util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;

class CSVParserUtilTest {
    private CSVParserUtil csvParserUtil = new CSVParserUtil();

    @Test
    void testParseCsv_Success() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,123456,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        EmployeeDataCSVDto dto = dtos.get(0);
        assertEquals("TW234121", dto.getID());
        assertEquals("資安", dto.getDEPARTMENT());
        assertEquals("工程師", dto.getJOB_TITLE());
        assertEquals("陳亮", dto.getNAME());
        assertEquals("02-1234-5678", dto.getTEL());
        assertEquals("LightChen@gmail.com", dto.getEMAIL());
        System.out.println("解析CSV，測試成功"); // 測試通過時顯示
    }

@Test
void testParseCsv_EmptyContent() {
    String csvContent = "";
    List<EmployeeDataCSVDto> result = csvParserUtil.parseCsv(csvContent);
    assertTrue(result.isEmpty(), "應返回空列表");
    System.out.println("CSV資料為空，測試成功"); // 測試通過時顯示
}

    @Test
    void testParseCsv_InvalidFormat() {
        String csvContent = "ID,DEPARTMENT\n1,IT,Engineer"; // 欄位數量不匹配
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        EmployeeDataCSVDto dto = dtos.get(0);
        assertEquals("TW234121", dto.getID());
        assertEquals("資安", dto.getDEPARTMENT());
        assertNull(dto.getJOB_TITLE()); // 由於欄位不足，應為 null
        System.out.println("CSV欄位不正確，測試成功"); // 測試通過時顯示
    }
}
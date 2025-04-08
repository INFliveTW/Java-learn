package cdf.training.svc.datatransfer.util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.opencsv.CSVReader;

import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;

public class CSVParserUtilTest {

    @InjectMocks
    private CSVParserUtil csvParserUtil;

    @Mock
    private CSVReader csvReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseCsv_NullContent() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserUtil.parseCsv(null);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("csvContent 為 null，測試成功");
    }

    @Test
    void testParseCsv_EmptyContent() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserUtil.parseCsv("");
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("csvContent 為空字串，測試成功");
    }

    @Test
    void testParseCsv_InvalidSeparator() {
        String csvContent = "ID;DEPARTMENT;JOB_TITLE;NAME;TEL;EMAIL\n1;IT;Engineer;John;12345678;john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 不合法分隔符 (使用 ; 而非 ,), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("csvContent 包含不合法分隔符，測試成功");
    }

    @Test
    void testParseCsv_WithBOM() {
        String csvContent = "\uFEFFID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        EmployeeDataCSVDto dto = dtos.get(0);
        assertEquals("1", dto.getID());
        assertEquals("IT", dto.getDEPARTMENT());
        assertEquals("Engineer", dto.getJOB_TITLE());
        assertEquals("John", dto.getNAME());
        assertEquals("12345678", dto.getTEL());
        assertEquals("john@example.com", dto.getEMAIL());
        System.out.println("csvContent 包含 BOM 字元，測試成功");
    }

    @Test
    void testParseCsv_EmptyLines() {
        String csvContent = "\n";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("lines 為空，測試成功");
    }

    @Test
    void testParseCsv_EmptyHeaders() throws Exception {
        String csvContent = "";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext()).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("headers 為空，測試成功");
    }

    @Test
    void testParseCsv_ReadHeadersFailure() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext()).thenThrow(new RuntimeException("Failed to read headers"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV headers: Failed to read headers", exception.getMessage());
        System.out.println("讀取標頭失敗，測試成功");
    }

    @Test
    void testParseCsv_MissingFields() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL\n1,IT,Engineer,John,12345678";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案 (缺少欄位: EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少必要欄位，測試成功");
    }

    @Test
    void testParseCsv_InsufficientCommas() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確 (第 1 行缺少分隔符號: 預期 5 個逗號，實際 3 個), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("每行逗號數量不足，測試成功");
    }

    @Test
    void testParseCsv_TooManyFields() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com,extra";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案 (第 1 行資料欄數多於標頭: 7 > 6), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄數過多，測試成功");
    }

    @Test
    void testParseCsv_TooFewFields() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確 (第 1 行缺少分隔符號: 預期 5 個逗號，實際 4 個), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄數過少，測試成功");
    }

    @Test
    void testParseCsv_MissingData() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n,IT,Engineer,John,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("必要資料缺失，測試成功");
    }

    @Test
    void testParseCsv_ReadDataFailure() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("Failed to read data"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: Failed to read data", exception.getMessage());
        System.out.println("讀取資料失敗，測試成功");
    }

    @Test
    void testParseCsv_Success() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        EmployeeDataCSVDto dto = dtos.get(0);
        assertEquals("1", dto.getID());
        assertEquals("IT", dto.getDEPARTMENT());
        assertEquals("Engineer", dto.getJOB_TITLE());
        assertEquals("John", dto.getNAME());
        assertEquals("12345678", dto.getTEL());
        assertEquals("john@example.com", dto.getEMAIL());
        System.out.println("正常解析，測試成功");
    }

    @Test
    void testParseCsv_Success_WithEmptyLine() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n\n2,HR,Manager,Jane,87654321,jane@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(2, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        assertEquals("2", dtos.get(1).getID());
        System.out.println("包含空行，測試成功");
    }

    @Test
    void testParseCsv_Success_WithSpaces() {
        String csvContent = "ID, DEPARTMENT , JOB_TITLE, NAME,TEL , EMAIL\n1 , IT, Engineer , John, 12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        EmployeeDataCSVDto dto = dtos.get(0);
        assertEquals("1", dto.getID());
        assertEquals("IT", dto.getDEPARTMENT());
        assertEquals("Engineer", dto.getJOB_TITLE());
        assertEquals("John", dto.getNAME());
        assertEquals("12345678", dto.getTEL());
        assertEquals("john@example.com", dto.getEMAIL());
        System.out.println("包含空格，測試成功");
    }

    @Test
    void testParseCsv_NonErrorResponseException() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("Some other error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: Some other error", exception.getMessage());
        System.out.println("非 ErrorResponseDto 格式的異常，測試成功");
    }

    @Test
    void testGetFieldValue_IndexOutOfBounds() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        List<String> lines = List.of("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", "1,IT,Engineer,,,");
        doReturn(lines).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer"})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確 (第 1 行欄數不一致: 3 < 6), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("getFieldValue 中 index 超出範圍，測試成功");
    }

    // 新增測試案例：模擬 lines 為空
    @Test
    void testParseCsv_LinesEmpty() throws Exception {
        String csvContent = "";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of()).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("lines 為空列表，測試成功");
    }

    // 新增測試案例：模擬數據行為空
    @Test
    void testParseCsv_EmptyDataLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{""}) // 模擬空數據行
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("數據行包含空行，測試成功");
    }

    // 新增測試案例：模擬缺失多個欄位
    @Test
    void testParseCsv_MissingMultipleFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n,,Engineer,,12345678,";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID, DEPARTMENT, NAME, EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺失多個欄位，測試成功");
    }

    // 新增測試案例：模擬 getFieldValue 返回 null
    @Test
    void testGetFieldValue_NullField() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        List<String> lines = List.of("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", "1,IT,Engineer,,,");
        doReturn(lines).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer"})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確 (第 1 行欄數不一致: 3 < 6), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("getFieldValue 返回 null，測試成功");
    }
}
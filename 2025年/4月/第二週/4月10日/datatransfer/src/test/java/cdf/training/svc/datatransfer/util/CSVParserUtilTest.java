package cdf.training.svc.datatransfer.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        assertThrows(IllegalArgumentException.class, () -> csvParserUtil.parseCsv(""));
        assertThrows(IllegalArgumentException.class, () -> csvParserUtil.parseCsv("\n"));
        System.out.println("CSV 內容為空或第一行為空，測試成功");
    }

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

    @Test
    void testParseCsv_FirstLineEmpty_Trimmed_Explicit() throws Exception {
        String csvContent = "   \n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of("   ")).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("第一行為空白字串（trim 後為空），測試成功");
    }

    @Test
    void testParseCsv_EmptyOrNullHeaders_Explicit() throws Exception {
        String csvContent = "";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());

        // 測試 headers == null
        when(csvReader.readNext()).thenReturn(null);
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception1.getMessage());

        // 測試 headers.length == 0
        when(csvReader.readNext()).thenReturn(new String[]{});
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception2.getMessage());

        System.out.println("headers 為 null 或空陣列，測試成功");
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
    void testParseCsv_MissingHeaderFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE\n1,IT,Engineer";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        List<String> lines = List.of("ID,DEPARTMENT,JOB_TITLE", "1,IT,Engineer");
        doReturn(lines).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE"})
                .thenReturn(new String[]{"1", "IT", "Engineer"})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案 (缺少欄位: NAME), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("標頭缺少必要欄位，觸發必要欄位檢查，測試成功");
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
    void testParseCsv_MissingDepartment() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,,Engineer,John,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: DEPARTMENT), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少 DEPARTMENT 欄位，測試成功");
    }

    @Test
    void testParseCsv_MissingJobTitle() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,,John,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: JOB_TITLE), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少 JOB_TITLE 欄位，測試成功");
    }

    @Test
    void testParseCsv_MissingName() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: NAME), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少 NAME 欄位，測試成功");
    }

    @Test
    void testParseCsv_MissingTel() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: TEL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少 TEL 欄位，測試成功");
    }

    @Test
    void testParseCsv_MissingEmail() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺少 EMAIL 欄位，測試成功");
    }

    @Test
    void testParseCsv_MissingMultipleFields_NullValues() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n,,Engineer,,12345678,";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"", "", "Engineer", "", "12345678", ""})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID, DEPARTMENT, NAME, EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("多個欄位為 null，測試成功");
    }

    @Test
    void testParseCsv_NullFieldValues() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,,,";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        List<String> lines = List.of("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", "1,IT,Engineer,,,");
        doReturn(lines).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "", "", ""})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: NAME, TEL, EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄位值為 null（index >= fields.length），測試成功");
    }

    @Test
    void testParseCsv_NullFieldValues_AllFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
    
        // 測試 ID
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID), triggerTime=null)", 
                     exception.getMessage());
    
        // 重置 csvReader.readNext() 的行為
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        // 測試 DEPARTMENT
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: DEPARTMENT), triggerTime=null)", 
                     exception.getMessage());
    
        // 重置 csvReader.readNext() 的行為
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        // 測試 JOB_TITLE
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: JOB_TITLE), triggerTime=null)", 
                     exception.getMessage());
    
        // 重置 csvReader.readNext() 的行為
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        // 測試 NAME
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: NAME), triggerTime=null)", 
                     exception.getMessage());
    
        // 重置 csvReader.readNext() 的行為
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        // 測試 TEL
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: TEL), triggerTime=null)", 
                     exception.getMessage());
    
        // 重置 csvReader.readNext() 的行為
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        // 測試 EMAIL
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn(null).when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));
    
        exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: EMAIL), triggerTime=null)", 
                     exception.getMessage());
    
        System.out.println("模擬 getFieldValue 返回 null，觸發所有 dto.getXXX() == null，測試成功");
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
    void testParseCsv_OuterCatch_NullMessageException() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return null;
                    }
                });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: null", exception.getMessage());
        System.out.println("外層 catch 塊處理 message == null，測試成功");
    }

    @Test
    void testParseCsv_NonErrorResponseMessage() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("Some other error message"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: Some other error message", exception.getMessage());
        System.out.println("異常訊息不以 ErrorResponseDto 開頭，測試成功");
    }

    @Test
    void testParseCsv_ErrorResponseException() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("ErrorResponseDto(code=CSV_005, message=Some error, triggerTime=null)"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_005, message=Some error, triggerTime=null)", exception.getMessage());
        System.out.println("異常訊息以 ErrorResponseDto 開頭，測試成功");
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
    void testParseCsv_Success_WithEmptyLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("包含空行（fields.length == 0），測試成功");
    }

    @Test
    void testParseCsv_Success_WithEmptyDataLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{""})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("包含空數據行（fields.length == 1 && fields[0].trim().isEmpty()），測試成功");
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
    void testParseCsv_Success_WithNonEmptySingleFieldLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\nnon-empty\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"non-empty"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("包含單欄非空數據行（fields.length == 1），測試成功");
    }

    @Test
    void testGetFieldValue_NullIndex() throws Exception {
        String[] fields = new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"};
        Map<String, Integer> headerMap = new HashMap<>();
        // 使用小寫鍵，與 getFieldValue 方法的查找方式一致
        headerMap.put("id", 0);
        headerMap.put("department", 1);
        headerMap.put("job_title", 2);
        headerMap.put("tel", 4);
        headerMap.put("email", 5);

        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
            "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true);
        String nameValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "NAME");

        assertNull(nameValue);
        System.out.println("getFieldValue 返回 null（index == null），測試成功");
    }

    @Test
    void testGetFieldValue_IndexOutOfBounds() throws Exception {
        String[] fields = new String[]{"1", "IT", "Engineer"};
        Map<String, Integer> headerMap = new HashMap<>();
        // 使用小寫鍵，與 getFieldValue 方法的查找方式一致
        headerMap.put("id", 0);
        headerMap.put("department", 1);
        headerMap.put("job_title", 2);
        headerMap.put("name", 3);
        headerMap.put("tel", 4);
        headerMap.put("email", 5);

        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
            "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true);
        String nameValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "NAME");
        String telValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "TEL");
        String emailValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "EMAIL");

        assertNull(nameValue);
        assertNull(telValue);
        assertNull(emailValue);
        System.out.println("getFieldValue 返回 null（index >= fields.length），測試成功");
    }

    @Test
    void testGetFieldValue_ValidIndex_Explicit() throws Exception {
        String[] fields = new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"};
        Map<String, Integer> headerMap = new HashMap<>();
        // 使用小寫鍵，與 getFieldValue 方法的查找方式一致
        headerMap.put("id", 0);
        headerMap.put("department", 1);
        headerMap.put("job_title", 2);
        headerMap.put("name", 3);
        headerMap.put("tel", 4);
        headerMap.put("email", 5);

        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
            "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true);
        String idValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "ID");
        String emailValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "EMAIL");

        assertEquals("1", idValue);
        assertEquals("john@example.com", emailValue);
        System.out.println("getFieldValue 返回有效值（index != null && index < fields.length），測試成功");
    }

    // 新增的顯式測試用例，針對未覆蓋的程式碼片段

    @Test
    void testParseCsv_LinesNotEmpty_FirstLineNotEmpty_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", "1,IT,Engineer,John,12345678,john@example.com"))
                .when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("顯式測試：lines 不為空且第一行不為空，測試成功");
    }

    @Test
    void testParseCsv_HeadersNotNull_NotEmpty_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("顯式測試：headers 不為 null 且不為空，測試成功");
    }

    @Test
    void testParseCsv_NonEmptyNonNullFieldValues_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        // 顯式模擬所有欄位不為 null 且不為空
        doReturn("1").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("ID"));
        doReturn("IT").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("DEPARTMENT"));
        doReturn("Engineer").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("JOB_TITLE"));
        doReturn("John").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("NAME"));
        doReturn("12345678").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("TEL"));
        doReturn("john@example.com").when(spyCsvParserUtil).getFieldValue(any(String[].class), any(Map.class), eq("EMAIL"));

        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        assertEquals("IT", dtos.get(0).getDEPARTMENT());
        assertEquals("Engineer", dtos.get(0).getJOB_TITLE());
        assertEquals("John", dtos.get(0).getNAME());
        assertEquals("12345678", dtos.get(0).getTEL());
        assertEquals("john@example.com", dtos.get(0).getEMAIL());
        System.out.println("顯式測試：所有欄位值不為 null 且不為空，測試成功");
    }

    @Test
    void testParseCsv_NonErrorResponseMessage_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("Generic error message"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: Generic error message", exception.getMessage());
        System.out.println("顯式測試：異常訊息不以 ErrorResponseDto 開頭，測試成功");
    }

    @Test
    void testGetFieldValue_ValidIndex_Explicit_Additional() throws Exception {
        String[] fields = new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"};
        Map<String, Integer> headerMap = new HashMap<>();
        // 使用小寫鍵，與 getFieldValue 方法的查找方式一致
        headerMap.put("id", 0);
        headerMap.put("department", 1);
        headerMap.put("job_title", 2);
        headerMap.put("name", 3);
        headerMap.put("tel", 4);
        headerMap.put("email", 5);

        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
                "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true);
        String idValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "ID");
        String departmentValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "DEPARTMENT");

        assertEquals("1", idValue);
        assertEquals("IT", departmentValue);
        System.out.println("顯式測試：getFieldValue 返回有效值（index != null && index < fields.length），測試成功");
    }
}
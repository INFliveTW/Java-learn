package cdf.training.svc.datatransfer.util;

import java.io.StringReader;
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
import static org.mockito.Mockito.doThrow;
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
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(null);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)",
                     exception.getMessage());
        System.out.println("csvContent 為 null，觸發 lines.isEmpty()，測試成功");
    }
    

    @Test
    void testParseCsv_EmptyContent() {
        Exception exception1 = assertThrows(RuntimeException.class, () -> csvParserUtil.parseCsv(""));
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception1.getMessage());
    
        Exception exception2 = assertThrows(RuntimeException.class, () -> csvParserUtil.parseCsv("\n"));
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception2.getMessage());
    
        System.out.println("CSV 內容為空或僅換行，觸發 lines.isEmpty()，測試成功");
    }
    
    
    @Test
    void testParseCsv_FirstLineEmpty_Trimmed_Explicit() {
        String csvContent = "   \n1,IT,Engineer,John,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("第一行為空白字串（trim 後為空），測試成功");
    }
    

    @Test
    void testParseCsv_LinesNotEmpty_FirstLineNotEmpty_Explicit() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("顯式測試：lines 不為空且第一行不為空，測試成功");
    }

    @Test
    void testParseCsv_LinesNotEmpty_FirstLineNotEmpty_Explicit_Additional() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n2,HR,Manager,Jane,87654321,jane@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(2, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        assertEquals("2", dtos.get(1).getID());
        System.out.println("顯式測試：lines 不為空且第一行不為空（多行數據），測試成功");
    }

    @Test
    void testParseCsv_EmptyOrNullHeaders_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext()).thenReturn(null);
        Exception exception1 = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception1.getMessage());
    
        System.out.println("headers 為 null，測試成功");
    }
    

    @Test
    void testParseCsv_HeadersNotNull_NotEmpty_Explicit() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("顯式測試：headers 不為 null 且不為空，測試成功");
    }

    @Test
    void testParseCsv_HeadersNotNull_NotEmpty_Explicit_Additional() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n2,HR,Manager,Jane,87654321,jane@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(2, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        assertEquals("2", dtos.get(1).getID());
        System.out.println("顯式測試：headers 不為 null 且不為空（多行數據），測試成功");
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
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取標頭: Failed to read headers, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("讀取標頭失敗，測試成功");
    }
    
    

    @Test
    void testParseCsv_MissingHeaderFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE\n1,IT,Engineer";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
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
    void testParseCsv_TooFewFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678"})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確 (第 1 行欄數不一致: 5 < 6), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄數過少，觸發 fields.length < headers.length，測試成功");
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
    void testParseCsv_NonEmptyNonNullFieldValues_Explicit() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
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
    void testParseCsv_NonEmptyNonNullFieldValues_Explicit_Additional() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n2,HR,Manager,Jane,87654321,jane@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(2, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        assertEquals("IT", dtos.get(0).getDEPARTMENT());
        assertEquals("Engineer", dtos.get(0).getJOB_TITLE());
        assertEquals("John", dtos.get(0).getNAME());
        assertEquals("12345678", dtos.get(0).getTEL());
        assertEquals("john@example.com", dtos.get(0).getEMAIL());
        assertEquals("2", dtos.get(1).getID());
        assertEquals("HR", dtos.get(1).getDEPARTMENT());
        assertEquals("Manager", dtos.get(1).getJOB_TITLE());
        assertEquals("Jane", dtos.get(1).getNAME());
        assertEquals("87654321", dtos.get(1).getTEL());
        assertEquals("jane@example.com", dtos.get(1).getEMAIL());
        System.out.println("顯式測試：所有欄位值不為 null 且不為空（多行數據），測試成功");
    }

    @Test
    void testParseCsv_TrimmedEmptyFieldValues() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n ,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{" ", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄位值為空格字串（trim 後為空），觸發 dto.getXXX().trim().isEmpty()，測試成功");
    }

    @Test
    void testParseCsv_TrimmedEmptyFieldValues_MultipleFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n , , ,John, , ";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{" ", " ", " ", "John", " ", " "})
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID, DEPARTMENT, JOB_TITLE, TEL, EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("多個欄位值為空格字串（trim 後為空），觸發多個 dto.getXXX().trim().isEmpty()，測試成功");
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
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: Failed to read data, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("讀取資料失敗，測試成功");
    }
    

    @Test
    void testParseCsv_InnerCatch_NullMessageException_MultiLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n2,HR,Manager,Jane,87654321,jane@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenThrow(new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return null;
                    }
                });
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 2 行: null, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("內層 catch 塊處理 message == null（多行數據，lineNumber > 0），測試成功");
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
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: null, triggerTime=null)", 
                     exception.getMessage());
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
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: Some other error message, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("異常訊息不以 ErrorResponseDto 開頭，測試成功");
    }
    

    @Test
    void testParseCsv_NonErrorResponseMessage_Explicit_Additional() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("Another error message"));
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: Another error message, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("顯式測試：異常訊息不以 ErrorResponseDto 開頭（另一種訊息），測試成功");
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
    
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: ErrorResponseDto(code=CSV_005, message=Some error, triggerTime=null), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("異常訊息為 ErrorResponseDto 格式，被包裹為 CSV_001，測試成功");
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

    @Test
    void testGetFieldValue_ValidIndex_Explicit_Additional() throws Exception {
        String[] fields = new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"};
        Map<String, Integer> headerMap = new HashMap<>();
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

    @Test
    void testParseCsv_HeadersNull_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext()).thenReturn(null);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("headers 為 null，測試成功");
    }
    
    
    @Test
    void testParseCsv_HeadersEmpty_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext()).thenReturn(new String[]{});
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("headers.length 為 0，測試成功");
    }
    

    @Test
    void testParseCsv_OuterCatch_NullMessageException_Explicit() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doThrow(new RuntimeException() {
            @Override
            public String getMessage() {
                return null;
            }
        }).when(spyCsvParserUtil).createCSVReader(anyString());
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 未知原因, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("外層 catch 塊處理 message == null，測試成功");
    }
    

    @Test
    void testSplitCsvContentIntoLines() throws Exception {
        Method splitCsvContentIntoLinesMethod = CSVParserUtil.class.getDeclaredMethod("splitCsvContentIntoLines", String.class);
        splitCsvContentIntoLinesMethod.setAccessible(true);
    
        // 測試 1：單行 CSV
        String singleLineCsv = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";
        List<String> lines1 = (List<String>) splitCsvContentIntoLinesMethod.invoke(csvParserUtil, singleLineCsv);
        assertEquals(1, lines1.size());
        assertEquals("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", lines1.get(0));
    
        // 測試 2：多行 CSV
        String multiLineCsv = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        List<String> lines2 = (List<String>) splitCsvContentIntoLinesMethod.invoke(csvParserUtil, multiLineCsv);
        assertEquals(2, lines2.size());
        assertEquals("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", lines2.get(0));
        assertEquals("1,IT,Engineer,John,12345678,john@example.com", lines2.get(1));
    
        // 測試 3：空行 CSV
        String emptyLineCsv = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        List<String> lines3 = (List<String>) splitCsvContentIntoLinesMethod.invoke(csvParserUtil, emptyLineCsv);
        assertEquals(3, lines3.size());
        assertEquals("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL", lines3.get(0));
        assertEquals("", lines3.get(1));
        assertEquals("1,IT,Engineer,John,12345678,john@example.com", lines3.get(2));
    
        // 測試 4：空內容
        String emptyCsv = "";
        List<String> lines4 = (List<String>) splitCsvContentIntoLinesMethod.invoke(csvParserUtil, emptyCsv);
        assertEquals(0, lines4.size());
    
        // 測試 5：僅換行
        String newlineCsv = "\n";
        List<String> lines5 = (List<String>) splitCsvContentIntoLinesMethod.invoke(csvParserUtil, newlineCsv);
        assertEquals(0, lines5.size());
    
        System.out.println("splitCsvContentIntoLines 方法測試成功");
    }

    @Test
    void testSplitCsvContentIntoLines_NullContent() {
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        List<String> result = spyCsvParserUtil.splitCsvContentIntoLines(null);
        assertEquals(0, result.size(), "當 csvContent 為 null 時，應返回空列表");
        System.out.println("splitCsvContentIntoLines 為 null，測試成功");
    }

    /**
     * 測試 splitCsvContentIntoLines 方法當 csvContent 為空字串時返回空列表
     */
    @Test
    void testSplitCsvContentIntoLines_EmptyContent() {
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        List<String> result = spyCsvParserUtil.splitCsvContentIntoLines("");
        assertEquals(0, result.size(), "當 csvContent 為空字串時，應返回空列表");
        System.out.println("splitCsvContentIntoLines 為空字串，測試成功");
    }

    /**
     * 測試 parseCsv 方法當 splitCsvContentIntoLines 返回空列表時拋出異常
     */
    @Test
    void testParseCsv_LinesEmpty() {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        
        // 模擬 splitCsvContentIntoLines 返回空列表
        doReturn(List.of()).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("parseCsv lines 為空，測試成功");
    }
    
    @Test
    void testParseCsv_WrappedExceptionWhenMessageNotStartsWithErrorResponseDto() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("非標準錯誤格式"));
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
    
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: 非標準錯誤格式, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("已捕捉非 ErrorResponseDto 錯誤訊息並進行包裝，測試成功");
    }

    @Test
    void testParseCsv_WrappedExceptionWhenMessageIsNull() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return null; // 模擬 null message
                    }
                });
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
    
        assertEquals(
            "ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: null, triggerTime=null)",
            exception.getMessage()
        );
        System.out.println("異常訊息為 null 時，補上 '未知原因'，測試成功");
    }

    @Test
    void testParseCsv_ErrorResponseMessageDirectThrow() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
    
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException("ErrorResponseDto(code=CSV_999, message=錯誤訊息, triggerTime=null)"));
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
    
        assertEquals(
            "ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 無法讀取第 1 行: ErrorResponseDto(code=CSV_999, message=錯誤訊息, triggerTime=null), triggerTime=null)",
            exception.getMessage()
        );
        System.out.println("已直接捕捉 ErrorResponseDto 格式錯誤訊息，測試成功");
    }
    @Test
    void testOuterCatch_GenericErrorMessage_WrappedAsDto() {
        String csvContent = "INVALID_CSV_WITH;SEMICOLONS";
    
        // 這會觸發 parseCsv 的 ; 分隔符錯誤 → 丟出 RuntimeException（非 ErrorResponseDto）
        Exception ex = assertThrows(RuntimeException.class, () -> csvParserUtil.parseCsv(csvContent));
    
        assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 不合法分隔符 (使用 ; 而非 ,), triggerTime=null)",
                     ex.getMessage());
        System.out.println("✅ 外層 catch 包裝非 ErrorResponseDto 訊息成功");
    }

@Test
void testOuterCatch_MessageStartsWithErrorResponseDto() {
    String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";

    // 模擬 header 為 null，觸發 ErrorResponseDto 字串訊息
    CSVParserUtil spyUtil = spy(csvParserUtil);
    doReturn(new CSVReader(new StringReader(csvContent)) {
        @Override
        public String[] readNext() {
            return null; // headers == null → 觸發 CSV_EMPTY_ERROR
        }
    }).when(spyUtil).createCSVReader(anyString());

    Exception ex = assertThrows(RuntimeException.class, () -> spyUtil.parseCsv(csvContent));

    // 應為原始的 ErrorResponseDto 字串，沒有被包裝
    assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)",
                 ex.getMessage());
    System.out.println("✅ 外層 catch passthrough ErrorResponseDto 訊息成功");
}

@Test
void testOuterCatch_MessageIsNull_FallbackToUnknown() {
    String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";

    CSVParserUtil spyUtil = spy(csvParserUtil);

    // 讓 createCSVReader 本身拋出異常，才會落到 outer catch，而不是 inner try-catch
    doThrow(new RuntimeException() {
        @Override
        public String getMessage() {
            return null; // 模擬 null message
        }
    }).when(spyUtil).createCSVReader(anyString());

    Exception ex = assertThrows(RuntimeException.class, () -> spyUtil.parseCsv(csvContent));

    assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 未知原因, triggerTime=null)",
                 ex.getMessage());
    System.out.println("✅ 外層 catch 處理 null message fallback 成功");
}

@Test
void testOuterCatchWithNullMessageOnly() {
    String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";

    CSVParserUtil spyUtil = spy(csvParserUtil);

    // 模擬 createCSVReader 直接拋出 Exception，跳過 inner try 塊
    doThrow(new RuntimeException() {
        @Override
        public String getMessage() {
            return null; // 確保是 null message
        }
    }).when(spyUtil).createCSVReader(anyString());

    Exception exception = assertThrows(RuntimeException.class, () -> spyUtil.parseCsv(csvContent));

    assertEquals("ErrorResponseDto(code=CSV_001, message=CSV 檔案解析失敗，請確認檔案格式正確: 未知原因, triggerTime=null)", exception.getMessage());
    System.out.println("✅ 外層 catch message 為 null，fallback to '未知原因' 成功");
}

@Test
void testOuterCatch_ErrorResponseDtoMessage_RethrowDirectly() {
    String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL";

    CSVParserUtil spyUtil = spy(csvParserUtil);

    doThrow(new RuntimeException("ErrorResponseDto(code=CSV_999, message=外層捕捉用, triggerTime=null)"))
        .when(spyUtil).createCSVReader(anyString());

    Exception ex = assertThrows(RuntimeException.class, () -> spyUtil.parseCsv(csvContent));

    assertEquals("ErrorResponseDto(code=CSV_999, message=外層捕捉用, triggerTime=null)", ex.getMessage());
    System.out.println("✅ 外層 catch 拋出原始 ErrorResponseDto 格式訊息，測試成功");
}


}
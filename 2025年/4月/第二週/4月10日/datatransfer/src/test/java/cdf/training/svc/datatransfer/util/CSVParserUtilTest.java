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
    void testParseCsv_FirstLineEmpty() throws Exception {
        String csvContent = "\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of("")).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("第一行為空，測試成功");
    }

    @Test
    void testParseCsv_EmptyHeaders() throws Exception {
        String csvContent = "";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext()).thenReturn(null); // 模擬 headers == null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("headers 為 null，測試成功");
    }

    @Test
    void testParseCsv_EmptyHeadersArray() throws Exception {
        String csvContent = "";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext()).thenReturn(new String[]{}); // 模擬 headers.length == 0

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("headers 為空陣列，測試成功");
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
    void testParseCsv_NullFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "", "john@example.com"})
                .thenReturn(null);
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: TEL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("欄位值為空字串，測試成功");
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
    void testParseCsv_Success_WithEmptyLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{}) // 模擬 fields.length == 0
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
                .thenReturn(new String[]{""}) // 模擬 fields.length == 1 && fields[0].trim().isEmpty()
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
    void testParseCsv_NullMessageException() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenThrow(new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return null; // 模擬 message == null
                    }
                });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("Failed to parse CSV: Failed to read CSV data at line 1: null", exception.getMessage());
        System.out.println("異常訊息為 null，測試成功");
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
        System.out.println("getFieldValue 返回 null（index < fields.length 為 false），測試成功");
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
    void testParseCsv_MissingMultipleFields() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n,,Engineer,,12345678,";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_004, message=CSV檔案，資料缺少，請確認檔案 (第 1 行缺少欄位: ID, DEPARTMENT, NAME, EMAIL), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("缺失多個欄位，測試成功");
    }

    @Test
    void testParseCsv_EmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> csvParserUtil.parseCsv(""));
        assertThrows(IllegalArgumentException.class, () -> csvParserUtil.parseCsv("\n"));
        System.out.println("CSV 內容為空或第一行為空，測試成功");
    }

    @Test
    void testParseCsv_NoHeaders() throws Exception {
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of("")).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
        assertThrows(IllegalArgumentException.class, () -> spyCsvParserUtil.parseCsv(""));
        System.out.println("CSV 無標頭，測試成功");
    }

    @Test
    void testParseCsv_EmptyLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n\n1,IT,Engineer,John,12345678,john@example.com";
        List<EmployeeDataCSVDto> dtos = csvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        System.out.println("CSV 包含空行，測試成功");
    }

    @Test
    void testGetFieldValue_NullIndex() throws Exception {
        // 模擬 fields 陣列和 headerMap
        String[] fields = new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"};
        Map<String, Integer> headerMap = new HashMap<>();
        // 不設置 NAME 的索引，模擬 index == null
        headerMap.put("ID", 0);
        headerMap.put("DEPARTMENT", 1);
        headerMap.put("JOB_TITLE", 2);
        headerMap.put("TEL", 4);
        headerMap.put("EMAIL", 5);
    
        // 使用反射調用 private 方法 getFieldValue
        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
            "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true); // 繞過存取權限
        String nameValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "NAME");
    
        // 驗證結果
        assertNull(nameValue); // NAME 欄位應為 null，因為 index == null
    
        System.out.println("getFieldValue 返回 null（index == null），測試成功");
    }

@Test
    void testGetFieldValue_IndexOutOfBounds() throws Exception {
        // 模擬 fields 陣列和 headerMap
        String[] fields = new String[]{"1", "IT", "Engineer"}; // 模擬缺少 NAME, TEL, EMAIL
        Map<String, Integer> headerMap = new HashMap<>();
        headerMap.put("ID", 0);
        headerMap.put("DEPARTMENT", 1);
        headerMap.put("JOB_TITLE", 2);
        headerMap.put("NAME", 3); // index 3 >= fields.length
        headerMap.put("TEL", 4);  // index 4 >= fields.length
        headerMap.put("EMAIL", 5); // index 5 >= fields.length

        // 使用反射調用 private 方法 getFieldValue
        Method getFieldValueMethod = CSVParserUtil.class.getDeclaredMethod(
            "getFieldValue", String[].class, Map.class, String.class);
        getFieldValueMethod.setAccessible(true); // 繞過存取權限
        String nameValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "NAME");
        String telValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "TEL");
        String emailValue = (String) getFieldValueMethod.invoke(csvParserUtil, fields, headerMap, "EMAIL");

        // 驗證結果
        assertNull(nameValue);  // NAME 欄位應為 null，因為索引超出範圍
        assertNull(telValue);   // TEL 欄位應為 null，因為索引超出範圍
        assertNull(emailValue); // EMAIL 欄位應為 null，因為索引超出範圍

        System.out.println("getFieldValue 返回 null（index >= fields.length），測試成功");
    }

    @Test
    void testParseCsv_FirstLineEmpty_Explicit() throws Exception {
        String csvContent = "   \n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(List.of("   ")).when(spyCsvParserUtil).splitCsvContentIntoLines(anyString());
    
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCsvParserUtil.parseCsv(csvContent);
        });
        assertEquals("CSV 內容為空", exception.getMessage());
        System.out.println("第一行為空白字串，測試成功");
    }

    @Test
    void testParseCsv_MissingHeaderField() throws Exception {
        String csvContent = "DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\nIT,Engineer,John,12345678,john@example.com";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            csvParserUtil.parseCsv(csvContent);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案 (缺少欄位: ID), triggerTime=null)", 
                     exception.getMessage());
        System.out.println("標頭缺少必要欄位 (ID)，測試成功");
    }

    @Test
    void testParseCsv_Success_WithNonEmptySingleFieldLine() throws Exception {
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\nnon-empty\n1,IT,Engineer,John,12345678,john@example.com";
        CSVParserUtil spyCsvParserUtil = spy(csvParserUtil);
        doReturn(csvReader).when(spyCsvParserUtil).createCSVReader(anyString());
        when(csvReader.readNext())
                .thenReturn(new String[]{"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"})
                .thenReturn(new String[]{"non-empty"}) // fields.length == 1，應被跳過
                .thenReturn(new String[]{"1", "IT", "Engineer", "John", "12345678", "john@example.com"})
                .thenReturn(null);
    
        List<EmployeeDataCSVDto> dtos = spyCsvParserUtil.parseCsv(csvContent);
        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getID());
        System.out.println("包含單欄非空數據行（fields.length == 1），測試成功");
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
}
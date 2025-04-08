package cdf.training.svc.datatransfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cdf.training.svc.datatransfer.dto.BaseResponse;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.Metadata;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;

class EmployeeDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CSVToDataBaseServiceImpl csvToDataBaseService;

    @InjectMocks
    private EmployeeDataController employeeDataController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeDataController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessEmployeeData_Success() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto)).thenReturn(true);

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(true))
                .andExpect(jsonPath("$.metadata.errorCode").doesNotExist())
                .andExpect(jsonPath("$.metadata.errorDesc").doesNotExist())
                .andExpect(jsonPath("$.data").value("資料處理成功"));

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，測試成功");
    }

    @Test
    void testProcessEmployeeData_ServiceReturnsFalse() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto)).thenReturn(false);

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("UNKNOWN_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("發生未知錯誤"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，service 返回 false，測試成功");
    }

    @Test
    void testProcessEmployeeData_BusinessException() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SFTP_001, message=SFTP 伺服器拒絕訪問，請檢查權限, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("SFTP_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("SFTP 伺服器拒絕訪問，請檢查權限"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API業務錯誤，測試成功");
    }

    @Test
    void testProcessEmployeeData_BusinessException_ParsingFailure() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SFTP_001)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("UNKNOWN_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("解析錯誤: Index 1 out of bounds for length 1"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API業務錯誤，解析失敗，測試成功");
    }

    @Test
    void testProcessEmployeeData_SystemException() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException("未知系統錯誤"));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("UNKNOWN_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("發生未知錯誤: 未知系統錯誤"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API系統錯誤，測試成功");
    }

    @Test
    void testProcessEmployeeData_SystemException_NullMessage() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException((String) null));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("UNKNOWN_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("發生未知錯誤: 未知原因"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API系統錯誤，異常訊息為 null，測試成功");
    }

    @Test
    void testProcessEmployeeData_SftpFileNotFound() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SFTP_002, message=SFTP 檔案不存在, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("SFTP_002"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("SFTP 檔案不存在"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，SFTP 檔案不存在，測試成功");
    }

    @Test
    void testProcessEmployeeData_SftpConnectionErrorWithNullMessage() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SFTP_001, message=SFTP 連線失敗，請確認連線參數正確, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("SFTP_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("SFTP 連線失敗，請確認連線參數正確"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，SFTP 連線失敗且異常訊息為 null，測試成功");
    }

    @Test
    void testProcessEmployeeData_CsvEmptyError() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=CSV_002, message=CSV 檔案無有效資料, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("CSV_002"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("CSV 檔案無有效資料"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，CSV 檔案無有效資料，測試成功");
    }

    @Test
    void testProcessEmployeeData_NullCompanyAndExcutetime() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        // 不設置 COMPANY 和 EXCUTETIME，讓其為 null

        when(csvToDataBaseService.processCsvToDatabase(requestDto)).thenReturn(true);

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(true))
                .andExpect(jsonPath("$.metadata.errorCode").doesNotExist())
                .andExpect(jsonPath("$.metadata.errorDesc").doesNotExist())
                .andExpect(jsonPath("$.data").value("資料處理成功"));

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，COMPANY 和 EXCUTETIME 為 null，測試成功");
    }

    @Test
    void testProcessEmployeeData_SqlConnectionError() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SQL_001, message=資料庫連線失敗，請檢查連線參數, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("SQL_001"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("資料庫連線失敗，請檢查連線參數"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，資料庫連線失敗，測試成功");
    }

    @Test
    void testProcessEmployeeData_SqlWriteError() throws Exception {
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料格式或主鍵是否重複, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.status").value(false))
                .andExpect(jsonPath("$.metadata.errorCode").value("SQL_002"))
                .andExpect(jsonPath("$.metadata.errorDesc").value("資料庫寫入失敗，請檢查資料格式或主鍵是否重複"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("觸發API，資料庫寫入失敗，測試成功");
    }

    @Test
    void testBaseResponseAndMetadata() {
        BaseResponse response = new BaseResponse("test data");
        Metadata metadata = response.getMetadata();

        metadata.setStatus(false);
        metadata.setErrorCode("TEST_001");
        metadata.setErrorDesc("Test error");

        assertEquals(false, metadata.getStatus());
        assertEquals("TEST_001", metadata.getErrorCode());
        assertEquals("Test error", metadata.getErrorDesc());

        response.setData("new data");
        assertEquals("new data", response.getData());

        Metadata errorMetadata = new Metadata("ERROR_001", "Error occurred");
        assertEquals(false, errorMetadata.getStatus());
        assertEquals("ERROR_001", errorMetadata.getErrorCode());
        assertEquals("Error occurred", errorMetadata.getErrorDesc());

        System.out.println("BaseResponse 和 Metadata getter 和 setter 測試成功");
    }
}
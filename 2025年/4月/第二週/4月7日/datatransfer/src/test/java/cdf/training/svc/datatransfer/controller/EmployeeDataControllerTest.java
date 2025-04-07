package cdf.training.svc.datatransfer.controller;

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

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
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
        //測試資料寫入成功
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto)).thenReturn(true);

        //測試資料寫入成功
        mockMvc.perform(post("/employee-data") // 修正路徑
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
    void testProcessEmployeeData_BusinessException() throws Exception {
        //測試資料寫入成功
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        String errorMessage = "ErrorResponseDto(code=SFTP_001, message=SFTP 伺服器拒絕訪問，請檢查權限, triggerTime=null)";
        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException(errorMessage));

        //測試資料寫入成功
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
    void testProcessEmployeeData_SystemException() throws Exception {
        //測試資料寫入成功
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        requestDto.setCOMPANY("金控");
        requestDto.setEXCUTETIME("2025-03-20 15:30:45");

        when(csvToDataBaseService.processCsvToDatabase(requestDto))
                .thenThrow(new RuntimeException("未知系統錯誤"));

        //測試資料寫入成功
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
}
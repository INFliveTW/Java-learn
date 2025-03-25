package cdf.training.svc.datatransfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseResponseDto;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;
//測試EmployeeDataController
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
        // Arrange
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();
        CSVToDataBaseResponseDto responseDto = new CSVToDataBaseResponseDto("資料處理成功");

        doNothing().when(csvToDataBaseService).processCsvToDatabase(requestDto);

        // Act & Assert
        mockMvc.perform(post("/api/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        
        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessEmployeeData_Exception() throws Exception {
        // Arrange
        CSVToDataBaseRequestDto requestDto = new CSVToDataBaseRequestDto();

        doThrow(new RuntimeException("處理失敗")).when(csvToDataBaseService).processCsvToDatabase(requestDto);

        // Act & Assert
        mockMvc.perform(post("/api/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
        
        verify(csvToDataBaseService, times(1)).processCsvToDatabase(requestDto);
        System.out.println("測試成功"); // 測試通過時顯示
    }
}
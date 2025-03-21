package cdf.training.svc.datatransfer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeDataControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CSVToDataBaseServiceImpl csvToDataBaseService;

    @Test
    void testProcessEmployeeData_Success() {
        // Arrange
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 12:00:00");

        doNothing().when(csvToDataBaseService).processCsvToDatabase(any(CSVToDataBaseRequestDto.class));

        // Act & Assert
        webTestClient.post()
                .uri("/api/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Data processed successfully");
    }

    @Test
    void testProcessEmployeeData_InvalidRequest() {
        // Arrange - 無效的請求（空物件）
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        doNothing().when(csvToDataBaseService).processCsvToDatabase(any(CSVToDataBaseRequestDto.class));

        // Act & Assert
        webTestClient.post()
                .uri("/api/employee-data")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()  // 即使請求為空，服務仍應成功處理
                .expectBody()
                .jsonPath("$.message").isEqualTo("Data processed successfully");
    }
}
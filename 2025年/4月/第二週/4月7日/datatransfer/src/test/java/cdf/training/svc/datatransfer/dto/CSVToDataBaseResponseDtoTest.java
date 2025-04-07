package cdf.training.svc.datatransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class CSVToDataBaseResponseDtoTest {

    @Test
    void testCSVToDataBaseResponseDto_ConstructorAndGetters() {
        // 測試建構子和 getter 方法
        String message = "Test message";
        CSVToDataBaseResponseDto responseDto = new CSVToDataBaseResponseDto(message);

        assertEquals(message, responseDto.getMessage(), "Message should match the constructor input");
        assertNull(responseDto.getTriggerTime(), "TriggerTime should be null by default");
        System.out.println("建構子和 getter 方法測試成功");
    }

    @Test
    void testCSVToDataBaseResponseDto_Setters() {
        // 測試 setter 方法
        CSVToDataBaseResponseDto responseDto = new CSVToDataBaseResponseDto("Initial message");

        String newMessage = "Updated message";
        String triggerTime = "2025-03-20 15:30:45";
        responseDto.setMessage(newMessage);
        responseDto.setTriggerTime(triggerTime);

        assertEquals(newMessage, responseDto.getMessage(), "Message should be updated");
        assertEquals(triggerTime, responseDto.getTriggerTime(), "TriggerTime should be updated");
        System.out.println("setter 方法測試成功");
    }
}
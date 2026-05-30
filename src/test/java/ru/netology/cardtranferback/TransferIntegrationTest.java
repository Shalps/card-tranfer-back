package ru.netology.cardtranferback;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты API")
class TransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Успешная инициация перевода")
    void shouldInitTransfer() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "cardFromNumber": "5469123456781234",
                      "cardFromValidTill": "12/25",
                      "cardFromCVV": "123",
                      "cardToNumber": "5469876543215678",
                      "amount": {"value": 100, "currency": "RUR"}
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").isNotEmpty());
    }

    @Test
    @DisplayName("Отклонение невалидных данных")
    void shouldRejectInvalidData() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "cardFromNumber": "123",
                      "cardFromValidTill": "13/99",
                      "cardFromCVV": "12",
                      "cardToNumber": "5469876543215678",
                      "amount": {"value": -50, "currency": "RUR"}
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Полный цикл перевода")
    void shouldCompleteTransferCycle() throws Exception {
        String operationId = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "cardFromNumber": "5469123456781234",
                      "cardFromValidTill": "12/25",
                      "cardFromCVV": "123",
                      "cardToNumber": "5469876543215678",
                      "amount": {"value": 100, "currency": "RUR"}
                    }
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"operationId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "operationId": "%s",
                      "code": "0000"
                    }
                    """.formatted(operationId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(operationId));
    }

    @Test
    @DisplayName("Неверный код подтверждения")
    void shouldRejectWrongCode() throws Exception {
        String operationId = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "cardFromNumber": "5469123456781234",
                      "cardFromValidTill": "12/25",
                      "cardFromCVV": "123",
                      "cardToNumber": "5469876543215678",
                      "amount": {"value": 100, "currency": "RUR"}
                    }
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"operationId\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "operationId": "%s",
                      "code": "000000"
                    }
                    """.formatted(operationId)))
                .andExpect(status().isBadRequest());
    }
}
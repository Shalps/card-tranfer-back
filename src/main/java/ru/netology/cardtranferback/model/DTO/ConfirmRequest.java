package ru.netology.cardtranferback.model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ConfirmRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String operationId;
}

package ru.netology.cardtranferback.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.cardtranferback.model.Amount;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransferRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardFromNumber;

    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Срок действия в формате ММ/ГГ")
    private String cardFromValidTill;

    @NotBlank
    @Pattern(regexp = "\\d{3}", message = "CVC должен содержать 3 цифры")
    private String cardFromCVV;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardToNumber;
    @NotNull
    Amount amount;


}


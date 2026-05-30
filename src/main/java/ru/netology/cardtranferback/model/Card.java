package ru.netology.cardtranferback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Card {
    private String id;
    private String number;
    private String date;
    private String cvc;
    private Long balance;

    public String getMAskedNumber(){
        if (number==null || number.length()!=16){
            return "****";
        }
        return number.substring(0,4) + "****"+ number.substring(12);

    }
}

package ru.netology.cardtranferback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Amount {
    private Double value;
    private String currency;
    public long toKopecks(){
        if (value!=null){
            return Math.round(value*100);
        }
        else return 0L;
    }
}

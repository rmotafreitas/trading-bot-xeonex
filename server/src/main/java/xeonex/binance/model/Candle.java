package xeonex.binance.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.math.BigDecimal;



@NoArgsConstructor
@Getter
@Setter

public class Candle {

    private Long openTime;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;

    private Long closeTime;



    public Candle(Long openTime, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice, BigDecimal volume, Long closeTime) {
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.closeTime = closeTime;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

}

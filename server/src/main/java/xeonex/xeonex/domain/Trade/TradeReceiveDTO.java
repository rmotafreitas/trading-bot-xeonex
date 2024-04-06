package xeonex.xeonex.domain.Trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xeonex.xeonex.domain.User.Currency;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradeReceiveDTO {

    private Integer risk;
    private BigDecimal spread;
    private String asset;
    private String window_money;

    private BigDecimal initialInvestment;

    private BigDecimal takeProfit;

    private BigDecimal stopLoss;
    private BigDecimal assetprice;





}

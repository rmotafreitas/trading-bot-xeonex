package xeonex.xeonex.domain.Trade;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import xeonex.xeonex.domain.User.Risk;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.repositories.TradeLogRepository;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity(name = "trade")
@Table(name = "trade")

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "initial_investment")
    private BigDecimal initialInvestment;

    @Column(name = "asset")
    private String asset;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Embedded
    @Column(name = "risk")
    private Risk risk;

    @Column(name = "TakeProfit")
    private BigDecimal TakeProfit;


    @Column(name = "StopLoss")
    private BigDecimal StopLoss;

    @Column(name = "TradeType")
    private String TradeType;

    @Column(name = "TradeStatus")
    private String TradeStatus;

    @ManyToOne
    private User user;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "quantity_asset", precision = 10, scale = 10)
    private BigDecimal quantityAsset;

    @Column(name = "window_money")
    private String windowMoney;

    @Column(name = "value_without_spread")
    private BigDecimal valueWithoutSpread;

    public Trade(BigDecimal initialInvestment, String asset, BigDecimal currentBalance, Risk risk, BigDecimal takeProfit, BigDecimal stopLoss, String tradeType, String tradeStatus, User user,BigDecimal quantityAsset,String windowMoney,BigDecimal valueWithoutSpread) {
        this.initialInvestment = initialInvestment;
        this.asset = asset;
        this.currentBalance = currentBalance;
        this.risk = risk;
        this.TakeProfit = takeProfit;
        this.StopLoss = stopLoss;
        this.TradeType = tradeType;
        this.TradeStatus = tradeStatus;
        this.user = user;
        this.date = LocalDateTime.now();
        this.quantityAsset = quantityAsset;
        this.windowMoney = windowMoney;
        this.valueWithoutSpread = valueWithoutSpread;
    }

    //Penso rápido : eu devia usar ASPECT, mas não estou a conseguir e não vou perder mais tempo, vou fazer assim

    public void setTradeStatus(String tradeStatus) {

        TradeStatus = tradeStatus;
    }

}

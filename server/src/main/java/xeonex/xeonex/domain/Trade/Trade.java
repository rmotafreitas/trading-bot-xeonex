package xeonex.xeonex.domain.Trade;


import jakarta.persistence.*;
import lombok.*;
import xeonex.xeonex.domain.User.Currency;
import xeonex.xeonex.domain.User.Risk;
import xeonex.xeonex.domain.User.User;

import java.math.BigDecimal;

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
    private Currency asset;

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

    @OneToOne
    private User user;

}

package xeonex.xeonex.domain.Trade;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "trade_log")
@Table(name = "trade_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Trade trade;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "action")
    private String action;

    @Column(name = "current_value")
    private String value;

    @Column(name = "msg", columnDefinition = "TEXT")
    private String explanation;

    public TradeLog(Trade trade, String action,String value) {
        this.trade = trade;
        this.action = action;
        this.date = LocalDateTime.now();
        this.value = value;
        this.explanation = "";
    }


    public TradeLog(Trade t, String action, String value, String msg) {
        this.trade = t;
        this.action = action;
        this.date = LocalDateTime.now();
        this.value = value;
        this.explanation = msg;
    }
}

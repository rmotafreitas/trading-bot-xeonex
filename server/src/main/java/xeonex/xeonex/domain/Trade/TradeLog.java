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

    public TradeLog(Trade trade, String action) {
        this.trade = trade;
        this.action = action;
        this.date = LocalDateTime.now();
    }


}

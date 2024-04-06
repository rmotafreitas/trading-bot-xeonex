package xeonex.xeonex.domain.Trade;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    private Trade trade;

    @Column(name = "date")
    private Date date;

    @Column(name = "action")
    private String action;


}

package xeonex.xeonex.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xeonex.xeonex.domain.Trade.Trade;
import xeonex.xeonex.domain.Trade.TradeLog;

import java.util.List;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, String> {



    List<TradeLog> findByTradeOrderByDateAsc(Trade trade);
}

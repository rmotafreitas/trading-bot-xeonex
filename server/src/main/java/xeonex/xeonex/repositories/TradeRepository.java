package xeonex.xeonex.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xeonex.xeonex.domain.Trade.Trade;

import xeonex.xeonex.domain.User.User;

import java.util.List;

@Repository
public interface TradeRepository  extends JpaRepository<Trade, String> {

    List<Trade> findTradesByUser(User user);

    Trade findTradyById(String id);

    List<Trade> findByUserOrderById(User user);
}

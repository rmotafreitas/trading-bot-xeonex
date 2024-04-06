package xeonex.xeonex.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import xeonex.xeonex.domain.Trade.Trade;

import xeonex.xeonex.domain.User.User;

import java.util.List;


public interface TradeRepository  extends JpaRepository<Trade, String> {

    List<Trade> findTradesByUser(User user);

    Trade findTradyById(String id);

}

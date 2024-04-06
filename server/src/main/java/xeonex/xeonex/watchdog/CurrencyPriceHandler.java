package xeonex.xeonex.watchdog;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import xeonex.xeonex.controller.CoinInfoController;
import xeonex.xeonex.domain.Trade.Trade;
import xeonex.xeonex.repositories.TradeRepository;

//Watchdog
@Component
public class CurrencyPriceHandler {

    @Autowired
    private TradeRepository tradeRepository;

    @Scheduled(fixedRate = 1000)
    public void handleCurrencyPrice() {

            for(Trade t: tradeRepository.findAll()){

               // System.out.println(t);

            }



    }
}

package xeonex.xeonex.watchdog;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import xeonex.xeonex.controller.CoinInfoController;

//Watchdog
@Component
public class CurrencyPriceHandler {

    @Autowired
    private CoinInfoController coinInfoController;

    @Scheduled(fixedRate = 1000)
    public void handleCurrencyPrice() {

           // System.out.println("Price is above 67000");

    }
}

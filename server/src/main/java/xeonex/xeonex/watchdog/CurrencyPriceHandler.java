package xeonex.xeonex.watchdog;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import xeonex.xeonex.controller.CoinInfoController;
import xeonex.xeonex.domain.Trade.Trade;
import xeonex.xeonex.domain.Trade.TradeLog;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.repositories.TradeLogRepository;
import xeonex.xeonex.repositories.TradeRepository;
import xeonex.xeonex.repositories.UserRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

//Watchdog
@Component
public class CurrencyPriceHandler {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private CoinInfoController coinInfoController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TradeLogRepository tra;

    @Scheduled(fixedRate = 1000)
    public void handleCurrencyPrice() {

        atualizarTrades();
        atualizaSaldoUsers();



    }

    private void atualizaSaldoUsers() {

        for(User u: userRepository.findAll()){
            atualizaSaldoUser(u);
        }
    }


    public void atualizarTrades(){
        for(Trade t: tradeRepository.findAll()){
            if(!t.getTradeStatus().equals("Open")){
                continue;
            }
            atualizaValorTrade(t);

            BigDecimal aux = t.getStopLoss().multiply(t.getInitialInvestment(), MathContext.DECIMAL32).multiply(new BigDecimal(0.01), MathContext.DECIMAL32);
            BigDecimal StopLoss =  t.getInitialInvestment().subtract(aux);

            aux = t.getTakeProfit().multiply(t.getInitialInvestment(), MathContext.DECIMAL32).multiply(new BigDecimal(0.01), MathContext.DECIMAL32);
            BigDecimal TakeProfit =  t.getInitialInvestment().add(aux);


            if(t.getCurrentBalance().compareTo(StopLoss) <= 0) {
                atualizaSaldoUser(t.getUser());
                t.setTradeStatus("Stop Loss");
                tradeRepository.save(t);
                TradeLog tradeLog = new TradeLog(t, "Stop Loss",t.getCurrentBalance().toString());
                tra.save(tradeLog);
                t.getUser().setBalanceInvested(t.getUser().getBalanceInvested().subtract(t.getCurrentBalance()));
                t.getUser().setBalanceAvailable(t.getUser().getBalanceAvailable().add(t.getCurrentBalance()));
                userRepository.save(t.getUser());
            }else if(t.getCurrentBalance().compareTo(TakeProfit) >= 0) {
                atualizaSaldoUser(t.getUser());
                t.setTradeStatus("Take Profit");
                tradeRepository.save(t);
                TradeLog tradeLog = new TradeLog(t, "Take Profit",t.getCurrentBalance().toString());
                tra.save(tradeLog);
                t.getUser().setBalanceInvested(t.getUser().getBalanceInvested().subtract(t.getCurrentBalance()));
                t.getUser().setBalanceAvailable(t.getUser().getBalanceAvailable().add(t.getCurrentBalance()));
                userRepository.save(t.getUser());
            }




        }
    }

    private void atualizaValorTrade(Trade t){
            String pair = t.getAsset() + "-" + t.getUser().getCurrency().getCurrency();
            BigDecimal currentPrice = new BigDecimal(coinInfoController.getAssetPrice(pair));
            BigDecimal newSaldo =  t.getQuantityAsset().multiply(currentPrice, MathContext.DECIMAL32);
            t.setCurrentBalance(newSaldo);
            int scale = Math.max(
                    newSaldo.scale(),
                    currentPrice.scale()
            );

            BigDecimal result ;

            try {
                result = newSaldo.divide(currentPrice, scale, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException e) {
                scale--;
                result = newSaldo.divide(currentPrice, scale, RoundingMode.HALF_UP);
            }

            t.setQuantityAsset( result );
            tradeRepository.save(t);

            TradeLog tradeLog = new TradeLog(t, "Update",t.getCurrentBalance().toString());
            tra.save(tradeLog);
    }

    private void atualizaSaldoUser(User u){
        BigDecimal saldo = new BigDecimal(0);

            for (Trade t : tradeRepository.findTradesByUser(u)){
                if(t.getTradeStatus().equals("Open")){

                    saldo = saldo.add(t.getCurrentBalance());
                }
            }


        u.setBalanceInvested(saldo);
        userRepository.save(u);


    }
}

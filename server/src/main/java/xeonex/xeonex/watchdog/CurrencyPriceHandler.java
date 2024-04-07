package xeonex.xeonex.watchdog;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import xeonex.xeonex.Utils;
import xeonex.xeonex.controller.BotController;
import xeonex.xeonex.controller.CoinInfoController;
import xeonex.xeonex.domain.Trade.Trade;
import xeonex.xeonex.domain.Trade.TradeLog;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.repositories.TradeLogRepository;
import xeonex.xeonex.repositories.TradeRepository;
import xeonex.xeonex.repositories.UserRepository;
import xeonex.xeonex.service.GptService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private int counter = 0;

    Map<String,BigDecimal> map;

    @Scheduled(fixedRate = 3000)
    public void handleCurrencyPrice() {
       counter+=2;


        map = coinInfoController.getPrices();

        try{
            atualizarTrades();
        }catch (HttpClientErrorException e){
           System.out.println("Limitacao API");
       }
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

            botUpdateTrade(t);
            atualizaValorTrade(t);

            BigDecimal profif = new BigDecimal(0);
            if(t.getTradeType().equals("LONG")){
                profif = t.getInitialInvestment().subtract(t.getCurrentBalance());
            }
            if(t.getTradeType().equals("SHORT")){
                profif = t.getCurrentBalance().subtract(t.getInitialInvestment());
            }

            BigDecimal ValorParaStopLoss = t.getInitialInvestment().multiply(t.getStopLoss().divide(new BigDecimal(100))).negate();
            BigDecimal ValorParaTakeProfit = t.getInitialInvestment().multiply(t.getTakeProfit().divide(new BigDecimal(100)));

            if(profif.compareTo(ValorParaStopLoss) <= 0) {
                atualizaSaldoUser(t.getUser());
                t.setTradeStatus("Stop Loss");
                tradeRepository.save(t);
                TradeLog tradeLog = new TradeLog(t, "Stop Loss",t.getCurrentBalance().toString());
                tra.save(tradeLog);



                t.getUser().setBalanceInvested(t.getUser().getBalanceInvested().subtract(t.getInitialInvestment().add(profif)));
                t.getUser().setBalanceAvailable(t.getUser().getBalanceAvailable().add(t.getInitialInvestment().add(profif)));
                userRepository.save(t.getUser());
            }else if(profif.compareTo(ValorParaTakeProfit) >= 0) {
                atualizaSaldoUser(t.getUser());
                t.setTradeStatus("Take Profit");
                tradeRepository.save(t);
                TradeLog tradeLog = new TradeLog(t, "Take Profit",t.getCurrentBalance().toString());
                tra.save(tradeLog);

                if(t.getTradeType().equals("LONG")){

                    profif = t.getInitialInvestment().subtract(t.getCurrentBalance());
                }

                if(t.getTradeType().equals("SHORT")){

                    profif = t.getCurrentBalance().subtract(t.getInitialInvestment());
                }

                t.getUser().setBalanceInvested(t.getUser().getBalanceInvested().subtract(t.getInitialInvestment().add(profif)));
                t.getUser().setBalanceAvailable(t.getUser().getBalanceAvailable().add(t.getInitialInvestment().add(profif)));
                userRepository.save(t.getUser());

            }




        }
    }


    @Autowired
    BotController botController;

    @Autowired
    GptService gptService;
    private void botUpdateTrade(Trade t) {

        Map<String,Integer> mapToSeconds = new HashMap<>();
        mapToSeconds.put("1m", 60);
        mapToSeconds.put("15m", 900);
        mapToSeconds.put("1h", 3600);


        int windowSizeForBotRequest =  mapToSeconds.get(Utils.getWindowMoney().get(t.getWindowMoney()) );

        if(counter % windowSizeForBotRequest == 0){

            String prompt = Utils.readFromFile("src/main/resources/prompt_2.txt");

            String json = buildTradeJson(t.getId().toString(),
                    t.getAsset() + t.getUser().getCurrency().toString(),
                    t.getInitialInvestment().doubleValue(),
                    t.getCurrentBalance().doubleValue(),
                    t.getCurrentBalance().subtract(t.getInitialInvestment()).doubleValue(),
                    t.getTradeType(),
                    t.getStopLoss().doubleValue(),
                    t.getTakeProfit().doubleValue(),
                    t.getRisk().getRiskLevel(),
                    t.getWindowMoney());

            String indicatorJson =    botController.getJsonIndicatorInfo(t.getAsset(), t.getWindowMoney(), t.getUser()).getBody();

            json += "," + indicatorJson;
            prompt = prompt.replace("{JSON_HERE}", json);


            decideByBot( (gptService.get_answer_by_bot(prompt,false)), t);


        }

    }

    public void decideByBot(String jsonString,Trade t) {






    try {
        LinkedHashMap<String,String> jsonObject = (LinkedHashMap<String, String>) new ObjectMapper().readValue(jsonString, Map.class);

       String action = jsonObject.get("action").toString();
       String explanation = jsonObject.get("explanation").toString();

        System.out.println("Action: " + action);

        if (action.equals("CHANGE")) {
            t.setTradeStatus("Close");
            tradeRepository.save(t);
            TradeLog tradeLog = new TradeLog(t, "Close",t.getCurrentBalance().toString(),explanation);
            tra.save(tradeLog);

            BigDecimal profif = new BigDecimal(0);
            if(t.getTradeType().equals("LONG")){

                profif = t.getInitialInvestment().subtract(t.getCurrentBalance());
            }

            if(t.getTradeType().equals("SHORT")){

                profif = t.getCurrentBalance().subtract(t.getInitialInvestment());
            }

            t.getUser().setBalanceInvested(t.getUser().getBalanceInvested().subtract(t.getInitialInvestment().add(profif)));
            t.getUser().setBalanceAvailable(t.getUser().getBalanceAvailable().add(t.getInitialInvestment().add(profif)));
            userRepository.save(t.getUser());
        }else{
            TradeLog tradeLog = new TradeLog(t, "Mantain",t.getCurrentBalance().toString(),explanation);
            tra.save(tradeLog);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }








    }
    public String buildTradeJson(String id, String currency, double open, double actual,
                                 double profit, String type, double SL, double TP,
                                 int userRisk, String windowMoney) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode tradeNode = mapper.createObjectNode();

        tradeNode.put("id", id);
        tradeNode.put("currency", currency);
        tradeNode.put("open", open);
        tradeNode.put("actual", actual);
        tradeNode.put("profit", profit);
        tradeNode.put("type", type);
        tradeNode.put("SL", SL);
        tradeNode.put("TP", TP);
        tradeNode.put("UserRisk", userRisk);
        tradeNode.put("windowMoney", windowMoney);

        return tradeNode.toString();
    }


    private void atualizaValorTrade(Trade t){


            String pair = t.getAsset() + "-" + t.getUser().getCurrency().getCurrency();


            BigDecimal currentPrice =  map.get(pair);


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

                    saldo = saldo.add(t.getInitialInvestment());
                }
            }


        u.setBalanceInvested(saldo);
        userRepository.save(u);


    }
}

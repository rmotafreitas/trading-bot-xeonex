package xeonex.xeonex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.Trade.TradeReceiveDTO;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.domain.User.UserUpdateRequestDTO;
import xeonex.xeonex.infra.security.TokenService;
import xeonex.xeonex.repositories.UserRepository;
import xeonex.xeonex.service.GptService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GptService gptService;

    @Autowired
    private BotController botController;


    // passar Json
    /*

    {
        "risk": entre 5 e 80,
        "spread": valor em %,
        "asset": "BTC/ETH",
        "window_money":  {1d,4h,15m},
        "initialInvestment": 1000 (com o spread aplicado),
        "takeProfit": 1100 (com o spread aplicado),
        "stopLoss": 900 (com o spread aplicado)
     */
    @PostMapping("/open")
    public ResponseEntity openTrade(@RequestHeader("Authorization") String bearerToken, @RequestBody TradeReceiveDTO dto) {
        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);
        User user = (User) userRepository.findByLogin( userLogin);

        if(dto.getInitialInvestment().compareTo(user.getBalanceAvailable()) > 0){
            return ResponseEntity.badRequest().body("{\"error\": \"Insufficient funds\"}");
        }
        if(dto.getRisk() < 5 || dto.getRisk() > 80){
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid risk\"}");
        }
        if(!Utils.getWindowMoney().keySet().contains(dto.getWindow_money())){
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid window money\"}");
        }
        /*
        "value":{the value the user wants to trade eg.100USDT or 100EUR},
        "risk_profile":{here goes the % of risk a use wants to take, basicly the less the percentage the less the risk, only open a trade if it is low risk, if your risk is very high (80% +) the chance of you decide to open a trade is very high}
        "window_money":{thats the time pretended of the user to make profit, this is not a mandotory requirement, but this value could go to 15m to 1w, and it is not SUPER important to get profit in the exact time but for example if he wants to take profit in 15m, i will give you indicator infos of the last hours with 1 minute gap, if it is 1w ill give you ingo about last days, so this value is indicative}
        "take_profit":{this value in % is the % of profit if he reaches the trade automatically closes}
        "stop_loss":{this value in % is the % of loss if he reaches the trade automatically closes}
        "spread":{this is the spread of the currency, this is the difference between the buy and sell price, this is important to calculate the profit}
        "is_there_position_open"{
            "position":{true_or_false if open true if not false}},
            "actual_profit":{in % (could be negative) the actual profit of the trade, you use this to calculate if is necessary to change the positione or not}
        }
        "currency": {the currency like BTCUSD, ETHUSD, etc},
        "backtrack_interval": {the time like 1m,5m,15m},
        {here goes a json with info of the last 20 candles on the {backtrack_interval (if the backtrack_interval is 1m is the info about last 20 minutes) }  of 6 indicators: "rsi", "macd", "ma", "ema", "bbands","fibonacciretracement}
         */


        // Se tiver tempo usar reflection para apanhar os valores do dto
        Map<String, Object> tradeData = new HashMap<>();
        tradeData.put("value", dto.getInitialInvestment().toString() + user.getCurrency());
        tradeData.put("risk_profile",  dto.getRisk().toString() + "%");
        tradeData.put("window_money",  dto.getWindow_money());
        tradeData.put("take_profit", "+" + dto.getTakeProfit().toString()+ "%");
        tradeData.put("stop_loss", "-" + dto.getStopLoss().toString() + "%");
        tradeData.put("spread", dto.getSpread().toString());
        tradeData.put("is_there_position_open", false);
        tradeData.put("actual_profit", null);
        tradeData.put("currency", dto.getAsset()+ user.getCurrency());
        tradeData.put("backtrack_interval", Utils.getWindowMoney().get(dto.getWindow_money()));



        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(tradeData);
            json+= (botController.getJsonIndicatorInfo(dto.getAsset(), Utils.getWindowMoney().get(dto.getWindow_money()), user).getBody());

            return ResponseEntity.ok().body(gptService.get_answer_by_bot(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }



        return ResponseEntity.ok().body("{\"error\": \"Internal error\"}");




    }






}

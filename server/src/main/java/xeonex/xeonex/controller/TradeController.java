package xeonex.xeonex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.Trade.Trade;
import xeonex.xeonex.domain.Trade.TradeAspect;
import xeonex.xeonex.domain.Trade.TradeLog;
import xeonex.xeonex.domain.Trade.TradeReceiveDTO;
import xeonex.xeonex.domain.User.Risk;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.domain.User.UserUpdateRequestDTO;
import xeonex.xeonex.infra.security.TokenService;
import xeonex.xeonex.repositories.TradeLogRepository;
import xeonex.xeonex.repositories.TradeRepository;
import xeonex.xeonex.repositories.UserRepository;
import xeonex.xeonex.service.GptService;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private TradeRepository tradeRepository;


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

    // penso rapido
    @Autowired
    TradeLogRepository tradeLogRepository;
    @PostMapping("/open")
    public ResponseEntity openTradeEndPoint(@RequestHeader("Authorization") String bearerToken, @RequestBody TradeReceiveDTO dto) {
        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);
        User user = (User) userRepository.findByLogin(userLogin);
        if(dto.getInitialInvestment().compareTo(user.getBalanceAvailable()) > 0){
            return ResponseEntity.badRequest().body("{\"error\": \"Insufficient funds\"}");
        }
        if(dto.getRisk() < 5 || dto.getRisk() > 80){
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid risk\"}");
        }
        if(!Utils.getWindowMoney().keySet().contains(dto.getWindow_money())){
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid window money\"}");
        }

        if(dto.getAssetprice() == null){
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid asset price\"}");
        }

        Map<String, Object> tradeData = prepareTradeData(dto, user);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(tradeData);
            json += getIndicatorInfo(dto.getAsset(), dto.getWindow_money(), user);

            String response = gptService.get_answer_by_bot(json);

            if(response.contains("DO_NOTHING")){
                return ResponseEntity.ok().body(response);
            }
            String tradeType = response.contains("SHORT") ? "SHORT" : "LONG";

            BigDecimal currentBalance = dto.getInitialInvestment().add(dto.getInitialInvestment().multiply(dto.getSpread()).divide(new BigDecimal(100)));
            Trade t = new Trade(
                    dto.getInitialInvestment(),
                    dto.getAsset(),
                    currentBalance,
                    new Risk(dto.getRisk()),
                    dto.getTakeProfit(),
                    dto.getStopLoss(),
                    tradeType,
                    "Waiting",
                    user,
                    dto.getAssetprice().divide(dto.getInitialInvestment(), RoundingMode.UNNECESSARY));


            tradeRepository.save(
            t
            );
            tradeLogRepository.save(new TradeLog( t, "Created")); // deviar ser um serviço ou um watch dog

            int lastBraceIndex = response.lastIndexOf("}");
            String tradeId = "\"trade_id\": \"" + t.getId() + "\"";
            response = response.substring(0, lastBraceIndex) + ", " + tradeId + "}";

            return ResponseEntity.ok().body(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("{\"error\": \"Internal error\"}");
    }

    private Map<String, Object> prepareTradeData(TradeReceiveDTO dto, User user) {
        Map<String, Object> tradeData = new HashMap<>();
        tradeData.put("value", dto.getInitialInvestment().toString() + user.getCurrency());
        tradeData.put("risk_profile", dto.getRisk().toString() + "%");
        tradeData.put("window_money", dto.getWindow_money());
        tradeData.put("take_profit", "+" + dto.getTakeProfit().toString() + "%");
        tradeData.put("stop_loss", "-" + dto.getStopLoss().toString() + "%");
        tradeData.put("spread", dto.getSpread().toString());
        tradeData.put("is_there_position_open", false);
        tradeData.put("actual_profit", null);
        tradeData.put("currency", dto.getAsset() + user.getCurrency());
        tradeData.put("backtrack_interval", Utils.getWindowMoney().get(dto.getWindow_money()));
        return tradeData;
    }

    private String getIndicatorInfo(String asset, String windowMoney, User user) {
        return botController.getJsonIndicatorInfo(asset, Utils.getWindowMoney().get(windowMoney), user).getBody();
    }







}

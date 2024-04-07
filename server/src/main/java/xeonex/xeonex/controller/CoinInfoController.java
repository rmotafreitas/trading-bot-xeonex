package xeonex.xeonex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jose4j.json.internal.json_simple.JSONObject;
import xeonex.binance.model.BinanceMapper;
import xeonex.binance.model.Candle;
import xeonex.binance.model.Line;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.User.Currency;
import xeonex.xeonex.domain.User.CurrencyDTO;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.infra.security.TokenService;
import xeonex.xeonex.repositories.UserRepository;
import xeonex.xeonex.service.BinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import xeonex.xeonex.service.CryptoCurrencyService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coin")
public class CoinInfoController {




    @Value("${binance.api.url.kline}")
    private String BINANCE_API_URL_KLINE;
    @Value("${uphold.api.url.price}")
    private String UPHOLD_API_URL_PRICE;

    @Value("${binance.api.bidprice}")
    private String BINANCE_API_URL_BIDPRICE;
    @Autowired
    private BinanceService binanceService;

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;


    private final String url_btc_eth = "https://api.uphold.com/v0/ticker/USDT";


    public Map<String, BigDecimal> getPrices() {
        Map<String, BigDecimal> prices = new HashMap<>();

        String pair1 = "BTC-USDT";
        String pair2 = "ETH-USDT";

        String responseBody = restTemplate.getForObject(url_btc_eth, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            for (JsonNode node : jsonNode) {

                if(node.get("pair").toString().replace("\"","").equals(pair1)) {
                    prices.put(pair1, new BigDecimal(node.get("ask").asText()));
                }
                if(node.get("pair").toString().replace("\"","").equals(pair2)) {
                    prices.put(pair2, new BigDecimal(node.get("ask").asText()));
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        return prices;
    }





    public double getAssetPrice(String asset) {

        String jsonResponse = coinPrice(asset);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        double ask = jsonNode.get("bid").asDouble();

        return ask;
    }






    @GetMapping("/currency")
    public ResponseEntity<List<CurrencyDTO>> getCurrency() {
        List<CurrencyDTO> currencies = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            currencies.add(new CurrencyDTO(currency.name(), currency.getCurrency()));
        }
        return ResponseEntity.ok(currencies);
    }


    @RequestMapping("/{pair}")
    public ResponseEntity<String> getCoinPrice(@RequestHeader("Authorization") String bearerToken ,@PathVariable String pair) throws JsonProcessingException {

        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);
        User u = (User) userRepository.findByLogin(userLogin);


        if(!cryptoCurrencyService.getCryptoCyrrency().keySet().contains(pair)){
            return ResponseEntity.badRequest().body("Invalid pair");
        }

        return ResponseEntity.ok(coinPrice(pair + "-" + u.getCurrency().getCurrency()));






    }
    public String coinPriceBinance(String pair) {

        String url = BINANCE_API_URL_BIDPRICE + "?symbol=" + pair;
        String body = restTemplate.getForEntity(url, String.class).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode.toString();
    }

    public String coinPrice(String pair) {

        String url = UPHOLD_API_URL_PRICE + "/" +  pair;
        String body = restTemplate.getForEntity(url, String.class).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        double ask = jsonNode.get("ask").asDouble();
        double bid = jsonNode.get("bid").asDouble();


        double spread = ask - bid;
        double spreadPercentage = (spread / ask) * 100;


        ((ObjectNode) jsonNode).put("spread", spread);


        ((ObjectNode) jsonNode).put("spread_percentage", spreadPercentage);

        return jsonNode.toString();
    }


    @RequestMapping("/{pair}/chart")
    public ResponseEntity<?> getCoinPriceFallback(@RequestHeader("Authorization") String bearerToken ,@PathVariable String pair,
                                                  @RequestParam(defaultValue = "1d")  String interval,
                                                  @RequestParam(defaultValue = "candle")  String type) {

        BinanceMapper binanceMapper = new BinanceMapper();

        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);
        User u = (User) userRepository.findByLogin(userLogin);

        if(!cryptoCurrencyService.getCryptoCyrrency().keySet().contains(pair)){
            return ResponseEntity.badRequest().body("Invalid pair");
        }



        String url = BINANCE_API_URL_KLINE + "?symbol=" + pair.toUpperCase() + u.getCurrency().getCurrency() + "&interval=" + interval;



        if ("candle".equals(type)) {

            List<Candle> candles = binanceService.getCandlesFromBinance(url, restTemplate);
            return ResponseEntity.ok(candles);

        }else if ("line".equals(type)){

            List<Line> lines = binanceMapper.mapCandleListToLine(binanceService.getCandlesFromBinance(url, restTemplate));
            return ResponseEntity.ok(lines);
        }





        return ResponseEntity.ok("Fallback");
    }


    @GetMapping("/pairs")
    public ResponseEntity<List<String>> getPairs() {
        Set<String> possibleDayTypes =  cryptoCurrencyService.getCryptoCyrrency().keySet();
        return ResponseEntity.ok(possibleDayTypes.stream().toList());
    }


    @RequestMapping("/timeTypes")
    public ResponseEntity<List<String>> getPossibleDayTypes() {
        List<String> possibleDayTypes = Utils.getIntervals();
        return ResponseEntity.ok(possibleDayTypes);
    }






}

package xeonex.xeonex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xeonex.binance.model.BinanceMapper;
import xeonex.binance.model.Candle;
import xeonex.binance.model.Line;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coin")
public class CoinInfoController {




    @Value("${binance.api.url.kline}")
    private String BINANCE_API_URL_KLINE;
    @Value("${uphold.api.url.price}")
    private String UPHOLD_API_URL_PRICE;

    @Autowired
    private BinanceService binanceService;

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

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




        String url = UPHOLD_API_URL_PRICE + "/" +  pair + "-" + u.getCurrency().getCurrency();

        String body = restTemplate.getForEntity(url, String.class).getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);

        return ResponseEntity.ok(jsonNode.toString());






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
    List<String> possibleDayTypes = (List<String>) cryptoCurrencyService.getCryptoCyrrency().keySet();
    return ResponseEntity.ok(possibleDayTypes);
}


    @RequestMapping("/timeTypes")
    public ResponseEntity<List<String>> getPossibleDayTypes() {
        List<String> possibleDayTypes = Arrays.asList(
                "1m", "5m", "15m", "30m", "1h", "4h", "1d", "1w", "1M"
        );
        return ResponseEntity.ok(possibleDayTypes);
    }






}

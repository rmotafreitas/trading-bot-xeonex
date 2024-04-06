package xeonex.xeonex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xeonex.binance.model.BinanceMapper;
import xeonex.binance.model.Candle;
import xeonex.binance.model.Line;
import xeonex.xeonex.service.BinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/coin")
public class CoinInfoController {




    @Value("${binance.api.url.kline}")
    private String BINANCE_API_URL_KLINE;
    @Value("${uphold.api.url.price}")
    private String UPHOLD_API_URL_PRICE;

    @Autowired
    private BinanceService binanceService;

    private final RestTemplate restTemplate = new RestTemplate();



    @RequestMapping("/{pair}")
    public ResponseEntity<String> getCoinPrice(@PathVariable String pair) throws JsonProcessingException {

        if(pair.contains("USDT")){
            pair = pair.replace("USDT", "-USDT");
        }
        String url = UPHOLD_API_URL_PRICE + "/" + pair ;

        String body = restTemplate.getForEntity(url, String.class).getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);

        return ResponseEntity.ok(jsonNode.toString());






    }


    @RequestMapping("/{pair}/chart")
    public ResponseEntity<?> getCoinPriceFallback(@PathVariable String pair,
                                                  @RequestParam(defaultValue = "1d")  String interval,
                                                  @RequestParam(defaultValue = "candle")  String type) {

        BinanceMapper binanceMapper = new BinanceMapper();

        String url = BINANCE_API_URL_KLINE + "?symbol=" + pair.toUpperCase() + "&interval=" + interval;


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
    List<String> possibleDayTypes = Arrays.asList(
            "BTCUSDT", "ETHUSDT"
    );
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

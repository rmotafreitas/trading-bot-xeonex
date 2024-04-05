package xeonex.xeonex.controller;

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


    @Value("${binance.api.url.price}")
    private  String BINANCE_API_URL_PRICE;

    @Value("${binance.api.url.kline}")
    private String BINANCE_API_URL_KLINE;

    @Autowired
    private BinanceService binanceService;

    private final RestTemplate restTemplate = new RestTemplate();



    @RequestMapping("/{coinName}")
    public ResponseEntity<String> getCoinPrice(@PathVariable String coinName) {

        String url = BINANCE_API_URL_PRICE + "?symbol=" + coinName.toUpperCase() + "USDT";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);


        return response;
    }


    @RequestMapping("/{coinName}/chart")
    public ResponseEntity<?> getCoinPriceFallback(@PathVariable String coinName,
                                                  @RequestParam(defaultValue = "1d")  String interval,
                                                  @RequestParam(defaultValue = "candle")  String type) {



        BinanceMapper binanceMapper = new BinanceMapper();

        String url = BINANCE_API_URL_KLINE + "?symbol=" + coinName.toUpperCase() + "USDT&interval=" + interval;


        if ("candle".equals(type)) {

            List<Candle> candles = binanceService.getCandlesFromBinance(url, restTemplate);
            return ResponseEntity.ok(candles);

        }else if ("line".equals(type)){

            List<Line> lines = binanceMapper.mapCandleListToLine(binanceService.getCandlesFromBinance(url, restTemplate));
            return ResponseEntity.ok(lines);
        }





        return ResponseEntity.ok("Fallback");
    }




    @RequestMapping("/timeTypes")
    public ResponseEntity<List<String>> getPossibleDayTypes() {
        List<String> possibleDayTypes = Arrays.asList(
                "1m", "5m", "15m", "30m", "1h", "4h", "1d", "1w", "1M"
        );
        return ResponseEntity.ok(possibleDayTypes);
    }



}

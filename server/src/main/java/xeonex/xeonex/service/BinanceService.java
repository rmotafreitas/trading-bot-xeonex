package xeonex.xeonex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import xeonex.binance.model.Candle;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceService {

    public List<Candle> getCandlesFromBinance(String url, RestTemplate restTemplate) {

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Object>> candleData = null;
        try {

            candleData = objectMapper.readValue(body, new TypeReference<List<List<Object>>>(){});


            List<Candle> candles = new ArrayList<>();
            for (List<Object> data : candleData) {
                Candle candle = new Candle( );

                candle.setOpenTime(((Long) data.get(0)));
                candle.setOpenPrice(new BigDecimal((String) data.get(1)));
                candle.setHighPrice(new BigDecimal((String) data.get(2)));
                candle.setLowPrice(new BigDecimal((String) data.get(3)));
                candle.setClosePrice(new BigDecimal((String) data.get(4)));
                candle.setVolume(new BigDecimal((String) data.get(5)));
                candle.setCloseTime(((Long) data.get(6)));


                candles.add(candle);
            }

            return candles;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

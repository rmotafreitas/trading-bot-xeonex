package xeonex.xeonex.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CryptoCurrencyService {


    public Map<String,String> getCryptoCyrrency(){

        Map<String,String> cryptoCurrency = new HashMap<>();
        cryptoCurrency.put("BTC","Bitcoin");
        cryptoCurrency.put("ETH","Ethereum");

        return cryptoCurrency;
    }


}

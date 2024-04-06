package xeonex.xeonex.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.User.Currency;

import java.util.Arrays;
import java.util.List;

@Service
public class IndicatorService {

    //return JSON

    @Value("${taapi.api.url.indicator}")
    private String urlRsi;




    private final RestTemplate restTemplate = new RestTemplate();

    public String getIndicatorInfo(String indicator,String currency, String interval, Currency userCurrency) {



        String urlFinal = "";
        if (Utils.getIntervals().contains(interval)) {
            urlFinal= urlRsi.replace("{indicator}",indicator) + "&symbol=" + currency + "/" + userCurrency.toString().replace(" ","")  + "&interval=" + interval + "&backtracks=20";

            return restTemplate.getForObject(urlFinal, String.class);
        }


        return "Invalid interval";


    }




}

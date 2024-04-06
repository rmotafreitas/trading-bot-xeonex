package xeonex.xeonex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import xeonex.gpt.model.ChatgptRequest;
import xeonex.gpt.model.ChatgptResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.User.Currency;
import xeonex.xeonex.domain.User.User;
import xeonex.xeonex.infra.security.TokenService;
import xeonex.xeonex.repositories.UserRepository;
import xeonex.xeonex.service.CryptoCurrencyService;
import xeonex.xeonex.service.IndicatorService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gpt")
public class BotController {





    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    IndicatorService indicatorService;

    @Autowired
    CryptoCurrencyService cryptoCurrencyService;




    private List<String> indicatersToUse = Arrays.asList("rsi", "macd", "ma", "ema", "bbands","fibonacciretracement");
    @GetMapping("/getIndicatorInfo")
    public ResponseEntity<String> getJsonIndicatorInfoEndPoint(@RequestHeader("Authorization") String bearerToken, @RequestParam String currency, @RequestParam String interval) {
        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);

        if (!Utils.getIntervals().contains(interval)) {
            return ResponseEntity.badRequest().body("Invalid interval");
        }



        User u = (User) userRepository.findByLogin(userLogin);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return getJsonIndicatorInfo(currency, interval, u);
    }


    // tem de passar para um service
    public ResponseEntity<String> getJsonIndicatorInfo(String currency, String interval, User u) {





        Currency userCurrency = u.getCurrency();
        StringBuilder indicatorInfo = new StringBuilder();
        for (String indicator : indicatersToUse) {
            String indicatorData = indicatorService.getIndicatorInfo(indicator, currency, interval, userCurrency);
            if (indicatorData != null) {
                indicatorInfo.append("\"").append(indicator).append("\":").append(indicatorData).append(",");
            }
        }

        if (indicatorInfo.length() > 0) {
            indicatorInfo.deleteCharAt(indicatorInfo.length() - 1); // Remova a vírgula extra no final
        }

        String json = "{" +
                "\"currency\":\"" + currency + "/" + userCurrency + "\"," +
                "\"backtrack_interval\":\"" + interval + "\"," +
                indicatorInfo.toString() +
                "}";

        return ResponseEntity.ok(json);
    }




    /*
    @RequestMapping(value = "/getmeal",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMealWithCalories(@RequestBody String query) {

        ChatgptRequest chatgptRequest = new ChatgptRequest(model, PromptMeal + query);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);


        ChatgptResponse chatgptResponse = restTemplate.postForObject(apiUrl,
                new HttpEntity<>(chatgptRequest,headers),
                ChatgptResponse.class);


        return ResponseEntity.ok(chatgptResponse.getChoices().get(0).getMessage().getContent());


    }


     */
}

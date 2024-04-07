package xeonex.xeonex.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import xeonex.gpt.model.ChatgptRequest;
import xeonex.gpt.model.ChatgptResponse;
import xeonex.xeonex.Utils;
import xeonex.xeonex.controller.BotController;

import java.io.File;

@Service
public class GptService {

    private static RestTemplate restTemplate = new RestTemplate();

    @Value("${chatgpt.model}")
    private String model;
    @Value("${chatgpt.api.url}")
    private String apiUrl;
    @Value("${chatgpt.api.key}")
    private String apiKey;

    private String prompt_default = Utils.readFromFile("src/main/resources/prompt_default.txt");


    public String get_answer_by_bot(String prt,boolean default_prompt) {

        ChatgptRequest chatgptRequest;
        if(default_prompt){
             chatgptRequest = new ChatgptRequest(model, prompt_default.replace(" {$JSON_PROMPT}",prt));
        }else{
             chatgptRequest = new ChatgptRequest(model, prt);
        }


        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + apiKey);
        ChatgptResponse chatgptResponse = restTemplate.postForObject(apiUrl,
                new HttpEntity<>(chatgptRequest,headers),
                ChatgptResponse.class);

        return chatgptResponse.getChoices().get(0).getMessage().getContent();


    }

}

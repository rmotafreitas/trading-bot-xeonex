package xeonex.xeonex;

import xeonex.xeonex.Exception.TokenInvalidException;
import jakarta.servlet.http.HttpServletResponse;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    @Value("${jose.is.enable.jose}")
    private static int IS_ENABLE_JOSE;




    public static String decodeJwt(String token) {

        /*
        if(IS_ENABLE_JOSE != 1){
            return token;
        }
         System.out.println("Decoding JWT");
         */

        try {
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setSkipAllValidators()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    .build();
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);

            return jwtClaims.getSubject();
        } catch (MalformedClaimException e) {
            throw new RuntimeException(e);
        } catch (InvalidJwtException e) {
            throw new TokenInvalidException("Invalid token");
        }
    }

    public static void sendError(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    public static List<String> getTradeType(){
        return List.of("SHORT", "LONG");
    }

    public static List<String> getIntervals(){
        return List.of("1m", "5m", "15m", "30m", "1h", "2h", "4h", "12h", "1d","1w");
    }

    public static List<String> getTradeStatus(){
        return List.of("OPEN", "CLOSED");
    }
    public static String readFromFile(String filePath) {
        try {

            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    public static Map<String,String> getWindowMoney(){
        Map<String,String> mp = new HashMap<>();
        mp.put("1d", "1h");
        mp.put("4h", "15m");
        mp.put("15m", "1m");
        return mp;
    }
}


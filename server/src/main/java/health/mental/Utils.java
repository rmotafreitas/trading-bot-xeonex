package health.mental;

import jakarta.servlet.http.HttpServletResponse;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class Utils {

    @Value("${jose.is.enable.jose}")
    private  static  boolean IS_ENABLE_JOSE;

    public static String decodeJwt(String token) {

        if(!IS_ENABLE_JOSE){
            return token;
        }

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
            throw new RuntimeException(e);
        }
    }

    public static void sendError(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}

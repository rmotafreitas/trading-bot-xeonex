package health.mental.Exception;

public class TokenExpiredExceptions extends RuntimeException{
    public TokenExpiredExceptions(String message) {
        super(message);
    }
}

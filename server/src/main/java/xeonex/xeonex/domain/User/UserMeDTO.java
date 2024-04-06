package xeonex.xeonex.domain.User;

import java.math.BigDecimal;

public record UserMeDTO(
        String login,
        UserRole role,
        BigDecimal balanceInvested,
        BigDecimal balanceAvailable,
        BigDecimal balanceTotal,
        Integer risk
) {



}

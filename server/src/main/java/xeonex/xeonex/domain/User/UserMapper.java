package xeonex.xeonex.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xeonex.xeonex.controller.TradeController;

import java.math.BigDecimal;

@Service
public class UserMapper {
    @Autowired
    TradeController tradeController;

    public   UserMeDTO toUserMeDTO(User user) {
        return new UserMeDTO(user.getLogin(), user.getRole(), user.getBalanceInvested(), user.getBalanceAvailable(), user.getBalanceInvested().add(tradeController.calculateLucroByUser(user)).add(user.getBalanceAvailable()),

                tradeController.calculateLucroByUser(user) ,

                user.getRisk().getRiskLevel() , user.getCurrency(), user.getImg() );
    }





}

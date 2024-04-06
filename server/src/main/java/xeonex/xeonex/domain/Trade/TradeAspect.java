package xeonex.xeonex.domain.Trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xeonex.xeonex.repositories.TradeLogRepository;

@Service
public class TradeAspect {

    @Autowired
    TradeLogRepository tradeLogRepository;

    public void setTradeStatus(Trade tr,String tradeStatus) {

        if(tradeStatus.equals("Closed")){
            tradeLogRepository.save(new TradeLog( tr, "Closed"));
        } else if(tradeStatus.equals("Cancelled")){
            tradeLogRepository.save(new TradeLog( tr, "Cancelled"));
        } else if(tradeStatus.equals("Active")){
            tradeLogRepository.save(new TradeLog( tr, "Active"));
        }


        tr.setTradeStatus(tradeStatus);
    }
}

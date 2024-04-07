package xeonex.xeonex.domain.Trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import xeonex.xeonex.repositories.TradeLogRepository;

@Service
@Component
public class TradeAspect {

    @Autowired
    TradeLogRepository tradeLogRepository;

    public void setTradeStatus(Trade tr,String tradeStatus) {

        if(tradeStatus.equals("Closed")){
            tradeLogRepository.save(new TradeLog( tr, "Closed",tr.getCurrentBalance().toString()));
        } else if(tradeStatus.equals("Cancelled")){
            tradeLogRepository.save(new TradeLog( tr, "Cancelled",tr.getCurrentBalance().toString()));
        } else if(tradeStatus.equals("Open")){
            tradeLogRepository.save(new TradeLog( tr, "Open",tr.getCurrentBalance().toString()));
        }


        tr.setTradeStatus(tradeStatus);
    }
}

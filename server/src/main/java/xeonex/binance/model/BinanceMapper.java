package xeonex.binance.model;

import java.util.ArrayList;
import java.util.List;

public class BinanceMapper {

    public static List<Line> mapCandleListToLine(List<Candle> candle) {

        List <Line> lineList = new ArrayList<>();
        for (Candle c : candle) {
            Line line = new Line();
            line.setXTime(c.getOpenTime().toString());
            line.setYPrice(c.getOpenPrice().toString());
            lineList.add(line);

            line = new Line();
            line.setXTime(c.getCloseTime().toString());
            line.setYPrice(c.getClosePrice().toString());
            lineList.add(line);

        }
        return lineList;

    }

}

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

import { DataTable } from "@/components/data-table";
import api from "@/lib/api/api";
import { FileVideo, FilterIcon, Globe } from "lucide-react";
import { useEffect, useState } from "react";
import { columns, HistoryItemTradeEntry } from "./trades-entry.columns";
import { useAuth } from "@/lib/hooks/useAuth";
import { TypeCryptoLive, usdtToCrypto } from "@/lib/api";

interface TradeTableProps {
  BTC: TypeCryptoLive;
  ETH: TypeCryptoLive;
}

export function TradesTable({ BTC, ETH }: TradeTableProps) {
  const [data, setData] = useState<HistoryItemTradeEntry[]>([]);
  const { user } = useAuth();

  const hydrate = async () => {
    api.get("/trade/all").then((response) => {
      let aux = response.data;
      aux = aux.map((item: any) => {
        /*
              if(trade.getTradeStatus().equals("Open")){

                BigDecimal profit = BigDecimal.ZERO;
                if (trade.getTradeType().equals("LONG")) {
                    profit = trade.getCurrentBalance().subtract(trade.getInitialInvestment());
                } else if (trade.getTradeType().equals("SHORT")) {
                    profit = trade.getInitialInvestment().subtract(trade.getCurrentBalance());
                }
                lucro = lucro.add(profit);

            }
            */
        item.profit =
          item.trade_type === "LONG"
            ? item.actual_value - item.buy_value
            : item.buy_value - item.actual_value;
        item.profit = Number(item.profit).toFixed(2);
        return {
          ...item,
          valor_compra: Number(+item.buy_value).toFixed(2),
          valor_atual: Number(item.actual_value).toFixed(2),
          stop_loss: Number(item.stop_loss_value).toFixed(2),
          take_profit: Number(item.take_profit_value).toFixed(2),
          created_at: new Date(item.trade_logs[0].date * 1000).toLocaleString(),
          window_money:
            item.window_money === "4h"
              ? "Medium"
              : item.window_money === "1d"
              ? "Slow"
              : "Fast",
          is_open: item.trade_status === "Open",
        };
      });
      console.log(response.data);
      console.log(aux);
      setData(aux);
    });
  };

  useEffect(() => {
    hydrate();
    const interval = setInterval(() => {
      hydrate();
    }, 4000);
    return () => clearInterval(interval);
  }, [user]);

  return (
    <div className="relative mt-8">
      <DataTable columns={columns} data={data} />
      <DropdownMenu>
        <DropdownMenuContent>
          <DropdownMenuLabel>Filter</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuItem className="flex flex-row gap-2 justify-start items-center">
            <FileVideo className="w-4" />
            Open
          </DropdownMenuItem>
          <DropdownMenuItem className="flex flex-row gap-2 justify-start items-center">
            <Globe className="w-4" />
            Close
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}

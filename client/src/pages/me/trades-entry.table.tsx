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
import { TypeCryptoLive } from "@/lib/api";

export const deleteRow = async (id: string) => {};

interface TradeTableProps {
  BTC: TypeCryptoLive;
  ETH: TypeCryptoLive;
}

export function TradesTable({ BTC, ETH }: TradeTableProps) {
  const [data, setData] = useState<HistoryItemTradeEntry[]>([]);

  const { user } = useAuth();

  useEffect(() => {
    api.get("/trade/all").then((response) => {
      let aux: HistoryItemTradeEntry[] = response.data;
      aux = aux.map((item: any) => {
        if (item.trade_status.toLowerCase() === "waiting") return;
        console.log(item);
        const newItem: HistoryItemTradeEntry = {
          trade_id: item.trade_id,
          valor_atual: item.valor_atual,
          stop_loss: item.stop_loss,
          take_profit: item.take_profit,
          risk: item.risk,
          created_at: item.created_at,
          window_money: item.window_money,
        };
        return newItem;
      });
      console.log(response.data);
      console.log(aux);
      setData(aux);
    });
  }, [user]);

  return (
    <>
      <DataTable columns={columns} data={data} />
      <DropdownMenu>
        <DropdownMenuTrigger className="absolute top-[4.85rem] right-2">
          <FilterIcon className="text-xl text-muted-foreground" />
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuLabel>Media Filter</DropdownMenuLabel>
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
    </>
  );
}

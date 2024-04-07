import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

import { DataTable } from "@/components/data-table";
import { FileVideo, FilterIcon, Globe } from "lucide-react";
import { columns } from "./trades-logs.columns";
import { HistoryItemTradeLogEntry } from "./trades-logs.columns";

interface TradeTableProps {
  data: HistoryItemTradeLogEntry[];
}

export function TradeTable({ data }: TradeTableProps) {
  return (
    <div className="relative mt-8">
      <DataTable columns={columns} data={data} />
      <DropdownMenu>
        <DropdownMenuContent>
          <DropdownMenuLabel>Filter</DropdownMenuLabel>
          <DropdownMenuSeparator />
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}

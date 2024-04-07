"use client";

import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown, MoreHorizontal } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Link } from "react-router-dom";
import { deleteRow } from "./trades-entry.table";

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type HistoryItemTradeEntry = {
  trade_id: string;
  valor_compra: number;
  valor_atual: number;
  stop_loss: number;
  take_profit: number;
  risk: number;
  created_at: string;
  window_money: number;
};

export const columns: ColumnDef<HistoryItemTradeEntry>[] = [
  {
    accessorKey: "trade_id",
    header: "Trade ID",
  },
  {
    id: "actions",
    cell: ({ row }) => {
      const website = row.original;

      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">Open menu</span>
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>Actions</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem>
              <Link
                className="w-full h-full flex items-center"
                to={`/apps/websites/${website.id}`}
              >
                View
              </Link>
            </DropdownMenuItem>
            <DropdownMenuItem
              className="text-red-500 cursor-pointer"
              onClick={async () => {
                // await deleteRow(website.id, "websites");
              }}
            >
              Close
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
];

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
  asset: string;
  valor_compra: number;
  valor_atual: number;
  stop_loss: number;
  take_profit: number;
  risk: number;
  created_at: string;
  window_money: number;
  profit: number;
  trade_type: string;
};

export const columns: ColumnDef<HistoryItemTradeEntry>[] = [
  {
    accessorKey: "trade_id",
    header: "Trade ID",
    cell: ({ row }) => {
      const value: string = row.getValue("trade_id");
      return value.length > 10 ? value.slice(0, 10) + "..." : value;
    },
  },
  {
    accessorKey: "asset",
    header: "Asset",
  },
  {
    accessorKey: "valor_compra",
    header: "Valor de Compra",
  },
  {
    accessorKey: "valor_atual",
    header: "Saldo",
  },
  {
    accessorKey: "trade_type",
    header: "Trade Type",
  },
  {
    accessorKey: "profit",
    header: "Profit",
    cell: ({ row }) => {
      const value: number = +row.getValue("profit");
      return (
        <div className="flex items-center gap-1">
          <span className="ml-1">{value}</span>
          <ArrowUpDown
            className={`h-4 w-4 ${
              value > 0 ? "text-green-500" : "text-red-500"
            }`}
          />
        </div>
      );
    },
  },
  {
    accessorKey: "stop_loss",
    header: "Stop Loss",
  },
  {
    accessorKey: "take_profit",
    header: "Take Profit",
  },
  {
    accessorKey: "risk",
    header: "Risk",
  },
  {
    accessorKey: "created_at",
    header: "Created At",
  },
  {
    accessorKey: "window_money",
    header: "Window Money",
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

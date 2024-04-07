"use client";

import { Button } from "@/components/ui/button";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";

export type HistoryItemTradeLogEntry = {
  date: number;
  value: number;
  asset: string;
  action: string;
  is_bot: boolean;
  msg: string;
};

export const columns: ColumnDef<HistoryItemTradeLogEntry>[] = [
  {
    accessorKey: "date",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Date
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const value: number = row.getValue("date");
      return new Date(value * 1000).toLocaleString();
    },
  },
  {
    accessorKey: "value",
    header: "Value Price",
  },
  {
    accessorKey: "asset",
    header: "Asset",
  },
  {
    accessorKey: "action",
    header: "Action",
  },
  {
    accessorKey: "is_bot",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Bot 🤖
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      );
    },
    cell: ({ row }) => {
      const value: boolean = row.getValue("is_bot");
      return value ? "✅" : "❌";
    },
  },
  {
    accessorKey: "msg",
    header: "Message",
  },
];

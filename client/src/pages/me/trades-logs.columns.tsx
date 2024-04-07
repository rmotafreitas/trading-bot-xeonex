"use client";

import { ColumnDef } from "@tanstack/react-table";

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
    header: "Date",
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
    header: "Bot 🤖",
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

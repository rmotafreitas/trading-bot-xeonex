import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { DataTable } from "@/components/data-table";
import { Navbar } from "@/components/navbar";
import {
  FileAudio,
  FileText,
  FileVideo,
  FilterIcon,
  Globe,
} from "lucide-react";

export const deleteRow = async (id: string) => {};

export function HistoryPage() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <div className="flex flex-col flex-1 justify-center items-center">
        {history?.websites && (
          <div className="flex flex-col justify-center items-end relative">
            <DataTable columns={cols} data={history} />
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
          </div>
        )}
      </div>
    </div>
  );
}

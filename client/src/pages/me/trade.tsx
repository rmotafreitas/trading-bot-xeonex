import { TradeParams } from "@/App";
import { Navbar } from "@/components/navbar";
import api from "@/lib/api/api";
import { useAuth } from "@/lib/hooks/useAuth";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";

import Chart from "react-apexcharts";
import { Label } from "@radix-ui/react-dropdown-menu";
import { getCryptoLive, TypeCryptoLive, usdtToCrypto } from "@/lib/api";
import { TradeTable } from "./trades-logs.table";
import { Button } from "@/components/ui/button";
import { useTheme } from "@/components/theme-provider";
import { LoaderIcon } from "lucide-react";

export function TradePage() {
  const { tradeId } = useParams<TradeParams>();
  const [data, setData] = useState<any>({});
  const { user } = useAuth();

  const firstTime = useRef(true);

  const { theme } = useTheme();

  const hydrate = async () => {
    const res = await api.get(`/trade/${tradeId}`);
    console.log(res.data);
    const coin = (await getCryptoLive(res.data.asset)) as TypeCryptoLive;
    res.data.trade_logs = res.data.trade_logs.map((item: any) => ({
      ...item,
      asset: `${usdtToCrypto(item.value, coin)} ${res.data.asset}`,
      is_bot: item?.msg.length > 0,
    }));
    if (res.data.trade_status === "Open") {
      setData(res.data);
    }
    if (firstTime.current) {
      firstTime.current = false;
      setData(res.data);
    }
  };

  useEffect(() => {
    setInterval(() => {
      hydrate();
    }, 4000);
  }, [tradeId, user, firstTime]);

  const tripChart = {
    type: "line",
    height: 300,
    series: [
      {
        name: "Price",
        data: data?.trade_logs?.map((item: any) => ({
          x: new Date(+item.date * 1000),
          y: [+Number(item.value).toFixed(2)],
        })),
      },
    ],
    options: {
      chart: {
        toolbar: {
          show: false,
        },
      },
      dataLabels: {
        enabled: false,
      },
      colors: ["#7C3AED"],
      stroke: {
        lineCap: "round",
        curve: "smooth",
      },
      markers: {
        size: 0,
      },
      xaxis: {
        axisTicks: {
          show: false,
        },
        type: "datetime",
        axisBorder: {
          show: false,
        },
        labels: {
          style: {
            colors: theme === "dark" ? "#fff" : "#616161",
            fontSize: "12px",
            fontFamily: "inherit",
            fontWeight: 400,
          },
        },
      },
      yaxis: {
        labels: {
          style: {
            colors: theme === "dark" ? "#fff" : "#616161",
            fontSize: "12px",
            fontFamily: "inherit",
            fontWeight: 400,
          },
        },
      },
      grid: {
        show: true,
        borderColor: "#dddddd",
        strokeDashArray: 0,
        xaxis: {
          lines: {
            show: false,
          },
        },
        padding: {
          top: 5,
          right: 20,
        },
      },
      fill: {
        opacity: 0.8,
      },
      tooltip: {
        theme: "dark",
      },
    },
  };

  if (!data.trade_logs)
    return (
      <div className="flex flex-col gap-6 min-h-screen min-w-full">
        <Navbar />
        <div className="w-full bg-inherit bg-no-repeat bg-cover min-h-96 justify-center items-center flex flex-col">
          <h1 className="bg-clip-text text-transparent font-bold text-7xl bg-gradient-to-r from-[#5350F6] to-[#E662FE] mt-20 text-center">
            <LoaderIcon className="w-10 h-10 animate-spin mx-auto text-primary" />
          </h1>
        </div>
      </div>
    );
  return (
    <div className="flex flex-col gap-6 min-h-screen min-w-full">
      <Navbar />
      <div className="w-full bg-inherit bg-no-repeat bg-cover min-h-96 flex flex-col justify-center items-center p-4">
        <div className="w-full h-full flex flex-col bg-muted overflow-hidden rounded-sm p-4">
          <div className="flex flex-row w-full justify-between items-start flex-wrap">
            <Label className="text-2xl font-bold ">Trade view</Label>
            <div className="flex flex-row flex-wrap gap-4 justify-start items-center">
              <Label className="text-lg font-bold">
                Open: {data.trade_status === "Open" ? "✅" : "❌"}
              </Label>
              <Label className="text-lg font-bold ">
                Created at:{" "}
                <span className="font-normal opacity-80">
                  {
                    new Date(+data.trade_logs[0].date * 1000)
                      .toLocaleString()
                      .split(",")[0]
                  }
                </span>
              </Label>
              <Label className="text-lg font-bold ">
                Type:{" "}
                <span className="font-normal opacity-80">
                  {data.trade_type}
                </span>
              </Label>
              <Label className="text-lg font-bold ">
                Time profile:{" "}
                <span className="font-normal opacity-80">
                  {data.window_money == "1d"
                    ? "Slow"
                    : data.window_money == "1h"
                    ? "Medium"
                    : "Fast"}
                </span>
              </Label>
              <Button
                onClick={() => {
                  const url = `${api.defaults.baseURL}/static/${data.trade_id}.csv`;
                  window.open(url, "_blank");
                }}
              >
                Export to CSV
              </Button>
            </div>
          </div>
          <div className="flex flex-col w-full justify-center items-stretch overflow-hidden">
            <Chart {...tripChart} />
          </div>
        </div>
        <div className="mt-4 flex flex-col w-full justify-center items-stretch overflow-hidden bg-muted p-4 rounded-sm">
          <TradeTable data={data.trade_logs} />
        </div>
      </div>
    </div>
  );
}

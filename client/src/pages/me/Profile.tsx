import { Navbar } from "@/components/navbar";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/hooks/useAuth";
import { useNavigate } from "react-router-dom";

import Chart from "react-apexcharts";
import { getChart, TypeChartData } from "@/lib/api";
import { useEffect, useState } from "react";

export function ProfilePage() {
  const { user, signOut } = useAuth();

  const router = useNavigate();

  const handleLogout = async () => {
    await signOut();
    router("/auth");
  };

  const [chartData, setChartData] = useState<TypeChartData[]>([]);

  const chartConfig = {
    type: "line",
    height: 240,
    series: [
      {
        name: "Sales",
        data: chartData.map((item: TypeChartData) => item.yprice),
      },
    ],
    options: {
      chart: {
        toolbar: {
          show: false,
        },
      },
      title: {
        show: "",
      },
      dataLabels: {
        enabled: false,
      },
      colors: ["#fff"],
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
        axisBorder: {
          show: false,
        },
        labels: {
          style: {
            colors: "#616161",
            fontSize: "0px",
            fontFamily: "inherit",
            fontWeight: 400,
          },
        },
        categories: chartData.map((item: TypeChartData) => item.xtime),
      },
      yaxis: {
        labels: {
          style: {
            colors: "#616161",
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

  useEffect(() => {
    (async () => {
      const data = await getChart({ interval: "1d" });
      setChartData(data);
    })();
  }, []);

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <div className="w-full bg-inherit bg-no-repeat bg-cover justify-center items-center flex flex-col gap-10 flex-wrap p-10">
        <h1 className="w-full font-black text-3xl justify-start items-center text-left flex flex-row gap-4">
          <p>My Profile</p>
        </h1>
        <div className="w-full flex flex-row gap-10 max-sm:flex-col">
          <section className="flex-1">
            <Chart {...chartConfig} />
          </section>
          <aside className="flex flex-col flex-1 max-w-xs p-4 bg-muted rounded-md overflow-hidden gap-3 h-fit">
            <div className="flex flex-row gap-3 justify-start items-center flex-wrap">
              <img
                className="w-20 h-20 aspect-square object-cover object-center rounded-full border-4 border-primary"
                src="user.png"
                alt=""
              />
              <p>{user?.login}</p>
            </div>
            <Button
              onClick={handleLogout}
              className="flex max-sm:w-full justify-center items-center"
            >
              Logout
            </Button>
          </aside>
        </div>
      </div>
    </div>
  );
}

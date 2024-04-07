/* eslint-disable react-hooks/exhaustive-deps */
import { Navbar } from "@/components/navbar";

import { useAuth } from "@/lib/hooks/useAuth";
import { useNavigate } from "react-router-dom";

import {
  getChartCandle,
  getChartLine,
  getCryptoLive,
  getPossibleCurrencies,
  TypeChartDataCandle,
  TypeChartDataLine,
  TypeCryptoLive,
  TypeCurrency,
} from "@/lib/api";
import { useContext, useEffect, useState } from "react";
import Chart from "react-apexcharts";

import { Input } from "@/components/ui/input";
import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarTrigger,
} from "@/components/ui/menubar";
import { Label } from "@radix-ui/react-dropdown-menu";
import { Camera, CirclePlus, Wallet } from "lucide-react";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

import { CreateTrade } from "@/components/create-trade";
import { Button } from "@/components/ui/button";
import { DialogClose } from "@radix-ui/react-dialog";
import { SuccessContext } from "@/lib/contexts/success.context";
import { ErrorContext } from "@/lib/contexts/error.context";
import { TradesTable } from "./trades-entry.table";
import { useTheme } from "@/components/theme-provider";
import api from "@/lib/api/api";

export function ProfilePage() {
  const { successMessage, setSuccessMessage } = useContext(SuccessContext);
  const { errorMessage, setErrorMessage } = useContext(ErrorContext);

  const { user, signOut, addAmount, update, save, withdrawAmount } = useAuth();

  const router = useNavigate();

  const handleLogout = async () => {
    await signOut();
    router("/auth");
  };

  const handleSave = async () => {
    if (!user) {
      return;
    }
    const newRisk = user.risk;
    if (newRisk < 5 || newRisk > 80) {
      setErrorMessage("Invalid risk percentage");
      return;
    }
    await update({
      ...user,
      currency: "USDT",
      risk: newRisk,
    });
    const res = await save({
      ...user,
      currency: "USDT",
      risk: newRisk,
    });

    if (photo) {
      const formData = new FormData();
      formData.append("img", photo);
      const resImg = await api.post("/auth/me/photo", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      if (resImg) {
        await update({
          ...user,
          urlImg: resImg.data.urlImg,
        });
        await save({
          ...user,
          urlImg: resImg.data.urlImg,
        });
      }
    }

    if (!res) {
      setErrorMessage("Error saving data");
      return;
    }
    router("/me");
  };

  const [chartDataLine, setChartDataLine] = useState<TypeChartDataLine[]>([]);
  const [chartDataCandle, setChartDataCandle] = useState<TypeChartDataCandle[]>(
    []
  );

  const [possibleCurrencies, setPossibleCurrencies] = useState<TypeCurrency[]>(
    []
  );

  const [selectedCrypto, setSelectedCrypto] = useState<"BTC" | "ETH">("BTC"); // 'BTC' | 'ETH'
  const [selectedInterval, setSelectedInterval] = useState<
    "1h" | "1d" | "1w" | "1m"
  >("1d"); // '1h' | '1d' | '1w' | '1m'
  const [selectedType, setSelectedType] = useState<"line" | "candle">("line"); // 'line' | 'candle'
  const [selectedCurrency, setSelectedCurrency] = useState(user?.currency);

  const [selectedAmount, setSelectedAmount] = useState(0);
  const handleAddAmount = () => {
    addAmount(selectedAmount).then((res) => {
      if (!res) setErrorMessage("Error adding amount");
      setSelectedAmount(0);
    });
  };

  const handleWithdrawAmount = () => {
    withdrawAmount(selectedAmount).then((res) => {
      if (!res) setErrorMessage("Error withdrawing amount");
      setSelectedAmount(0);
    });
  };

  const { theme } = useTheme();

  const candleChartConfig = {
    type: "candlestick",
    height: 300,
    series: [
      {
        data: chartDataCandle.map((item: TypeChartDataCandle) => ({
          x: new Date(item.openTime),
          y: [item.openPrice, item.highPrice, item.lowPrice, item.closePrice],
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
        // categories: chartData.map((item: TypeChartData) => item.xtime),
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
      tooltip: {},
    },
  };

  const lineChartConfig = {
    type: "line",
    height: 300,
    series: [
      {
        name: "Sales",
        data: chartDataLine.map((item: TypeChartDataLine) => ({
          x: new Date(+item.xtime),
          y: [item.yprice],
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

  const [BTC, setBTC] = useState<TypeCryptoLive | null>(null);
  const [ETH, setETH] = useState<TypeCryptoLive | null>(null);

  const hydrate = async () => {
    const dataBTC = await getCryptoLive("BTC");
    if (dataBTC) {
      setBTC(dataBTC);
    }
    const dataETH = await getCryptoLive("ETH");
    if (dataETH) {
      setETH(dataETH);
    }
  };

  useEffect(() => {
    hydrate();
    setSelectedCurrency(user?.currency);
    (async () => {
      const data = await getPossibleCurrencies();
      setPossibleCurrencies(data);

      const dataLine = await getChartLine(selectedCrypto, selectedInterval);
      setChartDataLine(dataLine);
      const dataCandle = await getChartCandle(selectedCrypto, selectedInterval);
      setChartDataCandle(dataCandle);
    })();
  }, [router, selectedCrypto, selectedInterval, user, selectedCurrency]);

  useEffect(() => {}, [user]);

  useEffect(() => {
    setInterval(() => {
      hydrate();
    }, 30000);
  }, []);

  const [isTradeDialogOpen, setIsTradeDialogOpen] = useState(false);

  const [photo, setPhoto] = useState<File | null>(null);

  if (user) {
    return (
      <div className="flex flex-col min-h-screen">
        <Navbar />
        <div className="w-full bg-inherit bg-no-repeat bg-cover justify-center items-center flex flex-col gap-10 flex-wrap p-10">
          <h1 className="w-full font-black text-3xl justify-start items-center text-left flex flex-row gap-4">
            <p>My Dashboard</p>
          </h1>
          <div className="w-full flex flex-row gap-10 max-sm:flex-col">
            <section className="flex-1 flex flex-col justify-start items-start gap-4">
              <div className="w-full bg-muted rounded-md overflow-hidden h-fit p-4 flex flex-col justify-start">
                <div className="flex flex-row gap-4 justify-between items-center flex-wrap w-full">
                  <p className="text-2xl font-bold">Markets</p>
                  <Menubar className="bg-muted">
                    <MenubarMenu>
                      <MenubarTrigger>Crypto: {selectedCrypto}</MenubarTrigger>
                      <MenubarContent>
                        <MenubarItem onClick={() => setSelectedCrypto("BTC")}>
                          BTC
                        </MenubarItem>
                        <MenubarItem onClick={() => setSelectedCrypto("ETH")}>
                          ETH
                        </MenubarItem>
                      </MenubarContent>
                    </MenubarMenu>
                    <MenubarMenu>
                      <MenubarTrigger>
                        Interval: {selectedInterval}
                      </MenubarTrigger>
                      <MenubarContent>
                        <MenubarItem onClick={() => setSelectedInterval("1h")}>
                          1h
                        </MenubarItem>
                        <MenubarItem onClick={() => setSelectedInterval("1d")}>
                          1d
                        </MenubarItem>
                        <MenubarItem onClick={() => setSelectedInterval("1w")}>
                          1w
                        </MenubarItem>
                        <MenubarItem onClick={() => setSelectedInterval("1m")}>
                          1m
                        </MenubarItem>
                      </MenubarContent>
                    </MenubarMenu>
                    <MenubarMenu>
                      <MenubarTrigger>Type: {selectedType}</MenubarTrigger>
                      <MenubarContent>
                        <MenubarItem onClick={() => setSelectedType("line")}>
                          Line
                        </MenubarItem>
                        <MenubarItem onClick={() => setSelectedType("candle")}>
                          Candle
                        </MenubarItem>
                      </MenubarContent>
                    </MenubarMenu>
                  </Menubar>
                </div>
                {selectedType === "candle" ? (
                  <Chart {...candleChartConfig} />
                ) : (
                  <Chart {...lineChartConfig} />
                )}
              </div>

              <div className="w-full bg-muted rounded-md overflow-hidden h-fit p-4 flex flex-col justify-start">
                <div className="flex flex-row gap-4 justify-between items-center flex-wrap w-full">
                  <p className="text-2xl font-bold">Trades</p>
                  <Button
                    onClick={() => setIsTradeDialogOpen(true)}
                    className="px-2 flex flex-row gap-2 flex-wrap justify-center items-center h-max"
                  >
                    <span className="flex flex-col justify-center items-center text-center">
                      Create trade
                    </span>
                    <CirclePlus />
                  </Button>
                </div>
                {BTC &&
                  ETH &&
                  selectedCurrency &&
                  user &&
                  possibleCurrencies.length > 0 && (
                    <TradesTable BTC={BTC} ETH={ETH} />
                  )}
              </div>
              {BTC &&
                ETH &&
                selectedCurrency &&
                user &&
                possibleCurrencies.length > 0 && (
                  <CreateTrade
                    isOpen={isTradeDialogOpen}
                    onClose={() => setIsTradeDialogOpen(false)}
                    BTC={BTC}
                    ETH={ETH}
                    selectedCurrency={selectedCurrency}
                    possibleCurrencies={possibleCurrencies}
                    user={user}
                  />
                )}
            </section>
            <aside className="flex flex-col gap-6 justify-start items-start flex-wrap flex-1 max-w-sm max-sm:max-w-full">
              <div className="flex flex-col p-4 bg-muted rounded-md overflow-hidden gap-3 h-fit w-full">
                <div className="flex flex-row gap-3 justify-start items-center flex-wrap">
                  <div className="relative">
                    <img
                      className="w-20 h-20 aspect-square object-cover object-center rounded-full border-[3px] border-primary"
                      src={
                        photo || user?.urlImg === "user.png"
                          ? URL.createObjectURL(photo)
                          : api.defaults.baseURL + "/" + user?.urlImg ||
                            "user.png"
                      }
                      alt=""
                      onError={(e) => {
                        e.currentTarget.src = "user.png";
                      }}
                    />
                    <input
                      type="file"
                      id="file"
                      accept="image/*"
                      onChange={(e) => {
                        if (e.target.files) {
                          setPhoto(e.target.files[0]);
                        }
                      }}
                      className="hidden"
                    />
                    <div
                      onClick={() => {
                        document.getElementById("file")?.click();
                      }}
                      className="absolute bottom-0 right-0 bg-muted border-primary rounded-full p-1 border-[3px]"
                    >
                      <Camera size={20} />
                    </div>
                  </div>
                  <p>{user?.login}</p>
                </div>
                <div className="flex flex-row justify-between gap-3 items-center flex-wrap">
                  <Label className="w-fit font-semibold">
                    Risk percentage:
                  </Label>
                  <Input
                    type="number"
                    min={5}
                    max={80}
                    defaultValue={user?.risk}
                    className="flex-1"
                    onChange={(e) => {
                      if (user) {
                        update({
                          ...user,
                          risk: +e.target.value,
                        });
                      }
                    }}
                  />
                </div>
                <div className="flex flex-row gap-3 justify-between items-center flex-wrap">
                  <Button
                    onClick={handleSave}
                    className="flex flex-1 max-sm:w-full justify-center items-center"
                  >
                    Save
                  </Button>
                  <Button
                    onClick={handleLogout}
                    className="flex flex-1 max-sm:w-full justify-center items-center"
                  >
                    Logout
                  </Button>
                </div>
              </div>
              <div className="flex flex-col gap-4 justify-start items-start flex-wrap w-full">
                <div className="flex flex-col w-full p-4 bg-muted rounded-md overflow-hidden gap-3 h-fit">
                  <div className="flex flex-col gap-1 justify-center items-start text-xl font-bold">
                    <p>Invested</p>
                    <div className="flex flex-row gap-3 justify-start items-center text-xl font-bold">
                      <Wallet size={25} />
                      <p>
                        {Number(user?.lucro + user?.balanceInvested).toFixed(2)}{" "}
                        (
                        {
                          possibleCurrencies.find(
                            (currency) => currency.name === user?.currency
                          )?.token
                        }
                        )
                      </p>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col w-full p-4 bg-muted rounded-md overflow-hidden gap-3 h-fit">
                  <div className="flex flex-col gap-1 justify-center items-start text-xl font-bold">
                    <p>Saved</p>
                    <div className="flex flex-row gap-3 justify-start items-center text-xl font-bold">
                      <Wallet size={25} />
                      <p>
                        {user?.balanceAvailable} (
                        {
                          possibleCurrencies.find(
                            (currency) => currency.name === user?.currency
                          )?.token
                        }
                        )
                      </p>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col w-full p-4 bg-muted rounded-md overflow-hidden gap-4 h-fit">
                  <div className="flex flex-col gap-3 justify-center items-start text-xl font-bold">
                    <p>
                      Total:{" "}
                      <span>
                        {user?.balanceTotal} (
                        {
                          possibleCurrencies.find(
                            (currency) => currency.name === user?.currency
                          )?.token
                        }
                        )
                      </span>
                    </p>
                    <div className="flex flex-row gap-3 justify-start items-center text-xl font-bold w-full">
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button className="flex-1">Add amount</Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                          <DialogHeader>
                            <DialogTitle>Add amount</DialogTitle>
                            <DialogDescription>
                              You can add more amount to your account
                            </DialogDescription>
                          </DialogHeader>
                          <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                              <Label className="text-left">Amount:</Label>
                              <Input
                                type="number"
                                min={0}
                                max={100000}
                                id="amount"
                                className="col-span-3"
                                onChange={(e) =>
                                  setSelectedAmount(+e.target.value)
                                }
                              />
                            </div>
                          </div>
                          <DialogFooter>
                            <DialogClose asChild>
                              <Button onClick={handleAddAmount} type="submit">
                                Add
                              </Button>
                            </DialogClose>
                          </DialogFooter>
                        </DialogContent>
                      </Dialog>
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button className="flex-1">Withdraw</Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                          <DialogHeader>
                            <DialogTitle>Withdraw amount</DialogTitle>
                            <DialogDescription>
                              You can withdraw amount from your account
                            </DialogDescription>
                          </DialogHeader>
                          <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                              <Label className="text-left">Amount:</Label>
                              <Input
                                type="number"
                                min={0}
                                max={100000}
                                onChange={(e) =>
                                  setSelectedAmount(+e.target.value)
                                }
                                value={selectedAmount}
                                id="amount"
                                className="col-span-3"
                              />
                            </div>
                          </div>
                          <DialogFooter>
                            <DialogClose asChild>
                              <Button
                                onClick={handleWithdrawAmount}
                                type="submit"
                              >
                                Withdraw
                              </Button>
                            </DialogClose>
                          </DialogFooter>
                        </DialogContent>
                      </Dialog>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col gap-4 justify-start items-start flex-wrap w-full">
                  {BTC && selectedCurrency && (
                    <AssetComponent
                      crypto={BTC}
                      selectedCurrency={selectedCurrency}
                      possibleCurrencies={possibleCurrencies}
                      cryptoName="Bitcoin"
                    />
                  )}

                  {ETH && selectedCurrency && (
                    <AssetComponent
                      crypto={ETH}
                      selectedCurrency={selectedCurrency}
                      possibleCurrencies={possibleCurrencies}
                      cryptoName="Ethereum"
                    />
                  )}
                </div>
              </div>
            </aside>
          </div>
        </div>
      </div>
    );
  }
}

interface AssetComponentProps {
  crypto: TypeCryptoLive;
  selectedCurrency: string;
  possibleCurrencies: TypeCurrency[];
  cryptoName: string;
}

export function AssetComponent({
  crypto,
  selectedCurrency,
  possibleCurrencies: possibleCurriencies,
  cryptoName,
}: AssetComponentProps) {
  return (
    <div className="flex flex-col w-full p-4 bg-muted rounded-md overflow-hidden gap-3 h-fit">
      <div className="flex flex-row gap-3 justify-start items-start text-xl font-bold flex-wrap">
        <img
          className="w-14 h-14 rounded-full object-cover object-center overflow-hidden"
          src={
            cryptoName.toLocaleLowerCase() === "bitcoin" ? "btc.png" : "eth.png"
          }
          alt={`${cryptoName} logo`}
        />
        <div className="flex flex-col flex-1 gap-1 justify-between items-center h-fit">
          <div className="flex flex-row gap-1 justify-between items-center w-full">
            <p className="text-base font-semibold">
              {cryptoName} (BTC-
              {
                possibleCurriencies.find(
                  (currency) => currency.name === selectedCurrency
                )?.token
              }
              )
            </p>
            <p className="text-base font-light opacity-70">
              {Number(crypto.ask).toFixed(2)}
            </p>
          </div>
          <div className="flex flex-row gap-1 justify-between items-center w-full">
            <p className="text-sm font-light opacity-70">
              Spread: {Number(crypto.spread_percentage).toFixed(2)}%
            </p>
            <p
              className={`text-sm font-light opacity-70 ${
                crypto.day_percent_change > 0
                  ? "text-green-500"
                  : "text-red-500"
              }`}
            >
              Day change: {Number(crypto.day_percent_change).toFixed(2)}%
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

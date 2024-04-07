// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";
import { User } from "../hooks/useAuth";
import api from "./api";

const doAuth = async (login: string, password: string): Promise<boolean> => {
  const res = await api.post("/auth/login", { login, password });

  if (res.status !== 200) {
    return false;
  }

  api.defaults.headers.Authorization = `Bearer ${res.data.token}`;
  Cookies.set("token", res.data.token);

  Cookies.set(
    "user",
    JSON.stringify({
      login,
      password,
    })
  );
  return true;
};

const getMe = async () => {
  const res = await api.get("/auth/me");

  if (res.status !== 200) {
    return null;
  }

  return res.data;
};

export type TypeChartDataLine = {
  yprice: number;
  xtime: string;
};

export type TypeChartDataCandle = {
  openPrice: number;
  closePrice: number;
  highPrice: number;
  lowPrice: number;
  openTime: string;
};

const getChart = async ({
  idCoin = "BTC",
  interval = "1d",
  type = "line",
}: {
  idCoin?: "BTC" | "ETH";
  type?: "candle" | "line";
  interval: "1h" | "1d" | "1w" | "1m";
}): Promise<TypeChartDataLine[] | TypeChartDataCandle[]> => {
  // set headers if not already set and cookie is present
  if (!api.defaults.headers.Authorization) {
    const token = Cookies.get("token");
    if (token) {
      api.defaults.headers.Authorization = `Bearer ${token}`;
    }
  }

  const res = await api.get(
    `/coin/${idCoin}/chart?type=${type}&interval=${interval}`
  );

  if (res.status !== 200) {
    return [];
  }

  return res.data;
};

const getChartLine = async (
  idCoin: "BTC" | "ETH" = "BTC",
  interval: "1h" | "1d" | "1w" | "1m" = "1d"
) => {
  return (await getChart({
    idCoin,
    interval,
    type: "line",
  })) as TypeChartDataLine[];
};

const getChartCandle = async (
  idCoin: "BTC" | "ETH" = "BTC",
  interval: "1h" | "1d" | "1w" | "1m" = "1d"
) => {
  return (await getChart({
    idCoin,
    interval,
    type: "candle",
  })) as TypeChartDataCandle[];
};

export type TypeCurrency = {
  name: string;
  token: string;
};

const getPossibleCurrencies = async (): Promise<TypeCurrency[]> => {
  const res = await api.get("/coin/currency");

  if (res.status !== 200) {
    return [];
  }

  return res.data;
};

const save = async (data: User): Promise<boolean> => {
  try {
    const allCurrencies = await getPossibleCurrencies();

    const res = await api.put("/auth/me", {
      risk: data.risk,
      balanceAvailable: data.balanceAvailable.toString(),
      currency: "USDT",
    });

    if (res.status !== 200) {
      return false;
    }

    return true;
  } catch (e) {
    return false;
  }
};

export type TypeCryptoLive = {
  ask: number;
  bid: number;
  currency: string;
  spread: number;
  spread_percentage: number;
  day_percent_change: number;
};

function roundUntilNonZero(number: number): number {
  let roundedNumber = number;

  // Keep rounding until the first non-zero digit appears
  while (Math.abs(roundedNumber) < 0.001 && roundedNumber !== 0) {
    roundedNumber *= 10;
  }

  // Round the number to three decimal places
  roundedNumber = Math.round(roundedNumber * 1000) / 1000;

  return roundedNumber;
}

function usdtToCrypto(usdt: number, crypto: TypeCryptoLive): number {
  return usdt / crypto.ask;
}

const getCryptoLive = async (
  coin: "BTC" | "ETH"
): Promise<TypeCryptoLive | null> => {
  const res = await api.get(`/coin/${coin}`);

  if (res.status !== 200) {
    return null;
  }

  // To get day_percent_change we need to calculate it, so we need candle data
  const candleData = await getChartCandle(coin, "1d");

  if (candleData.length < 2) {
    return null;
  }

  const day_percent_change =
    ((candleData[candleData.length - 1].closePrice -
      candleData[candleData.length - 1].openPrice) /
      candleData[candleData.length - 1].openPrice) *
    100;

  return {
    ...res.data,
    day_percent_change,
  };
};

export {
  doAuth,
  getChartCandle,
  getChartLine,
  getMe,
  getPossibleCurrencies,
  save,
  getCryptoLive,
  usdtToCrypto,
};

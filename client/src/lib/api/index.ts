import axios from "axios";
// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";

const api = axios.create({
  baseURL: "http://10.14.0.41:8080",
});

const doAuth = async (login: string, password: string): Promise<boolean> => {
  const res = await api.post("/auth/login", { login, password });

  if (res.status !== 200) {
    return false;
  }

  api.defaults.headers.Authorization = `Bearer ${res.data.token}`;

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

export type TypeChartData = {
  yprice: number;
  xtime: string;
};

const getChart = async ({
  idCoin = "BTCUSDT",
  type = "line",
  interval = "1d",
}: {
  idCoin?: string;
  type?: "candle" | "line";
  interval: "1h" | "1d" | "1w" | "1m";
}): Promise<TypeChartData[]> => {
  const res = await api.get(
    `/coin/${idCoin}/chart?type=${type}&interval=${interval}`
  );

  if (res.status !== 200) {
    return [];
  }

  return res.data;
};

export { api, doAuth, getMe, getChart };

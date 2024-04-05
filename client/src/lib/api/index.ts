import axios from "axios";
// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";

const api = axios.create({
  baseURL: "http://26.108.87.238:8080",
  headers: {
    Authorization: `Bearer ${Cookies.get("userjwt")}`,
  },
});

const login = async (user: string, login: string): Promise<boolean> => {
  const res = await api.post("/auth", { user, login });

  if (res.status !== 200) {
    return false;
  }

  Cookies.set("userjwt", res.data.token);
  api.defaults.headers.Authorization = `Bearer ${res.data.jwt}`;
  return true;
};

const createUser = async (user: string, login: string) => {
  await api.post("/users", { user, login });
};

export { api, login, createUser };

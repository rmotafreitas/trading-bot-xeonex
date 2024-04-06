import axios from "axios";

const api = axios.create({
  baseURL: "http://10.14.0.41:8080",
});

export default api;

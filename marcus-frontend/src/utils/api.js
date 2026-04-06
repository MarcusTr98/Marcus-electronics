import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("ACCESS_TOKEN");

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

api.interceptors.response.use(
  (response) => {
    // Nếu API gọi thành công (Status 2xx), trả nguyên data về
    return response;
  },
  (error) => {
    // Nếu API gọi thất bại
    if (error.response && error.response.status === 401) {
      console.warn("Token đã hết hạn hoặc không hợp lệ. Đăng xuất tự động.");
      // Xóa ví
      localStorage.removeItem("ACCESS_TOKEN");
      localStorage.removeItem("USER_INFO");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export default api;

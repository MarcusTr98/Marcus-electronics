import axios from "axios";

// 1. Tạo một bản sao của axios với cấu hình mặc định
const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  timeout: 10000,
});

// 2. Trạm gác TRƯỚC KHI request bay lên server (Interceptor)
api.interceptors.request.use(
  (config) => {
    // Lấy token từ "Ví" localStorage
    const token = localStorage.getItem("ACCESS_TOKEN");

    // Nếu có token, tự động gắn vào Header Authorization theo chuẩn Bearer
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default api;

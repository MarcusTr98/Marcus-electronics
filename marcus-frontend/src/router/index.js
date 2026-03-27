import { createRouter, createWebHistory } from "vue-router";
import ClientLayout from "../layouts/ClientLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";
import HomeView from "../views/client/HomeView.vue";
import CartView from "../views/client/CartView.vue";
import CheckoutView from "../views/client/CheckoutView.vue";
import ProductDetail from "../views/client/ProductDetail.vue";
import LoginView from "../views/auth/LoginView.vue";
import DashboardView from "../views/admin/DashboardView.vue";

const routes = [
  // --- NHÓM TRANG KHÁCH HÀNG (Có Header/Footer) ---
  {
    path: "/",
    component: ClientLayout,
    children: [
      { path: "", name: "home", component: HomeView },
      { path: "cart", name: "cart", component: CartView },
      { path: "checkout", name: "checkout", component: CheckoutView },
      {
        path: "product/:slug",
        name: "product-detail",
        component: ProductDetail,
      },
    ],
  },

  // --- TRANG LOGIN (Không Header/Footer) ---
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },

  // --- NHÓM TRANG ADMIN (Có Sidebar/Dashboard) ---
  {
    path: "/admin",
    component: AdminLayout,
    children: [{ path: "", name: "admin-dashboard", component: DashboardView }],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

//NAVIGATION GUARD
router.beforeEach((to, from, next) => {
  // Lấy thông tin từ LocalStorage
  const token = localStorage.getItem("jwt_token");
  const role = localStorage.getItem("user_role") || "";

  // Nếu người dùng cố tình truy cập vào bất kỳ route nào bắt đầu bằng '/admin'
  if (to.path.startsWith("/admin")) {
    if (!token) {
      alert("Truy cập bị từ chối: Bạn chưa đăng nhập!");
      next("/login"); // Trục xuất về trang Login
    } else if (!role.includes("ADMIN")) {
      alert("Truy cập bị từ chối: Bạn không có quyền Quản trị viên!");
      next("/"); // Trục xuất về trang chủ
    } else {
      next(); // Hợp lệ, cho phép đi tiếp
    }
  } else {
    // Các trang không phải admin thì ai cũng được vào
    next();
  }
});

export default router;

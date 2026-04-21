import { createRouter, createWebHistory } from "vue-router";
import ClientLayout from "../layouts/ClientLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";

// Khách hàng
import HomeView from "../views/client/HomeView.vue";
import CartView from "../views/client/CartView.vue";
import CheckoutView from "../views/client/CheckoutView.vue";
import ProductDetail from "../views/client/ProductDetail.vue";
import LoginView from "../views/auth/LoginView.vue";

// Quản trị viên
import DashboardView from "../views/admin/DashboardView.vue";
import ProductManageView from "../views/admin/ProductManageView.vue";
import CategoryManageView from "../views/admin/CategoryManageView.vue";
import ProductVariants from "../views/admin/ProductVariants.vue";
import InventoryManager from "../views/admin/InventoryManager.vue";
import ProductVariantDetail from "../views/admin/ProductVariantDetail.vue";
import CustomerManageView from "../views/admin/CustomerManageView.vue";
import OrderManageView from "../views/admin/OrderManageView.vue";

import OrderSuccessView from "../views/client/OrderSuccessView.vue";
import AllProductsView from "../views/client/AllProductsView.vue";

const routes = [
  // --- NHÓM TRANG KHÁCH HÀNG (Có Header/Footer)
  {
    path: "/",
    component: ClientLayout,
    children: [
      { path: "", name: "home", component: HomeView },
      { path: "cart", name: "cart", component: CartView },
      { path: "checkout", name: "checkout", component: CheckoutView },
      {
        path: "order-success",
        name: "order-success",
        component: OrderSuccessView,
      },
      { path: "products", name: "products", component: AllProductsView },
      {
        path: "product/:slug",
        name: "product-detail",
        component: ProductDetail,
      },
    ],
  },

  // --- TRANG LOGIN (Không Header/Footer)
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },

  // --- NHÓM TRANG ADMIN
  {
    path: "/admin",
    component: AdminLayout,
    children: [
      {
        path: "",
        name: "admin-dashboard",
        component: DashboardView,
      },
      {
        path: "products",
        name: "admin-products",
        component: ProductManageView,
      },
      {
        path: "categories",
        name: "AdminCategories",
        component: CategoryManageView,
      },
      {
        path: "products/variants",
        name: "admin-products-variants",
        component: ProductVariants,
      },
      {
        path: "products/:id/variants",
        name: "admin-product-variant-detail",
        component: ProductVariantDetail,
      },
      {
        path: "products/inventory",
        name: "admin-products-inventory",
        component: InventoryManager,
      },
      {
        path: "customers",
        name: "admin-customers",
        component: CustomerManageView,
      },
      {
        path: "orders",
        name: "admin-orders",
        component: OrderManageView,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, behavior: "smooth" };
    }
  },
});

// NAVIGATION GUARD
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("ACCESS_TOKEN");
  const role = localStorage.getItem("USER_ROLE") || "";

  if (to.path.startsWith("/admin")) {
    if (!token) {
      alert("Truy cập bị từ chối: Bạn chưa đăng nhập!");
      next("/login");
    } else if (!role.includes("ADMIN")) {
      alert("Truy cập bị từ chối: Bạn không có quyền Quản trị viên!");
      next("/");
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;

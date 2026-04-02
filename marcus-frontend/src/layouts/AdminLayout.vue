<script setup>
import { ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

// ==================== STATE ====================
const sidebarActive = ref(true);
const ordersOpen = ref(false);
const productsOpen = ref(false);
const userMenuOpen = ref(false);

watch(
  () => route.path,
  (path) => {
    if (path.startsWith("/admin/products")) productsOpen.value = true;
    if (path.startsWith("/admin/orders")) ordersOpen.value = true;
  },
  { immediate: true },
);

// ==================== HANDLERS ====================
const toggleSidebar = () => {
  sidebarActive.value = !sidebarActive.value;
};

const handleLogout = () => {
  if (!confirm("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?")) return;
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  router.push("/login");
};
</script>

<template>
  <div id="app">
    <!-- ===== SIDEBAR ===== -->
    <div id="sidebar" :class="{ active: sidebarActive }">
      <div class="sidebar-wrapper" :class="{ active: sidebarActive }">
        <!-- Logo -->
        <div class="sidebar-header">
          <div class="d-flex justify-content-between align-items-center">
            <div class="logo">
              <router-link to="/admin">
                <span class="logo-text">
                  <i class="bi bi-lightning-charge-fill text-warning"></i>
                  Marcus-E Admin
                </span>
              </router-link>
            </div>
            <div class="toggler">
              <a
                href="#"
                class="sidebar-hide d-xl-none d-block"
                @click.prevent="toggleSidebar"
              >
                <i class="bi bi-x bi-middle"></i>
              </a>
            </div>
          </div>
        </div>

        <!-- Menu -->
        <div class="sidebar-menu">
          <ul class="menu">
            <!-- Tổng quan -->
            <li class="sidebar-title">Tổng quan</li>
            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin' }"
            >
              <router-link to="/admin" class="sidebar-link">
                <i class="bi bi-grid-fill"></i>
                <span>Bảng điều khiển</span>
              </router-link>
            </li>

            <!-- Kinh doanh -->
            <li class="sidebar-title">Kinh doanh</li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/categories' }"
            >
              <router-link to="/admin/categories" class="sidebar-link">
                <i class="bi bi-diagram-3"></i>
                <span>Danh mục</span>
              </router-link>
            </li>

            <!-- Sản phẩm (có submenu) -->
            <li class="sidebar-item has-sub" :class="{ open: productsOpen }">
              <a
                href="#"
                class="sidebar-link"
                @click.prevent="productsOpen = !productsOpen"
              >
                <i class="bi bi-laptop"></i>
                <span>Sản phẩm</span>
              </a>
              <ul class="submenu" :class="{ open: productsOpen }">
                <li class="submenu-item">
                  <router-link to="/admin/products">
                    <i class="bi bi-circle-fill"></i> Sản phẩm gốc
                  </router-link>
                </li>
                <li class="submenu-item">
                  <router-link to="/admin/products/variants">
                    <i class="bi bi-circle-fill"></i> Quản lý biến thể
                  </router-link>
                </li>
                <li class="submenu-item">
                  <router-link to="/admin/products/inventory">
                    <i class="bi bi-circle-fill"></i> Quản lý Tồn kho
                  </router-link>
                </li>
              </ul>
            </li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/customers' }"
            >
              <router-link to="/admin/customers" class="sidebar-link">
                <i class="bi bi-people-fill"></i>
                <span>Khách hàng</span>
              </router-link>
            </li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/orders' }"
            >
              <router-link to="/admin/orders" class="sidebar-link">
                <i class="bi bi-cart-fill"></i>
                <span>Đơn hàng</span>
              </router-link>
            </li>

            <!-- Nội dung & Phân tích -->
            <li class="sidebar-title">Nội dung & Phân tích</li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/blogs' }"
            >
              <router-link to="/admin/blogs" class="sidebar-link">
                <i class="bi bi-file-earmark-text-fill"></i>
                <span>Bài viết (Blog)</span>
              </router-link>
            </li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/reports' }"
            >
              <router-link to="/admin/reports" class="sidebar-link">
                <i class="bi bi-bar-chart-fill"></i>
                <span>Báo cáo doanh thu</span>
              </router-link>
            </li>

            <!-- Hệ thống -->
            <li class="sidebar-title">Hệ thống</li>

            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/users' }"
            >
              <router-link to="/admin/users" class="sidebar-link">
                <i class="bi bi-person-badge-fill"></i>
                <span>Quản trị viên</span>
              </router-link>
            </li>

            <li class="sidebar-item">
              <a href="#" class="sidebar-link">
                <i class="bi bi-gear-fill"></i>
                <span>Cài đặt hệ thống</span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- ===== MAIN ===== -->
    <div id="main">
      <header class="mb-3">
        <div
          class="header-top d-flex align-items-center justify-content-between"
        >
          <a href="#" class="burger-btn" @click.prevent="toggleSidebar">
            <i class="bi bi-justify fs-3"></i>
          </a>

          <div class="header-right d-flex align-items-center gap-4">
            <!-- Thông báo -->
            <div class="notification-btn position-relative cursor-pointer">
              <i class="bi bi-bell fs-5 text-secondary"></i>
              <span
                class="badge bg-danger position-absolute top-0 start-100 translate-middle rounded-pill"
                style="font-size: 0.65rem"
                >3</span
              >
            </div>

            <!-- Tin nhắn -->
            <div class="message-btn position-relative cursor-pointer">
              <i class="bi bi-envelope fs-5 text-secondary"></i>
              <span
                class="badge bg-primary position-absolute top-0 start-100 translate-middle rounded-pill"
                style="font-size: 0.65rem"
                >5</span
              >
            </div>

            <!-- User dropdown -->
            <div class="user-dropdown-wrapper position-relative">
              <div
                class="user-dropdown d-flex align-items-center gap-2"
                style="cursor: pointer"
                @click="userMenuOpen = !userMenuOpen"
              >
                <div class="text-end d-none d-md-block">
                  <h6 class="mb-0 text-sm font-bold">Admin Marcus</h6>
                  <p class="text-xs text-muted mb-0">Super Admin</p>
                </div>
                <div class="avatar avatar-md">
                  <img
                    src="https://ui-avatars.com/api/?name=Marcus+Tran&background=435ebe&color=fff"
                    alt="Avatar"
                    class="rounded-circle"
                    style="width: 40px; height: 40px; object-fit: cover"
                  />
                </div>
              </div>

              <div
                v-if="userMenuOpen"
                class="dropdown-menu dropdown-menu-end show shadow position-absolute mt-2"
                style="top: 100%; right: 0; min-width: 160px; z-index: 1050"
              >
                <a class="dropdown-item" href="#"
                  ><i class="bi bi-person me-2"></i> Hồ sơ</a
                >
                <a class="dropdown-item" href="#"
                  ><i class="bi bi-gear me-2"></i> Cài đặt</a
                >
                <hr class="dropdown-divider" />
                <a
                  class="dropdown-item text-danger"
                  href="#"
                  @click.prevent="handleLogout"
                >
                  <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
                </a>
              </div>
            </div>
          </div>
        </div>
      </header>

      <router-view />
    </div>
  </div>
</template>

<style scoped>
@import "../assets/css/admin-layout.css";

.cursor-pointer {
  cursor: pointer;
}

.user-dropdown:hover h6 {
  color: #d94f6e;
}

.dropdown-menu {
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

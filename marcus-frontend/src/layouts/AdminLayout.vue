<template>
  <div id="app">
    <div id="sidebar" :class="{ active: sidebarActive }">
      <div class="sidebar-wrapper" :class="{ active: sidebarActive }">
        <div class="sidebar-header">
          <div class="d-flex justify-content-between align-items-center">
            <div class="logo">
              <router-link to="/admin">
                <span class="logo-text">
                  <i class="bi bi-lightning-charge-fill text-warning"></i>
                  Marcus Admin
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

        <div class="sidebar-menu">
          <ul class="menu">
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
            <li
              class="sidebar-item"
              :class="{ active: $route.path === '/admin/products' }"
            >
              <router-link to="/admin/products" class="sidebar-link">
                <i class="bi bi-laptop"></i>
                <span>Sản phẩm</span>
              </router-link>
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

            <li class="sidebar-item has-sub" :class="{ open: ordersOpen }">
              <a
                href="#"
                class="sidebar-link"
                @click.prevent="ordersOpen = !ordersOpen"
              >
                <i class="bi bi-cart-fill"></i>
                <span>Đơn hàng</span>
              </a>
              <ul class="submenu" :class="{ open: ordersOpen }">
                <li class="submenu-item">
                  <router-link to="/admin/orders"
                    ><i class="bi bi-circle"></i> Tất cả đơn</router-link
                  >
                </li>
                <li class="submenu-item">
                  <router-link to="/admin/orders/pending"
                    ><i class="bi bi-circle"></i> Chờ xử lý</router-link
                  >
                </li>
                <li class="submenu-item">
                  <router-link to="/admin/orders/completed"
                    ><i class="bi bi-circle"></i> Đã hoàn thành</router-link
                  >
                </li>
              </ul>
            </li>

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

    <div id="main">
      <header class="mb-3">
        <div
          class="header-top d-flex align-items-center justify-content-between"
        >
          <a href="#" class="burger-btn" @click.prevent="toggleSidebar">
            <i class="bi bi-justify fs-3"></i>
          </a>

          <div class="header-right d-flex align-items-center gap-4">
            <div class="notification-btn position-relative cursor-pointer">
              <i class="bi bi-bell fs-5 text-secondary"></i>
              <span
                class="badge bg-danger position-absolute top-0 start-100 translate-middle rounded-pill"
                style="font-size: 0.65rem"
              >
                3
              </span>
            </div>

            <div class="message-btn position-relative cursor-pointer">
              <i class="bi bi-envelope fs-5 text-secondary"></i>
              <span
                class="badge bg-primary position-absolute top-0 start-100 translate-middle rounded-pill"
                style="font-size: 0.65rem"
              >
                5
              </span>
            </div>

            <div
              class="user-dropdown d-flex align-items-center gap-2"
              style="cursor: pointer"
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
          </div>
        </div>
      </header>

      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const sidebarActive = ref(true);
const ordersOpen = ref(false);

const toggleSidebar = () => {
  sidebarActive.value = !sidebarActive.value;
};

// Gọi API lấy thông tin User Admin nếu cần ở đây
</script>

<style scoped>
@import "../assets/css/admin-layout.css";
.cursor-pointer {
  cursor: pointer;
}
.user-dropdown:hover h6 {
  color: #435ebe;
}
</style>

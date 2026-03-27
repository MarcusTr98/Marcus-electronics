<template>
  <div id="app">
    <div id="sidebar" class="active">
      <div class="sidebar-wrapper active">
        <div class="sidebar-header">
          <div class="d-flex justify-content-between">
            <div class="logo">
              <router-link to="/admin"
                ><h3 class="text-primary mt-2">MARCUS ADMIN</h3></router-link
              >
            </div>
          </div>
        </div>
        <div class="sidebar-menu">
          <ul class="menu">
            <li class="sidebar-title">Menu Quản Trị</li>

            <li class="sidebar-item">
              <router-link to="/admin" class="sidebar-link">
                <i class="bi bi-grid-fill"></i>
                <span>Dashboard</span>
              </router-link>
            </li>

            <li class="sidebar-item">
              <router-link to="/admin/products" class="sidebar-link">
                <i class="bi bi-box-seam"></i>
                <span>Sản phẩm</span>
              </router-link>
            </li>

            <li class="sidebar-item">
              <router-link to="/admin/orders" class="sidebar-link">
                <i class="bi bi-cart-check-fill"></i>
                <span>Đơn hàng</span>
              </router-link>
            </li>

            <li class="sidebar-title">Cá nhân</li>
            <li class="sidebar-item">
              <a
                href="#"
                @click.prevent="handleLogout"
                class="sidebar-link text-danger"
              >
                <i class="bi bi-box-arrow-right text-danger"></i>
                <span>Đăng xuất</span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div id="main">
      <header class="mb-3">
        <a href="#" class="burger-btn d-block d-xl-none">
          <i class="bi bi-justify fs-3"></i>
        </a>
      </header>

      <router-view></router-view>

      <footer>
        <div class="footer clearfix mb-0 text-muted">
          <div class="float-start">
            <p>2026 &copy; Marcus Electronics</p>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const handleLogout = () => {
  // Xóa token và đá về trang chủ
  localStorage.removeItem("jwt_token");
  localStorage.removeItem("user_role");
  localStorage.removeItem("username");
  router.push("/login");
};
</script>

<style scoped>
@import "/assets/css/bootstrap.css";
@import "/assets/css/app.css";
@import "/assets/vendors/bootstrap-icons/bootstrap-icons.css";
@import "/assets/vendors/iconly/bold.css";
</style>

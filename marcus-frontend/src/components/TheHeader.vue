<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// State cho Giỏ hàng
const totalMoney = ref(0);
const totalQuantity = ref(0);

// State cho Xác thực (Auth)
const isLoggedIn = ref(false);
const userName = ref("");

// Kiểm tra trạng thái đăng nhập
const checkAuth = () => {
  const token = localStorage.getItem("ACCESS_TOKEN");
  const name = localStorage.getItem("USERNAME");

  if (token) {
    isLoggedIn.value = true;
    userName.value = name || "Khách hàng";
  } else {
    isLoggedIn.value = false;
    userName.value = "";
  }
};

// Xử lý Đăng xuất
const handleLogout = () => {
  if (!confirm("Bạn có chắc chắn muốn đăng xuất?")) return;

  localStorage.removeItem("ACCESS_TOKEN");
  localStorage.removeItem("USER_ROLE");
  localStorage.removeItem("USERNAME");

  isLoggedIn.value = false;
  router.push("/login");
};

// Tính toán lại giỏ hàng
const updateCartHeader = () => {
  const cart = JSON.parse(localStorage.getItem("marcus_cart")) || [];
  totalMoney.value = cart.reduce(
    (total, item) => total + item.price * item.quantity,
    0,
  );
  totalQuantity.value = cart.reduce((total, item) => total + item.quantity, 0);
};

onMounted(() => {
  checkAuth();
  updateCartHeader();
  window.addEventListener("cart-updated", updateCartHeader);
  // Lắng nghe thêm sự kiện login thành công (nếu cần thiết lập ở LoginView)
  window.addEventListener("auth-changed", checkAuth);
});

onUnmounted(() => {
  window.removeEventListener("cart-updated", updateCartHeader);
  window.removeEventListener("auth-changed", checkAuth);
});
</script>

<template>
  <div class="fixed-top-header">
    <div class="container-fluid topbar bg-light px-5 d-none d-lg-block">
      <div class="row gx-0 align-items-center">
        <div class="col-lg-4 text-center text-lg-start mb-lg-0">
          <div class="d-inline-flex align-items-center" style="height: 45px">
            <a href="#" class="text-muted me-2 small">Chính sách</a
            ><small class="text-muted"> / </small>
            <a href="#" class="text-muted mx-2 small">Hỗ trợ</a
            ><small class="text-muted"> / </small>
            <a href="#" class="text-muted ms-2 small">Liên hệ</a>
          </div>
        </div>
        <div class="col-lg-4 text-center">
          <div class="d-inline-flex align-items-center justify-content-center">
            <small class="text-dark me-2">Hotline tư vấn:</small>
            <a href="tel:0907640098" class="text-primary fw-bold mb-0"
              >(+84) 907 640 098</a
            >
          </div>
        </div>

        <div class="col-lg-4 text-center text-lg-end">
          <div class="d-inline-flex align-items-center" style="height: 45px">
            <template v-if="!isLoggedIn">
              <router-link
                to="/login"
                class="text-dark small text-decoration-none"
              >
                <i class="fa fa-user text-primary me-2"></i> Đăng nhập / Đăng ký
              </router-link>
            </template>
            <template v-else>
              <div class="dropdown">
                <a
                  href="#"
                  class="text-dark small text-decoration-none dropdown-toggle"
                  data-bs-toggle="dropdown"
                >
                  <i class="fa fa-user text-primary me-2"></i> Xin chào,
                  <span class="fw-bold">{{ userName }}</span>
                </a>
                <ul
                  class="dropdown-menu dropdown-menu-end shadow border-0 mt-2"
                >
                  <li>
                    <router-link class="dropdown-item" to="/profile"
                      >Tài khoản của tôi</router-link
                    >
                  </li>
                  <li>
                    <router-link class="dropdown-item" to="/my-orders"
                      >Đơn mua</router-link
                    >
                  </li>
                  <li><hr class="dropdown-divider" /></li>
                  <li>
                    <a
                      class="dropdown-item text-danger"
                      href="#"
                      @click.prevent="handleLogout"
                      >Đăng xuất</a
                    >
                  </li>
                </ul>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="container-fluid px-5 py-4 bg-white border-bottom">
      <div class="row align-items-center">
        <div class="col-md-3">
          <router-link to="/" class="navbar-brand text-decoration-none">
            <h1 class="display-5 text-primary m-0 fw-bold">
              <i class="fas fa-shopping-bag text-secondary me-2"></i>Marcus
            </h1>
          </router-link>
        </div>

        <div class="col-md-6">
          <div class="position-relative">
            <div class="input-group">
              <input
                type="text"
                class="form-control border-2 border-primary py-3 rounded-start-pill"
                placeholder="Bạn tìm gì hôm nay?"
              />
              <button
                class="btn btn-primary border-2 border-primary py-3 px-4 rounded-end-pill"
              >
                <i class="fas fa-search text-white"></i>
              </button>
            </div>
          </div>
        </div>

        <div class="col-md-3 text-end">
          <div class="d-inline-flex align-items-center">
            <router-link
              to="/cart"
              class="position-relative text-decoration-none"
            >
              <span
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-success"
              >
                {{ totalQuantity }}
              </span>
              <i class="fa fa-shopping-cart fa-2x text-secondary"></i>
            </router-link>
            <div class="ms-3 text-start">
              <p class="small text-muted mb-0">Giỏ hàng</p>
              <h6 class="text-dark m-0">
                {{ totalMoney.toLocaleString("vi-VN") }} ₫
              </h6>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container-fluid bg-primary px-5">
      <nav class="navbar navbar-expand-lg navbar-dark p-0">
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarCollapse"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <div class="navbar-nav me-auto">
            <router-link to="/" class="nav-item nav-link active px-4"
              >TRANG CHỦ</router-link
            >
            <a href="#" class="nav-item nav-link px-4">ĐIỆN THOẠI</a>
            <a href="#" class="nav-item nav-link px-4">LAPTOP</a>
            <a href="#" class="nav-item nav-link px-4">PHỤ KIỆN</a>
          </div>
        </div>
      </nav>
    </div>
  </div>
</template>

<style scoped>
.topbar {
  font-size: 14px;
}
.navbar-dark .navbar-nav .nav-link {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding-top: 15px;
  padding-bottom: 15px;
  transition: 0.3s;
}
.navbar-dark .navbar-nav .nav-link:hover,
.navbar-dark .navbar-nav .nav-link.active {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.1);
}
/* Sửa nhỏ để dropdown hiển thị chuẩn trên layout Bootstrap */
.dropdown-menu {
  z-index: 1050;
}
</style>

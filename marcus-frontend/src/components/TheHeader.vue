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
  window.addEventListener("auth-changed", checkAuth);
});

onUnmounted(() => {
  window.removeEventListener("cart-updated", updateCartHeader);
  window.removeEventListener("auth-changed", checkAuth);
});
</script>

<template>
  <header class="modern-header sticky-top">
    <div class="main-header bg-white shadow-sm py-3">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-6 col-lg-4">
            <router-link to="/" class="text-decoration-none">
              <h3
                class="m-0 fw-black text-dark brand-logo tracking-tight d-flex align-items-center"
              >
                <i
                  class="fas fa-bolt me-3"
                  style="color: #ff6b00; font-size: 2.2rem"
                ></i>
                <span style="font-size: 2rem">Marcus Electronics</span>
              </h3>
            </router-link>
          </div>

          <div class="col-12 col-lg-4 mt-3 mt-lg-0 order-3 order-lg-2">
            <div class="search-wrapper position-relative">
              <input
                type="text"
                class="form-control rounded-pill bg-light border-0 px-4 py-2 custom-search"
                placeholder="Bạn muốn tìm gì hôm nay?"
              />
              <button
                class="btn text-muted position-absolute end-0 top-0 h-100 px-4 rounded-pill border-0 search-btn"
              >
                <i class="fas fa-search"></i>
              </button>
            </div>
          </div>

          <div
            class="col-6 col-lg-4 text-end order-2 order-lg-3 d-flex justify-content-end align-items-center gap-3"
          >
            <div class="user-action">
              <template v-if="!isLoggedIn">
                <router-link
                  to="/login"
                  class="action-btn text-decoration-none d-flex align-items-center"
                >
                  <div class="icon-box"><i class="far fa-user"></i></div>
                  <div class="text-start ms-2 d-none d-xl-block">
                    <span class="d-block text-muted small-text">Đăng nhập</span>
                    <span class="fw-semibold text-dark">Tài khoản</span>
                  </div>
                </router-link>
              </template>

              <template v-else>
                <div class="dropdown">
                  <a
                    href="#"
                    class="action-btn text-decoration-none d-flex align-items-center"
                    data-bs-toggle="dropdown"
                  >
                    <div class="icon-box active">
                      <i class="far fa-user"></i>
                    </div>
                    <div class="text-start ms-2 d-none d-xl-block">
                      <span class="d-block text-muted small-text"
                        >Xin chào,</span
                      >
                      <span class="fw-semibold text-dark">{{ userName }}</span>
                    </div>
                  </a>
                  <ul
                    class="dropdown-menu dropdown-menu-end shadow border-0 rounded-4 mt-3"
                  >
                    <li>
                      <router-link class="dropdown-item py-2" to="/profile"
                        ><i class="far fa-id-badge me-2 text-primary"></i>Tài
                        khoản</router-link
                      >
                    </li>
                    <li>
                      <router-link class="dropdown-item py-2" to="/my-orders"
                        ><i class="fas fa-box-open me-2 text-primary"></i>Đơn
                        mua</router-link
                      >
                    </li>
                    <li><hr class="dropdown-divider mx-3" /></li>
                    <li>
                      <a
                        class="dropdown-item text-danger py-2"
                        href="#"
                        @click.prevent="handleLogout"
                        ><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a
                      >
                    </li>
                  </ul>
                </div>
              </template>
            </div>

            <router-link
              to="/cart"
              class="action-btn text-decoration-none d-flex align-items-center"
            >
              <div class="icon-box position-relative">
                <i class="fas fa-shopping-bag"></i>
                <span v-if="totalQuantity > 0" class="cart-badge">{{
                  totalQuantity
                }}</span>
              </div>
              <div class="text-start ms-2 d-none d-xl-block">
                <span class="d-block text-muted small-text">Giỏ hàng</span>
                <span class="fw-bold" style="color: #ff6b00"
                  >{{ totalMoney.toLocaleString("vi-VN") }} ₫</span
                >
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div class="nav-bottom bg-white border-top d-none d-lg-block shadow-sm">
      <div class="container d-flex justify-content-between align-items-center">
        <nav class="d-flex gap-4">
          <router-link to="/" class="nav-item-link active"
            >TRANG CHỦ</router-link
          >
          <a href="#" class="nav-item-link">ĐIỆN THOẠI</a>
          <a href="#" class="nav-item-link">LAPTOP</a>
          <a href="#" class="nav-item-link">PHỤ KIỆN</a>
          <a href="#" class="nav-item-link">ĐỒNG HỒ</a>
        </nav>

        <div class="d-flex align-items-center gap-4 py-2">
          <nav class="d-flex gap-3 auxiliary-links">
            <a
              href="#"
              class="text-muted text-decoration-none fw-semibold auxiliary-link-item"
              >Chính sách</a
            >
            <a
              href="#"
              class="text-muted text-decoration-none fw-semibold auxiliary-link-item"
              >Hỗ trợ</a
            >
            <a
              href="#"
              class="text-muted text-decoration-none fw-semibold auxiliary-link-item"
              >Liên hệ</a
            >
          </nav>
          <div class="hotline-box bg-light px-4 py-2 rounded-pill border">
            <i
              class="fas fa-phone-alt me-2"
              style="color: #ff6b00; font-size: 1.1rem"
            ></i>
            <a
              href="tel:0907640098"
              class="text-dark fw-bold text-decoration-none m-0 auxiliary-link-item"
              >(+84) 907 640 098</a
            >
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.modern-header {
  font-family: "Inter", sans-serif;
}

/* Typography & Layout */
.fw-black {
  font-weight: 800;
}
.tracking-tight {
  letter-spacing: -1px;
} /* Tighten slightly for larger size */
.small-text {
  font-size: 11px;
  line-height: 1.2;
}

/* Search Bar */
.custom-search {
  transition: all 0.3s ease;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.02);
}
.custom-search:focus {
  background-color: #fff !important;
  box-shadow: 0 0 0 3px rgba(255, 107, 0, 0.15); /* Orange glow */
  outline: none;
}
.search-btn:hover i {
  color: #ff6b00;
}

/* User & Cart Actions */
.action-btn {
  padding: 6px 12px;
  border-radius: 50px;
  transition: all 0.2s ease;
}
.action-btn:hover {
  background-color: #f8f9fa;
}
.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f1f3f5;
  color: #495057;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.3s ease;
}
.action-btn:hover .icon-box,
.icon-box.active {
  background-color: #ff6b00;
  color: white;
}
.cart-badge {
  position: absolute;
  top: -2px;
  right: -4px;
  background-color: #ef4444;
  color: white;
  font-size: 10px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 20px;
  border: 2px solid white;
}

/* Dropdown Animation */
.dropdown-menu {
  animation: fade-in 0.2s ease-out;
}
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Navigation Bottom - TĂNG CỠ CHỮ LÊN RẤT NHIỀU */
.nav-item-link {
  color: #4b5563;
  text-decoration: none;
  font-weight: 700;
  font-size: 16px; /* TO LÊN */
  padding: 20px 0; /* Increase vertical space slightly */
  position: relative;
  transition: color 0.3s ease;
}
.nav-item-link::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 4px; /* Slightly thicker underline */
  background-color: #ff6b00;
  border-radius: 4px 4px 0 0;
  transition: width 0.3s ease;
}
.nav-item-link:hover,
.nav-item-link.active {
  color: #ff6b00;
}
.nav-item-link:hover::after,
.nav-item-link.active::after {
  width: 100%;
}

/* Auxiliary Links & Hotline - TO LÊN */
.auxiliary-link-item {
  font-size: 14px; /* TO LÊN */
}
.auxiliary-links a:hover {
  color: #ff6b00 !important;
  transition: color 0.2s;
}
.hotline-box {
  transition: all 0.2s ease;
}
.hotline-box:hover {
  background-color: #fff !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #ff6b00 !important;
}
</style>

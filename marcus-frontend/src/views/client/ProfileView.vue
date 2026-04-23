<script setup>
import { ref, onMounted } from "vue";

const user = ref({
  username: "",
  role: "",
});

onMounted(() => {
  user.value.username = localStorage.getItem("USERNAME") || "Khách hàng";
  user.value.role = localStorage.getItem("USER_ROLE") || "Người dùng";
});
</script>

<template>
  <div class="container py-5 mt-5">
    <div class="row">
      <div class="col-lg-3 mb-4">
        <div class="card border-0 shadow-sm rounded-4">
          <div class="card-body p-4 text-center border-bottom">
            <div
              class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
              style="width: 70px; height: 70px; font-size: 2rem"
            >
              <i class="bi bi-person"></i>
            </div>
            <h5 class="fw-bold mb-0">Tài khoản của tôi</h5>
          </div>
          <div class="list-group list-group-flush rounded-bottom-4">
            <router-link
              to="/profile"
              class="list-group-item list-group-item-action p-3 fw-bold text-primary active-link"
            >
              <i class="bi bi-person-vcard me-2"></i> Hồ sơ cá nhân
            </router-link>
            <router-link
              to="/my-orders"
              class="list-group-item list-group-item-action p-3 fw-medium text-muted"
            >
              <i class="bi bi-bag-check me-2"></i> Lịch sử đơn hàng
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-lg-9">
        <div class="card border-0 shadow-sm rounded-4 p-4">
          <h4 class="fw-bold mb-4 text-dark">Hồ sơ của bạn</h4>

          <div class="row g-4">
            <div class="col-md-6">
              <div class="p-3 bg-light rounded-3 border">
                <p class="small text-muted fw-bold mb-1">TÊN ĐĂNG NHẬP</p>
                <h6 class="mb-0 fw-bold text-dark">{{ user.username }}</h6>
              </div>
            </div>
            <div class="col-md-6">
              <div class="p-3 bg-light rounded-3 border">
                <p class="small text-muted fw-bold mb-1">
                  PHÂN QUYỀN TÀI KHOẢN
                </p>
                <h6 class="mb-0 fw-bold text-primary">{{ user.role }}</h6>
              </div>
            </div>
          </div>

          <div class="mt-4 pt-4 border-top">
            <p class="text-secondary small mb-0">
              * Để thay đổi mật khẩu hoặc cập nhật số điện thoại, vui lòng liên
              hệ bộ phận hỗ trợ chăm sóc khách hàng của Marcus Electronics.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.active-link {
  background-color: #f0f5ff;
  border-left: 4px solid #0054a6 !important;
}
</style>

<script setup>
import { useRoute } from "vue-router";

const route = useRoute();
// Lấy mã đơn hàng từ query URL (ví dụ: ?id=15)
const orderId = route.query.id || "N/A";
</script>

<template>
  <div class="container py-5 mt-5 text-center">
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="bg-white p-5 rounded-4 shadow-sm border mt-4">
          <div class="mb-4">
            <div
              class="d-inline-flex align-items-center justify-content-center bg-success-subtle text-success rounded-circle"
              style="width: 100px; height: 100px"
            >
              <i class="bi bi-check-circle-fill" style="font-size: 3rem"></i>
            </div>
          </div>

          <h2 class="fw-bold text-dark mb-3">Đặt hàng thành công!</h2>
          <p class="text-muted fs-5 mb-4">
            Cảm ơn bạn đã tin tưởng và mua sắm tại
            <span class="text-primary fw-bold">Marcus Electronics</span>.
          </p>

          <div class="bg-light p-3 rounded-3 mb-4 d-inline-block border">
            <span class="text-muted me-2">Mã đơn hàng của bạn:</span>
            <span class="fs-4 fw-bold text-primary">#ORD-{{ orderId }}</span>
          </div>

          <p class="text-secondary small mb-5">
            Hệ thống đang xử lý đơn hàng. Chúng tôi sẽ sớm liên hệ với bạn qua
            số điện thoại để xác nhận giao hàng.
          </p>

          <div class="d-flex justify-content-center gap-3">
            <router-link
              to="/orders"
              class="btn btn-outline-primary rounded-pill px-4 py-2 fw-bold"
            >
              Theo dõi đơn hàng
            </router-link>
            <router-link
              to="/"
              class="btn btn-primary rounded-pill px-4 py-2 fw-bold shadow-sm"
            >
              Tiếp tục mua sắm
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../../utils/api";

const route = useRoute();
const router = useRouter();

// Trạng thái: loading, success, failed
const status = ref("loading");
const message = ref("Đang xác thực thanh toán...");
const orderId = ref(route.query.id || route.query.vnp_TxnRef || "N/A");

const verifyPayment = async () => {
  // Lấy NGUYÊN SI chuỗi URL gốc (chứa dấu ?vnp_Amount=...)
  const queryString = window.location.search;

  // Nếu là thanh toán COD (chỉ có ?id=...)
  if (!queryString.includes("vnp_SecureHash")) {
    status.value = "success";
    message.value = "Đặt hàng thành công!";
    return;
  }

  // NẾU LÀ VNPAY: Gọi API để xác thực
  try {
    const res = await api.get(`/payment/vnpay/return${queryString}`);
    status.value = "success";
    message.value = res.data;
    router.replace({ query: { id: route.query.vnp_TxnRef } });
  } catch (error) {
    status.value = "failed";
    message.value =
      error.response?.data || "Chữ ký không hợp lệ! Nghi ngờ gian lận.";
  }
};

onMounted(() => {
  verifyPayment();
});
</script>

<template>
  <div class="container py-5 mt-5 text-center">
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="bg-white p-5 rounded-4 shadow-sm border mt-4">
          <div v-if="status === 'loading'">
            <div
              class="spinner-border text-primary mb-3"
              style="width: 3rem; height: 3rem"
            ></div>
            <h4 class="fw-bold text-dark">Đang kiểm tra giao dịch...</h4>
            <p class="text-muted">Vui lòng không đóng trình duyệt lúc này.</p>
          </div>

          <div v-else-if="status === 'success'">
            <div class="mb-4">
              <div
                class="d-inline-flex align-items-center justify-content-center bg-success-subtle text-success rounded-circle"
                style="width: 100px; height: 100px"
              >
                <i class="bi bi-check-circle-fill" style="font-size: 3rem"></i>
              </div>
            </div>
            <h2 class="fw-bold text-dark mb-3">{{ message }}</h2>
            <p class="text-muted fs-5 mb-4">
              Cảm ơn bạn đã tin tưởng và mua sắm tại
              <span class="text-primary fw-bold">Marcus Electronics</span>.
            </p>

            <div class="bg-light p-3 rounded-3 mb-4 d-inline-block border">
              <span class="text-muted me-2">Mã đơn hàng của bạn:</span>
              <span class="fs-4 fw-bold text-primary">#ORD-{{ orderId }}</span>
            </div>

            <div class="d-flex justify-content-center gap-3">
              <router-link
                to="/my-orders"
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

          <div v-else>
            <div class="mb-4">
              <div
                class="d-inline-flex align-items-center justify-content-center bg-danger-subtle text-danger rounded-circle"
                style="width: 100px; height: 100px"
              >
                <i class="bi bi-x-circle-fill" style="font-size: 3rem"></i>
              </div>
            </div>
            <h2 class="fw-bold text-dark mb-3">Thanh toán không thành công</h2>
            <p class="text-danger fw-bold">{{ message }}</p>
            <p class="text-muted mb-4">
              Mã đơn hàng: #ORD-{{ orderId }}<br />Đơn hàng của bạn vẫn được lưu
              lại ở trạng thái <b>Chờ xử lý</b>.
            </p>

            <router-link
              to="/my-orders"
              class="btn btn-primary rounded-pill px-5 py-2 fw-bold shadow-sm"
            >
              Đến Lịch sử đơn hàng để thử lại
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

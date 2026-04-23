<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api";

const orders = ref([]);
const loading = ref(true);

const fetchMyOrders = async () => {
  loading.value = true;
  try {
    const res = await api.get("/orders/my-orders");
    orders.value = res.data;
  } catch (error) {
    console.error("Lỗi tải lịch sử đơn hàng:", error);
    alert("Vui lòng đăng nhập để xem lịch sử đơn hàng!");
  } finally {
    loading.value = false;
  }
};

const cancelOrder = async (orderId) => {
  if (!confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?")) return;
  try {
    await api.patch(`/orders/${orderId}/cancel`);
    alert("Đã hủy đơn hàng thành công!");
    fetchMyOrders(); // Tải lại danh sách
  } catch (error) {
    alert("Lỗi: " + (error.response?.data || error.message));
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

const formatDate = (dateString) => {
  if (!dateString) return "N/A";
  const date = new Date(dateString);
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
};

const getStatusBadge = (status) => {
  switch (status) {
    case "PENDING":
      return { text: "Chờ xử lý", class: "bg-warning text-dark" };
    case "PROCESSING":
      return { text: "Đang chuẩn bị", class: "bg-primary" };
    case "SHIPPED":
      return { text: "Đang giao", class: "bg-info text-dark" };
    case "DELIVERED":
      return { text: "Đã giao", class: "bg-success" };
    case "CANCELLED":
      return { text: "Đã hủy", class: "bg-danger" };
    default:
      return { text: "Không rõ", class: "bg-secondary" };
  }
};

onMounted(fetchMyOrders);
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
              class="list-group-item list-group-item-action p-3 fw-medium text-muted"
            >
              <i class="bi bi-person-vcard me-2"></i> Hồ sơ cá nhân
            </router-link>
            <router-link
              to="/my-orders"
              class="list-group-item list-group-item-action p-3 fw-bold text-primary active-link"
            >
              <i class="bi bi-bag-check me-2"></i> Lịch sử đơn hàng
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-lg-9">
        <div class="card border-0 shadow-sm rounded-4 p-4">
          <h4 class="fw-bold mb-4 text-dark">Lịch sử đơn hàng</h4>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary"></div>
          </div>

          <div v-else-if="orders.length === 0" class="text-center py-5">
            <i class="bi bi-box-seam display-1 text-muted opacity-50 mb-3"></i>
            <p class="text-muted">Bạn chưa có đơn hàng nào.</p>
            <router-link
              to="/"
              class="btn btn-outline-primary rounded-pill mt-2"
              >Bắt đầu mua sắm</router-link
            >
          </div>

          <div v-else class="d-flex flex-column gap-3">
            <div
              v-for="order in orders"
              :key="order.id"
              class="border rounded-4 p-4 position-relative"
            >
              <div
                class="d-flex justify-content-between align-items-start border-bottom pb-3 mb-3"
              >
                <div>
                  <h6 class="fw-bold text-primary mb-1">
                    Đơn hàng #ORD-{{ order.id }}
                  </h6>
                  <span class="text-muted small"
                    ><i class="bi bi-clock me-1"></i>
                    {{ formatDate(order.orderDate) }}</span
                  >
                </div>
                <span
                  class="badge px-3 py-2 rounded-pill"
                  :class="getStatusBadge(order.status).class"
                >
                  {{ getStatusBadge(order.status).text }}
                </span>
              </div>

              <div class="row align-items-center">
                <div class="col-md-8">
                  <p class="mb-1 small text-secondary">
                    <span class="fw-bold text-dark">Người nhận:</span>
                    {{ order.fullName }} ({{ order.phoneNumber }})
                  </p>
                  <p class="mb-1 small text-secondary">
                    <span class="fw-bold text-dark">Địa chỉ:</span>
                    {{ order.address }}
                  </p>
                  <p class="mb-0 small text-secondary">
                    <span class="fw-bold text-dark">Thanh toán:</span>
                    {{ order.paymentMethod }}
                  </p>
                </div>
                <div class="col-md-4 text-md-end mt-3 mt-md-0">
                  <p class="text-muted small mb-1">Tổng tiền</p>
                  <h5 class="fw-bold text-danger mb-3">
                    {{ formatCurrency(order.totalMoney) }}
                  </h5>

                  <button
                    v-if="order.status === 'PENDING'"
                    @click="cancelOrder(order.id)"
                    class="btn btn-sm btn-outline-danger rounded-pill px-3"
                  >
                    <i class="bi bi-x-circle me-1"></i> Hủy đơn hàng
                  </button>
                </div>
              </div>
            </div>
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

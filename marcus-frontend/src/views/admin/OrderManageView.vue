<template>
  <div class="page-content">
    <!-- Header -->
    <div class="page-heading d-flex justify-content-between align-items-center">
      <h3 class="page-title-text">Quản lý Đơn hàng</h3>
      <button
        v-if="targetCustomerId"
        class="btn btn-outline-secondary btn-sm"
        @click="clearCustomerFilter"
      >
        <i class="bi bi-x-circle"></i> Bỏ lọc khách hàng
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="row mb-4">
      <div class="col-md-4">
        <div class="stat-card stat-card--blue">
          <div class="stat-card__body">
            <div>
              <h6 class="stat-card__label">Tổng đơn hàng</h6>
              <h3 class="stat-card__value">{{ orders.length }}</h3>
            </div>
            <i class="bi bi-box-seam stat-card__icon"></i>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card stat-card--yellow">
          <div class="stat-card__body">
            <div>
              <h6 class="stat-card__label">Đang xử lý</h6>
              <h3 class="stat-card__value">{{ pendingCount }}</h3>
            </div>
            <i class="bi bi-hourglass-split stat-card__icon"></i>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card stat-card--green">
          <div class="stat-card__body">
            <div>
              <h6 class="stat-card__label">Doanh thu</h6>
              <h3 class="stat-card__value">
                {{ formatCurrency(totalRevenue) }}
              </h3>
            </div>
            <i class="bi bi-cash-stack stat-card__icon"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Table -->
    <div class="card">
      <div class="card-body">
        <!-- Filters -->
        <div class="row mb-4">
          <div class="col-md-4">
            <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Tìm theo tên, SĐT hoặc Mã đơn..."
            />
          </div>
          <div class="col-md-3">
            <select v-model="filterStatus" class="form-select">
              <option value="ALL">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PROCESSING">Đang chuẩn bị hàng</option>
              <option value="SHIPPED">Đang giao hàng</option>
              <option value="DELIVERED">Đã giao thành công</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="isLoading" class="text-center py-4">
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <!-- Table -->
        <div v-else class="table-responsive">
          <table class="table table-hover align-middle border">
            <thead class="table-light">
              <tr>
                <th>Mã Đơn</th>
                <th>Khách hàng</th>
                <th>Ngày đặt</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th class="col-status">Trạng thái</th>
                <th class="text-center">Chi tiết</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredOrders.length === 0">
                <td colspan="7" class="text-center text-muted py-4">
                  Không có đơn hàng nào
                </td>
              </tr>
              <tr v-for="order in filteredOrders" :key="order.id">
                <td class="fw-bold text-primary">#ORD-{{ order.id }}</td>
                <td>
                  <div class="fw-bold text-dark">{{ order.fullName }}</div>
                  <div class="small text-muted">{{ order.phoneNumber }}</div>
                </td>
                <td>{{ formatDateTime(order.orderDate) }}</td>
                <td class="text-danger fw-bold">
                  {{ formatCurrency(order.totalMoney) }}
                </td>
                <td>
                  <span class="badge bg-light text-dark border">{{
                    order.paymentMethod
                  }}</span>
                </td>
                <td>
                  <select
                    class="form-select form-select-sm fw-bold"
                    :class="statusColor(order.status)"
                    v-model="order.status"
                    @change="updateStatus(order.id, order.status)"
                  >
                    <option value="PENDING">Chờ xử lý</option>
                    <option value="PROCESSING">Đang chuẩn bị</option>
                    <option value="SHIPPED">Đang giao hàng</option>
                    <option value="DELIVERED">Đã giao</option>
                    <option value="CANCELLED">Đã hủy</option>
                  </select>
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-sm btn-outline-info"
                    title="Xem chi tiết đơn hàng"
                    @click="openDetailModal(order)"
                  >
                    <i class="bi bi-eye"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <div
      v-if="showDetailModal"
      class="modal-overlay"
      @click.self="showDetailModal = false"
    >
      <div class="modal-box modal-lg modal-box--order-detail">
        <div class="modal-header-custom">
          <h5>
            Chi tiết Đơn hàng
            <span class="text-primary">#ORD-{{ selectedOrder?.id }}</span>
          </h5>
          <button class="modal-close" @click="showDetailModal = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="modal-body-custom">
          <!-- Order Summary -->
          <div class="row mb-3 bg-light p-3 rounded mx-0 border">
            <div class="col-md-6">
              <p class="mb-1">
                <strong>Khách hàng:</strong> {{ selectedOrder?.fullName }}
              </p>
              <p class="mb-1">
                <strong>SĐT:</strong> {{ selectedOrder?.phoneNumber }}
              </p>
              <p class="mb-0">
                <strong>Ngày đặt:</strong>
                {{ formatDateTime(selectedOrder?.orderDate) }}
              </p>
            </div>
            <div class="col-md-6">
              <p class="mb-1">
                <strong>Địa chỉ:</strong> {{ selectedOrder?.address }}
              </p>
              <p class="mb-1">
                <strong>Ghi chú:</strong>
                {{ selectedOrder?.note || "Không có" }}
              </p>
              <p class="mb-0">
                <strong>Tổng thanh toán: </strong>
                <span class="text-danger fw-bold">{{
                  formatCurrency(selectedOrder?.totalMoney)
                }}</span>
              </p>
            </div>
          </div>

          <h6 class="fw-bold mb-3 border-bottom pb-2">Danh sách sản phẩm</h6>

          <!-- Loading Details -->
          <div v-if="isFetchingDetails" class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
          </div>

          <!-- Details Table -->
          <div v-else class="table-responsive">
            <table class="table table-bordered align-middle text-sm">
              <thead class="table-light">
                <tr>
                  <th class="text-center col-img">Ảnh</th>
                  <th>Sản phẩm / Biến thể</th>
                  <th class="text-center">Đơn giá</th>
                  <th class="text-center">SL</th>
                  <th class="text-end">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in orderDetails" :key="item.detailId">
                  <td class="text-center">
                    <img
                      :src="item.imageUrl || 'https://via.placeholder.com/60'"
                      class="product-thumbnail"
                    />
                  </td>
                  <td>
                    <div class="fw-bold text-dark">{{ item.productName }}</div>
                    <div class="small text-muted">{{ item.variantDetail }}</div>
                    <code class="small text-primary">{{ item.skuCode }}</code>
                  </td>
                  <td class="text-center">
                    {{ formatCurrency(item.unitPrice) }}
                  </td>
                  <td class="text-center fw-bold">{{ item.quantity }}</td>
                  <td class="text-end fw-bold text-danger">
                    {{ formatCurrency(item.lineTotal) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="modal-footer-custom">
          <button class="btn-cancel" @click="showDetailModal = false">
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../../utils/api";

const route = useRoute();
const router = useRouter();

const orders = ref([]);
const isLoading = ref(false);
const searchQuery = ref("");
const filterStatus = ref("ALL");
const targetCustomerId = ref(route.query.customerId || null);

const fetchOrders = async () => {
  isLoading.value = true;
  try {
    const res = await api.get("/admin/orders");
    orders.value = res.data;
  } catch (error) {
    alert("Không thể tải danh sách đơn hàng!");
  } finally {
    isLoading.value = false;
  }
};

const pendingCount = computed(
  () => orders.value.filter((o) => o.status === "PENDING").length,
);

const totalRevenue = computed(() =>
  orders.value
    .filter((o) => o.status === "DELIVERED")
    .reduce((sum, o) => sum + o.totalMoney, 0),
);

const filteredOrders = computed(() =>
  orders.value.filter((o) => {
    if (targetCustomerId.value && o.userId !== Number(targetCustomerId.value))
      return false;

    const searchLower = searchQuery.value.toLowerCase();
    const matchSearch =
      o.fullName.toLowerCase().includes(searchLower) ||
      o.phoneNumber.includes(searchLower) ||
      o.id.toString().includes(searchLower);

    const matchStatus =
      filterStatus.value === "ALL" || o.status === filterStatus.value;

    return matchSearch && matchStatus;
  }),
);

const clearCustomerFilter = () => {
  targetCustomerId.value = null;
  router.replace("/admin/orders");
};

const updateStatus = async (orderId, newStatus) => {
  if (!confirm("Xác nhận cập nhật trạng thái đơn hàng này?")) {
    fetchOrders();
    return;
  }
  try {
    await api.patch(`/admin/orders/${orderId}/status`, { status: newStatus });
  } catch (error) {
    alert(
      "Lỗi cập nhật trạng thái: " + (error.response?.data || error.message),
    );
    fetchOrders();
  }
};

const statusColor = (status) => {
  const map = {
    PENDING: "text-warning border-warning",
    PROCESSING: "text-primary border-primary",
    SHIPPED: "text-info border-info",
    DELIVERED: "text-success border-success",
    CANCELLED: "text-danger border-danger",
  };
  return map[status] ?? "";
};

const formatDateTime = (dateString) => {
  if (!dateString) return "N/A";
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(dateString));
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

// Modal state & logic
const showDetailModal = ref(false);
const selectedOrder = ref(null);
const orderDetails = ref([]);
const isFetchingDetails = ref(false);

const openDetailModal = async (order) => {
  selectedOrder.value = order;
  showDetailModal.value = true;
  isFetchingDetails.value = true;
  try {
    const res = await api.get(`/admin/orders/${order.id}/details`);
    orderDetails.value = res.data;
  } catch (error) {
    alert("Lỗi khi tải chi tiết đơn hàng!");
    showDetailModal.value = false;
  } finally {
    isFetchingDetails.value = false;
  }
};

onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
@import "../../assets/css/product-manage.css";

/* Stat Cards */
.stat-card {
  height: 100%;
  border: none;
  border-radius: 12px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-card--blue {
  background: linear-gradient(135deg, #435ebe, #6c8cff);
}
.stat-card--yellow {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}
.stat-card--green {
  background: linear-gradient(135deg, #10b981, #34d399);
}

.stat-card__body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
}

.stat-card__label {
  color: rgba(255, 255, 255, 0.6);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 0.75rem;
  margin-bottom: 0.5rem;
}

.stat-card__value {
  font-weight: 700;
  margin-bottom: 0;
}

.stat-card__icon {
  font-size: 2rem;
  opacity: 0.5;
}

/* Table */
.col-status {
  width: 200px;
}
.col-img {
  width: 60px;
}

.product-thumbnail {
  width: 45px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
}

/* Modal */
.modal-box--order-detail {
  max-width: 800px;
}

/* Status select */
select.form-select-sm {
  background-position: right 0.25rem center;
  padding-right: 1.5rem;
}
</style>

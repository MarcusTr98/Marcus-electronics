<template>
  <div class="page-content">
    <div
      class="page-heading d-flex justify-content-between align-items-center mb-4"
    >
      <h3 class="page-title-text">Quản lý Đơn hàng</h3>
      <button
        v-if="targetCustomerId"
        class="btn btn-outline-secondary btn-sm rounded-pill px-3 fw-bold shadow-sm"
        @click="clearCustomerFilter"
      >
        <i class="bi bi-x-circle me-1"></i> Bỏ lọc khách hàng
      </button>
    </div>

    <div class="row mb-4 g-3">
      <div class="col-md-4">
        <div
          class="card h-100 mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #435ebe, #6c8cff);
            color: white;
            border: none;
            border-radius: 12px;
          "
        >
          <div
            class="card-body d-flex justify-content-between align-items-center p-4"
          >
            <div>
              <h6
                class="text-white-50 fw-bold text-uppercase mb-2"
                style="letter-spacing: 0.5px; font-size: 0.75rem"
              >
                Tổng đơn hàng
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ orders.length }}</h3>
            </div>
            <div class="fs-1 opacity-50"><i class="bi bi-box-seam"></i></div>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div
          class="card h-100 mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #f59e0b, #fbbf24);
            color: white;
            border: none;
            border-radius: 12px;
          "
        >
          <div
            class="card-body d-flex justify-content-between align-items-center p-4"
          >
            <div>
              <h6
                class="text-white-50 fw-bold text-uppercase mb-2"
                style="letter-spacing: 0.5px; font-size: 0.75rem"
              >
                Đang xử lý
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ pendingCount }}</h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-hourglass-split"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div
          class="card h-100 mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #10b981, #34d399);
            color: white;
            border: none;
            border-radius: 12px;
          "
        >
          <div
            class="card-body d-flex justify-content-between align-items-center p-4"
          >
            <div>
              <h6
                class="text-white-50 fw-bold text-uppercase mb-2"
                style="letter-spacing: 0.5px; font-size: 0.75rem"
              >
                Doanh thu
              </h6>
              <h3 class="mb-0 fw-bold text-white">
                {{ formatCurrency(totalRevenue) }}
              </h3>
            </div>
            <div class="fs-1 opacity-50"><i class="bi bi-cash-stack"></i></div>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-body p-4">
        <div class="row mb-4">
          <div class="col-md-5">
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0"
                ><i class="bi bi-search text-muted"></i
              ></span>
              <input
                v-model="searchQuery"
                type="text"
                class="form-control border-start-0 ps-0"
                placeholder="Tìm theo tên, SĐT hoặc Mã đơn..."
              />
            </div>
          </div>
          <div class="col-md-3">
            <select v-model="filterStatus" class="form-select fw-medium">
              <option value="ALL">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PROCESSING">Đang chuẩn bị hàng</option>
              <option value="SHIPPED">Đang giao hàng</option>
              <option value="DELIVERED">Đã giao thành công</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>
        </div>

        <div v-if="isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th class="ps-4">Mã Đơn</th>
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
                <td colspan="7" class="text-center text-muted py-5">
                  Không tìm thấy đơn hàng nào
                </td>
              </tr>
              <tr v-for="order in filteredOrders" :key="order.id">
                <td class="fw-bold text-primary ps-4">#ORD-{{ order.id }}</td>
                <td>
                  <div class="fw-bold text-dark">{{ order.fullName }}</div>
                  <div class="small text-muted">
                    <i class="bi bi-telephone me-1"></i>{{ order.phoneNumber }}
                  </div>
                </td>
                <td class="small text-muted fw-medium">
                  {{ formatDateTime(order.orderDate) }}
                </td>
                <td class="text-success fw-bold">
                  {{ formatCurrency(order.totalMoney) }}
                </td>
                <td>
                  <span
                    class="badge bg-light text-dark border px-3 py-2 rounded-pill"
                    >{{ order.paymentMethod }}</span
                  >
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
                    class="btn btn-sm btn-outline-info rounded-3"
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

    <div
      v-if="showDetailModal"
      class="modal-overlay"
      @click.self="showDetailModal = false"
    >
      <div class="modal-box modal-lg">
        <div class="modal-header-custom">
          <h5>
            Chi tiết Đơn hàng
            <span class="text-primary ms-1">#ORD-{{ selectedOrder?.id }}</span>
          </h5>
          <button class="modal-close" @click="showDetailModal = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="modal-body-custom">
          <div class="row mb-4 bg-light p-3 rounded-4 mx-0 border">
            <div class="col-md-6">
              <p class="mb-2 text-dark">
                <strong class="text-muted me-2">Khách hàng:</strong>
                {{ selectedOrder?.fullName }}
              </p>
              <p class="mb-2 text-dark">
                <strong class="text-muted me-2">SĐT:</strong>
                {{ selectedOrder?.phoneNumber }}
              </p>
              <p class="mb-0 text-dark">
                <strong class="text-muted me-2">Ngày đặt:</strong>
                {{ formatDateTime(selectedOrder?.orderDate) }}
              </p>
            </div>
            <div class="col-md-6">
              <p class="mb-2 text-dark">
                <strong class="text-muted me-2">Địa chỉ:</strong>
                {{ selectedOrder?.address }}
              </p>
              <p class="mb-2 text-dark">
                <strong class="text-muted me-2">Ghi chú:</strong>
                {{ selectedOrder?.note || "Không có" }}
              </p>
              <p class="mb-0">
                <strong class="text-muted me-2">Tổng thanh toán: </strong>
                <span class="text-danger fw-bold fs-5">{{
                  formatCurrency(selectedOrder?.totalMoney)
                }}</span>
              </p>
            </div>
          </div>

          <h6
            class="fw-bold mb-3 text-dark text-uppercase"
            style="letter-spacing: 0.5px"
          >
            Danh sách sản phẩm
          </h6>

          <div v-if="isFetchingDetails" class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
          </div>

          <div v-else class="table-responsive border rounded-3">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="text-center" style="width: 80px">Ảnh</th>
                  <th>Sản phẩm / Phân loại</th>
                  <th class="text-center">Đơn giá</th>
                  <th class="text-center">SL</th>
                  <th class="text-end pe-4">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in orderDetails" :key="item.detailId">
                  <td class="text-center">
                    <img
                      :src="item.imageUrl || 'https://via.placeholder.com/60'"
                      class="rounded-3 border"
                      style="width: 50px; height: 50px; object-fit: contain"
                    />
                  </td>
                  <td>
                    <div class="fw-bold text-dark mb-1">
                      {{ item.productName }}
                    </div>
                    <div class="small text-muted">{{ item.variantDetail }}</div>
                    <code
                      class="small text-primary bg-primary-subtle px-2 py-1 rounded"
                      >{{ item.skuCode }}</code
                    >
                  </td>
                  <td class="text-center fw-medium text-dark">
                    {{ formatCurrency(item.unitPrice) }}
                  </td>
                  <td class="text-center fw-bold text-dark">
                    {{ item.quantity }}
                  </td>
                  <td class="text-end fw-bold text-danger pe-4">
                    {{ formatCurrency(item.lineTotal) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="modal-footer-custom">
          <button
            class="btn btn-light border fw-bold px-4 rounded-3"
            @click="showDetailModal = false"
          >
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
    PENDING: "text-warning border-warning bg-warning-subtle",
    PROCESSING: "text-primary border-primary bg-primary-subtle",
    SHIPPED: "text-info border-info bg-info-subtle",
    DELIVERED: "text-success border-success bg-success-subtle",
    CANCELLED: "text-danger border-danger bg-danger-subtle",
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

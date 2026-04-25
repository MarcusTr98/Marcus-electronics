<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../utils/api";

const router = useRouter();
const customers = ref([]);
const isLoading = ref(false);
const searchQuery = ref("");
const filterStatus = ref("all");

const fetchCustomers = async () => {
  isLoading.value = true;
  try {
    const res = await api.get("/admin/customers");
    customers.value = res.data;
  } catch (error) {
    console.error("Lỗi tải dữ liệu khách hàng:", error);
    alert("Không thể tải danh sách khách hàng!");
  } finally {
    isLoading.value = false;
  }
};

const activeCustomersCount = computed(() => {
  return customers.value.filter((c) => c.isActive).length;
});

const filteredCustomers = computed(() => {
  return customers.value.filter((c) => {
    const searchLower = searchQuery.value.toLowerCase();
    const matchSearch =
      c.fullName.toLowerCase().includes(searchLower) ||
      c.email.toLowerCase().includes(searchLower) ||
      (c.phoneNumber && c.phoneNumber.includes(searchLower));

    let matchStatus = true;
    if (filterStatus.value === "active") matchStatus = c.isActive === true;
    if (filterStatus.value === "locked") matchStatus = c.isActive === false;

    return matchSearch && matchStatus;
  });
});

const toggleStatus = async (id, isActive) => {
  const actionText = isActive ? "mở khóa" : "khóa";
  if (!confirm(`Bạn có chắc muốn ${actionText} tài khoản này?`)) return;

  try {
    await api.patch(`/admin/customers/${id}/status`, { active: isActive });
    const target = customers.value.find((c) => c.id === id);
    if (target) target.isActive = isActive;
  } catch (error) {
    alert(`Lỗi khi ${actionText} tài khoản!`);
  }
};

const viewOrderHistory = (customerId) => {
  router.push(`/admin/orders?customerId=${customerId}`);
};

const formatDate = (dateString) => {
  if (!dateString) return "N/A";
  const date = new Date(dateString);
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

onMounted(() => {
  fetchCustomers();
});
</script>

<template>
  <div class="page-content">
    <div class="page-heading">
      <h3 class="page-title-text">Quản lý Khách hàng</h3>
    </div>

    <div class="row mb-4">
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
                Tổng khách hàng
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ customers.length }}</h3>
            </div>
            <div class="fs-1 opacity-50"><i class="bi bi-people-fill"></i></div>
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
                Tài khoản hoạt động
              </h6>
              <h3 class="mb-0 fw-bold text-white">
                {{ activeCustomersCount }}
              </h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-person-check-fill"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div
          class="card h-100 mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #ef4444, #f87171);
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
                Tài khoản bị khóa
              </h6>
              <h3 class="mb-0 fw-bold text-white">
                {{ customers.length - activeCustomersCount }}
              </h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-person-x-fill"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-body p-4">
        <div class="row mb-4">
          <div class="col-md-5">
            <div class="input-group">
              <span class="input-group-text bg-light"
                ><i class="bi bi-search text-muted"></i
              ></span>
              <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Tìm theo tên, email hoặc số điện thoại..."
              />
            </div>
          </div>
          <div class="col-md-3">
            <select v-model="filterStatus" class="form-select">
              <option value="all">Tất cả trạng thái</option>
              <option value="active">Đang hoạt động</option>
              <option value="locked">Bị khóa</option>
            </select>
          </div>
        </div>

        <div v-if="isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <div class="table-responsive" v-else>
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th class="ps-3">Khách hàng</th>
                <th>Liên hệ</th>
                <th class="text-center">Số đơn hàng</th>
                <th class="text-end">Tổng chi tiêu (LTV)</th>
                <th class="text-center">Trạng thái</th>
                <th class="text-center">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredCustomers.length === 0">
                <td colspan="6" class="text-center text-muted py-5">
                  Không tìm thấy dữ liệu
                </td>
              </tr>
              <tr v-for="customer in filteredCustomers" :key="customer.id">
                <td class="ps-3">
                  <div class="fw-bold text-dark">{{ customer.fullName }}</div>
                  <div class="small text-muted">
                    Tham gia: {{ formatDate(customer.createdAt) }}
                  </div>
                </td>
                <td>
                  <div class="small text-dark">
                    <i class="bi bi-envelope me-2 text-muted"></i
                    >{{ customer.email }}
                  </div>
                  <div class="small mt-1 text-dark">
                    <i class="bi bi-telephone me-2 text-muted"></i
                    >{{ customer.phoneNumber || "Chưa cập nhật" }}
                  </div>
                </td>
                <td class="text-center">
                  <div class="bg-light text-success border">
                    {{ customer.totalOrders || 0 }} đơn
                  </div>
                </td>
                <td
                  class="text-end fw-bold"
                  :class="
                    customer.totalSpent > 10000000
                      ? 'text-danger'
                      : 'text-primary'
                  "
                >
                  {{ formatCurrency(customer.totalSpent || 0) }}
                </td>
                <td class="text-center">
                  <span
                    class="px-3 py-2 rounded-pill"
                    :class="
                      customer.isActive
                        ? 'bg-success-subtle text-success border border-success-subtle'
                        : 'bg-danger-subtle text-danger border border-danger-subtle'
                    "
                  >
                    {{ customer.isActive ? "Hoạt động" : "Bị khóa" }}
                  </span>
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-sm btn-outline-info me-2"
                    @click="viewOrderHistory(customer.id)"
                    title="Xem lịch sử mua hàng"
                  >
                    <i class="bi bi-receipt"></i>
                  </button>
                  <button
                    v-if="customer.isActive"
                    class="btn btn-sm btn-outline-danger"
                    @click="toggleStatus(customer.id, false)"
                    title="Khóa tài khoản"
                  >
                    <i class="bi bi-lock-fill"></i>
                  </button>
                  <button
                    v-else
                    class="btn btn-sm btn-success text-white"
                    @click="toggleStatus(customer.id, true)"
                    title="Mở khóa tài khoản"
                  >
                    <i class="bi bi-unlock-fill"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

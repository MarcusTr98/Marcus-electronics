<template>
  <div class="page-content">
    <!-- ── Header ─────────────────────────────────────────────── -->
    <div class="page-heading">
      <h3 class="page-title-text">Bảng điều khiển</h3>
      <p class="page-subtitle">Dữ liệu kinh doanh thời gian thực 🚀</p>
    </div>

    <!-- ── Loading ────────────────────────────────────────────── -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Đang đồng bộ dữ liệu hệ thống...</p>
    </div>

    <div v-else>
      <!-- ── KPI Cards ───────────────────────────────────────── -->
      <div class="stats-grid">
        <div class="stat-card" v-for="stat in stats" :key="stat.title">
          <div class="stat-icon" :style="{ background: stat.bg }">
            <i :class="stat.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.title }}</div>
          </div>
        </div>
      </div>

      <!-- ── Charts ─────────────────────────────────────────── -->
      <div class="charts-row">
        <div class="chart-card chart-card--wide">
          <div class="chart-card__header">
            <span class="chart-card__title">Doanh thu 7 ngày qua</span>
          </div>
          <div class="chart-card__body">
            <canvas ref="revenueChartCanvas"></canvas>
          </div>
        </div>

        <div class="chart-card chart-card--narrow">
          <div class="chart-card__header">
            <span class="chart-card__title">Tỷ lệ Trạng thái Đơn hàng</span>
          </div>
          <div class="chart-card__body chart-card__body--center">
            <canvas
              ref="orderStatusChartCanvas"
              class="chart-doughnut"
            ></canvas>
          </div>
        </div>
      </div>

      <!-- ── Recent Orders ──────────────────────────────────── -->
      <div class="orders-card">
        <div class="orders-card__header">
          <span class="chart-card__title">5 Đơn hàng mới nhất</span>
          <router-link
            to="/admin/orders"
            class="btn btn-sm btn-outline-primary"
          >
            Xem tất cả
          </router-link>
        </div>
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th class="ps-4">Mã ĐH</th>
                <th>Khách hàng</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="recentOrders.length === 0">
                <td colspan="4" class="text-center py-4 text-muted">
                  Chưa có đơn hàng nào
                </td>
              </tr>
              <tr v-for="order in recentOrders" :key="order.id">
                <td class="ps-4">
                  <span class="fw-bold text-primary">#ORD-{{ order.id }}</span>
                </td>
                <td>{{ order.fullName }}</td>
                <td class="text-danger fw-bold">
                  {{ formatCurrency(order.totalMoney) }}
                </td>
                <td>
                  <span
                    class="badge border"
                    :class="getStatusClass(order.status)"
                  >
                    {{ order.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import api from "../../utils/api";
import Chart from "chart.js/auto";

// ─── State ────────────────────────────────────────────────────────────────────
const isLoading = ref(true);
const stats = ref([]);
const recentOrders = ref([]);

// ─── Canvas Refs ──────────────────────────────────────────────────────────────
const revenueChartCanvas = ref(null);
const orderStatusChartCanvas = ref(null);

// ─── Chart Instances ──────────────────────────────────────────────────────────
let revenueChartInstance = null;
let orderStatusChartInstance = null;

// ─── Constants ────────────────────────────────────────────────────────────────
const STATUS_COLORS = {
  PENDING: "#f59e0b",
  PROCESSING: "#435ebe",
  SHIPPED: "#0dcaf0",
  DELIVERED: "#10b981",
  CANCELLED: "#ef4444",
};

const STATUS_CLASS_MAP = {
  PENDING: "text-warning border-warning",
  PROCESSING: "text-primary border-primary",
  SHIPPED: "text-info border-info",
  DELIVERED: "text-success border-success",
  CANCELLED: "text-danger border-danger",
};

// ─── Helpers ──────────────────────────────────────────────────────────────────
const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

const getStatusClass = (status) =>
  STATUS_CLASS_MAP[status] ?? "text-secondary border-secondary";

// ─── Build Stats ──────────────────────────────────────────────────────────────
const buildStats = (data) => [
  {
    title: "Tổng Doanh thu",
    value: formatCurrency(data.totalRevenue),
    icon: "bi bi-cash-coin",
    bg: "linear-gradient(135deg, #435ebe, #6c8cff)",
  },
  {
    title: "Tổng Đơn hàng",
    value: data.totalOrders,
    icon: "bi bi-cart-check-fill",
    bg: "linear-gradient(135deg, #10b981, #34d399)",
  },
  {
    title: "Khách hàng",
    value: data.newCustomers,
    icon: "bi bi-people-fill",
    bg: "linear-gradient(135deg, #f59e0b, #fbbf24)",
  },
  {
    title: "Cảnh báo Kho (< 10)",
    value: data.lowStockSkus + " SKU",
    icon: "bi bi-exclamation-triangle-fill",
    bg: "linear-gradient(135deg, #ef4444, #f87171)",
  },
];

// ─── Fetch ────────────────────────────────────────────────────────────────────
const fetchDashboardData = async () => {
  isLoading.value = true;
  try {
    const { data } = await api.get("/admin/dashboard");

    stats.value = buildStats(data);
    recentOrders.value = data.recentOrders;

    // Tắt loading trước để Vue render <canvas> ra DOM
    isLoading.value = false;
    await nextTick();

    renderRevenueChart(data.revenueByDay);
    renderOrderStatusChart(data.orderStatusRatio);
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu dashboard:", error);
    alert("Không thể tải dữ liệu Bảng điều khiển!");
    isLoading.value = false;
  }
};

// ─── Charts ───────────────────────────────────────────────────────────────────
const renderRevenueChart = (revenueData) => {
  revenueChartInstance?.destroy();

  revenueChartInstance = new Chart(revenueChartCanvas.value, {
    type: "line",
    data: {
      labels: Object.keys(revenueData),
      datasets: [
        {
          label: "Doanh thu (VNĐ)",
          data: Object.values(revenueData),
          borderColor: "#435ebe",
          backgroundColor: "rgba(67, 94, 190, 0.08)",
          borderWidth: 2,
          tension: 0.4,
          fill: true,
          pointRadius: 4,
          pointBackgroundColor: "#435ebe",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { color: "#f1f5f9" } },
        x: { grid: { display: false } },
      },
    },
  });
};

const renderOrderStatusChart = (statusData) => {
  orderStatusChartInstance?.destroy();

  orderStatusChartInstance = new Chart(orderStatusChartCanvas.value, {
    type: "doughnut",
    data: {
      labels: Object.keys(statusData),
      datasets: [
        {
          data: Object.values(statusData),
          backgroundColor: Object.keys(statusData).map(
            (k) => STATUS_COLORS[k] ?? "#ccc",
          ),
          borderWidth: 0,
          hoverOffset: 6,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: "bottom",
          labels: { padding: 16, font: { size: 12 } },
        },
      },
      cutout: "72%",
    },
  });
};

// ─── Lifecycle ────────────────────────────────────────────────────────────────
onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
@import "../../assets/css/dashboard.css";

/* ── Loading ─────────────────────────────────────────────────────────────── */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4rem 0;
}

/* ── Charts Row ──────────────────────────────────────────────────────────── */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  margin-top: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.chart-card__header {
  padding: 1.25rem 1.5rem 0;
}

.chart-card__title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1e293b;
}

.chart-card__body {
  padding: 1rem 1.5rem 1.5rem;
}

.chart-card__body--center {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* ── Recent Orders ───────────────────────────────────────────────────────── */
.orders-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-top: 16px;
  overflow: hidden;
}

.orders-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
}

/* ── Table ───────────────────────────────────────────────────────────────── */
.table td,
.table th {
  padding: 1rem 0.75rem;
}

/* ── Canvas ──────────────────────────────────────────────────────────────── */
canvas {
  max-height: 300px;
}
.chart-doughnut {
  max-height: 250px;
}

/* ── Responsive ──────────────────────────────────────────────────────────── */
@media (max-width: 900px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>

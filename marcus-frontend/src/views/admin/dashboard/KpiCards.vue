<script setup>
import { computed } from "vue";

const props = defineProps({
  data: { type: Object, required: true },
});

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

const stats = computed(() => [
  {
    title: "Tổng Doanh thu",
    value: formatCurrency(props.data.totalRevenue),
    icon: "bi bi-cash",
    bg: "linear-gradient(135deg, #435ebe, #6c8cff)",
    trend: props.data.revenueTrend,
  },
  {
    title: "Tổng Đơn hàng",
    value: props.data.totalOrders,
    icon: "bi-bag-check-fill",
    bg: "linear-gradient(135deg, #10b981, #34d399)",
    trend: props.data.orderTrend,
  },
  {
    title: "Khách hàng Mới",
    value: props.data.newCustomers,
    icon: "bi-people-fill",
    bg: "linear-gradient(135deg, #f59e0b, #fbbf24)",
    trend: props.data.customerTrend,
  },
  {
    title: "Cảnh báo Tồn kho",
    value: props.data.lowStockSkus + " SKU",
    icon: "bi-exclamation-triangle-fill",
    bg: "linear-gradient(135deg, #ef4444, #f87171)",
    warning: props.data.lowStockSkus > 0 ? "Cần nhập hàng" : "Kho ổn định",
  },
]);
</script>

<template>
  <div class="row g-3">
    <div class="col-xl-3 col-md-6" v-for="stat in stats" :key="stat.title">
      <div
        class="card border-0 shadow-sm rounded-4 h-100 stat-card transition-hover"
      >
        <div class="card-body p-4 d-flex align-items-center gap-3">
          <div
            class="stat-icon text-white rounded-3 d-flex align-items-center justify-content-center"
            :style="{ background: stat.bg }"
          >
            <i class="bi" :class="stat.icon"></i>
          </div>
          <div>
            <h4 class="fw-bold text-dark mb-1 fs-5">{{ stat.value }}</h4>
            <p class="text-muted small fw-medium mb-1">{{ stat.title }}</p>

            <div
              v-if="stat.trend !== undefined"
              class="small fw-bold"
              :class="stat.trend >= 0 ? 'text-success' : 'text-danger'"
            >
              <i
                class="bi"
                :class="
                  stat.trend >= 0 ? 'bi-arrow-up-short' : 'bi-arrow-down-short'
                "
              ></i>
              {{ Math.abs(stat.trend) }}% so tháng trước
            </div>
            <div
              v-else-if="stat.warning"
              :class="
                props.data.lowStockSkus > 0 ? 'text-danger' : 'text-success'
              "
              class="small fw-bold"
            >
              {{ stat.warning }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stat-icon {
  width: 54px;
  height: 54px;
  font-size: 1.5rem;
}
.transition-hover {
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}
.transition-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08) !important;
}
</style>

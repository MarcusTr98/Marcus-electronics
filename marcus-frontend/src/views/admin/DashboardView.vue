<script setup>
import { ref, onMounted, computed } from "vue";
import api from "../../utils/api";
import KpiCards from "./dashboard/KpiCards.vue";
import RevenueChart from "./dashboard/RevenueChart.vue";
import OrderStatusChart from "./dashboard/OrderStatusChart.vue";
import TopProductsChart from "./dashboard/TopProductsChart.vue";
import CompletionChart from "./dashboard/CompletionChart.vue";
import RecentOrdersTable from "./dashboard/RecentOrdersTable.vue";

const isLoading = ref(true);
const dashboardData = ref(null);

const currentDateLabel = computed(() => {
  return new Date().toLocaleDateString("vi-VN", {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
  });
});

const fetchDashboardData = async () => {
  try {
    const res = await api.get("/admin/dashboard");
    dashboardData.value = res.data;
  } catch (error) {
    console.error("Lỗi tải Dashboard:", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => fetchDashboardData());
</script>

<template>
  <div class="page-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold text-dark mb-1">Admin Dashboard</h3>
        <p class="text-muted small mb-0">
          Tổng quan kinh doanh Marcus-Electronics
        </p>
      </div>
      <div
        class="bg-white border rounded-3 px-3 py-2 shadow-sm text-secondary small fw-medium"
      >
        <i class="bi bi-calendar3 me-2 text-primary"></i>{{ currentDateLabel }}
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-5">
      <div
        class="spinner-border text-primary"
        style="width: 3rem; height: 3rem"
      ></div>
      <p class="mt-3 text-muted fw-medium">Đang đồng bộ dữ liệu hệ thống...</p>
    </div>

    <div v-else-if="dashboardData">
      <KpiCards :data="dashboardData" />

      <div class="row g-4 mt-2">
        <div class="col-lg-8">
          <RevenueChart
            :revenueData="dashboardData.revenueByDay"
            :ordersData="dashboardData.ordersByDay"
          />
        </div>
        <div class="col-lg-4">
          <OrderStatusChart :statusData="dashboardData.orderStatusRatio" />
        </div>
      </div>

      <div class="row g-4 mt-2">
        <div class="col-lg-7">
          <TopProductsChart :topProductsData="dashboardData.topProducts" />
        </div>
        <div class="col-lg-5">
          <CompletionChart
            :completionData="dashboardData.completionByDay"
            :cancellationData="dashboardData.cancellationByDay"
          />
        </div>
      </div>

      <div class="mt-4">
        <RecentOrdersTable :orders="dashboardData.recentOrders" />
      </div>
    </div>
  </div>
</template>

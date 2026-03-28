<template>
  <div class="page-content">
    <div class="page-heading">
      <div class="page-title-row">
        <div>
          <h3 class="page-title-text">Bảng điều khiển</h3>
          <p class="page-subtitle">Chào mừng trở lại, Quản trị viên 👋</p>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="text-center my-5">Đang tải dữ liệu...</div>

    <div v-else>
      <div class="stats-grid">
        <div class="stat-card" v-for="stat in stats" :key="stat.title">
          <div class="stat-icon" :style="{ background: stat.bg }">
            <i :class="stat.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.title }}</div>
            <div class="stat-change" :class="stat.trend > 0 ? 'up' : 'down'">
              <i
                :class="
                  stat.trend > 0
                    ? 'bi bi-arrow-up-short'
                    : 'bi bi-arrow-down-short'
                "
              ></i>
              {{ Math.abs(stat.trend) }}% so với tháng trước
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
// import apiService from '@/services/api'; // Tùy chỉnh theo file config Axios của bạn

const isLoading = ref(true);
const stats = ref([]);
const chartData = ref([]);
const recentOrders = ref([]);
const topProducts = ref([]);

// Khai báo hàm gọi API
const fetchDashboardData = async () => {
  isLoading.value = true;
  try {
    // Thay thế bằng Axios gọi Spring Boot API thực tế
    // const resStats = await apiService.get('/api/admin/dashboard/stats');
    // stats.value = resStats.data;

    // Giả lập nhận data từ API:
    stats.value = [
      {
        title: "Tổng doanh thu",
        value: "$48,295",
        trend: 12.5,
        icon: "bi bi-currency-dollar",
        bg: "linear-gradient(135deg,#435ebe,#6c8cff)",
      },
      {
        title: "Tổng đơn hàng",
        value: "1,842",
        trend: 8.2,
        icon: "bi bi-cart-check-fill",
        bg: "linear-gradient(135deg,#28a745,#4cd964)",
      },
      // ... nhận data mapping từ backend
    ];
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu dashboard:", error);
    // Xử lý thông báo lỗi UI ở đây
  } finally {
    isLoading.value = false;
  }
};

// Tự động gọi API khi component được mount
onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
@import "../../assets/css/dashboard.css";
</style>

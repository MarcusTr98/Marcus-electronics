<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import Chart from "chart.js/auto";

const props = defineProps({
  revenueData: Object,
  ordersData: Object,
});

const canvasRef = ref(null);
let chartInstance = null;

const renderChart = () => {
  if (chartInstance) chartInstance.destroy(); // Chống rò rỉ bộ nhớ

  const labels = Object.keys(props.revenueData || {});
  const revValues = Object.values(props.revenueData || {});
  const ordValues = Object.values(props.ordersData || {});

  chartInstance = new Chart(canvasRef.value, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          type: "bar",
          label: "Doanh thu (VNĐ)",
          data: revValues,
          backgroundColor: "rgba(67, 94, 190, 0.2)",
          borderColor: "#435ebe",
          borderWidth: 2,
          borderRadius: 6,
          yAxisID: "yRevenue",
        },
        {
          type: "line",
          label: "Số đơn",
          data: ordValues,
          borderColor: "#10b981",
          backgroundColor: "#10b981",
          borderWidth: 3,
          tension: 0.4,
          yAxisID: "yOrders",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: "top" } },
      scales: {
        yRevenue: {
          position: "left",
          ticks: { callback: (v) => (v >= 1e6 ? v / 1e6 + "M" : v) },
        },
        yOrders: { position: "right", grid: { drawOnChartArea: false } },
      },
    },
  });
};

onMounted(() => renderChart());
watch(
  () => props.revenueData,
  () => renderChart(),
  { deep: true },
); // Tự vẽ lại nếu data thay đổi

onUnmounted(() => {
  if (chartInstance) chartInstance.destroy(); // Quan trọng!
});
</script>

<template>
  <div class="card border-0 shadow-sm rounded-4 h-100 bg-white">
    <div class="card-body p-4">
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h6 class="fw-bold text-dark mb-1">Doanh thu & Đơn hàng</h6>
          <p class="text-muted small mb-0">Thống kê 7 ngày qua</p>
        </div>
        <span
          class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 rounded-pill"
          >Tuần này</span
        >
      </div>
      <div style="height: 300px">
        <canvas ref="canvasRef"></canvas>
      </div>
    </div>
  </div>
</template>

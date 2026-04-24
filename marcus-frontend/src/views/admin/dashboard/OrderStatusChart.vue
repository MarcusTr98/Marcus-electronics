<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from "vue";
import Chart from "chart.js/auto";

const props = defineProps({
  statusData: Object,
});

const canvasRef = ref(null);
let chartInstance = null;

const STATUS_COLORS = {
  PENDING: "#f59e0b",
  PROCESSING: "#435ebe",
  SHIPPED: "#0dcaf0",
  DELIVERED: "#10b981",
  CANCELLED: "#ef4444",
};

const STATUS_LABELS = {
  PENDING: "Chờ xử lý",
  PROCESSING: "Đang xử lý",
  SHIPPED: "Đang giao",
  DELIVERED: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

const renderChart = () => {
  if (chartInstance) chartInstance.destroy();
  if (!props.statusData) return;

  const labels = Object.keys(props.statusData).map(
    (k) => STATUS_LABELS[k] || k,
  );
  const dataValues = Object.values(props.statusData);
  const bgColors = Object.keys(props.statusData).map(
    (k) => STATUS_COLORS[k] || "#ccc",
  );

  chartInstance = new Chart(canvasRef.value, {
    type: "doughnut",
    data: {
      labels,
      datasets: [
        {
          data: dataValues,
          backgroundColor: bgColors,
          borderWidth: 3,
          borderColor: "#fff",
          hoverOffset: 8,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: "bottom", labels: { boxWidth: 12 } } },
      cutout: "70%",
    },
  });
};

onMounted(() => renderChart());
watch(
  () => props.statusData,
  () => renderChart(),
  { deep: true },
);

onUnmounted(() => {
  if (chartInstance) chartInstance.destroy();
});
</script>

<template>
  <div class="card border-0 shadow-sm rounded-4 h-100 bg-white">
    <div class="card-body p-4">
      <h6 class="fw-bold text-dark mb-1">Tỷ lệ Trạng thái Đơn hàng</h6>
      <p class="text-muted small mb-4">Phân bố theo tình trạng</p>

      <div style="height: 250px" class="d-flex justify-content-center">
        <canvas ref="canvasRef"></canvas>
      </div>
    </div>
  </div>
</template>

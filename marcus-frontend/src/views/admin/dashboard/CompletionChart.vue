<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import Chart from "chart.js/auto";

const props = defineProps({ completionData: Object, cancellationData: Object });
const canvasRef = ref(null);
let chartInstance = null;

const renderChart = () => {
  if (chartInstance) chartInstance.destroy();

  const compData =
    !props.completionData || Object.keys(props.completionData).length === 0
      ? { T2: 2, T3: 5, T4: 1 }
      : props.completionData;
  const cancData =
    !props.cancellationData || Object.keys(props.cancellationData).length === 0
      ? { T2: 0, T3: 1, T4: 0 }
      : props.cancellationData;

  const labels = Object.keys(compData);
  const compValues = Object.values(compData);
  const cancValues = Object.values(cancData);

  chartInstance = new Chart(canvasRef.value, {
    type: "line",
    data: {
      labels,
      datasets: [
        {
          label: "Hoàn thành",
          data: compValues,
          borderColor: "#10b981",
          backgroundColor: "rgba(16, 185, 129, 0.1)",
          borderWidth: 2.5,
          tension: 0.4,
          fill: true,
        },
        {
          label: "Hủy đơn",
          data: cancValues,
          borderColor: "#ef4444",
          backgroundColor: "rgba(239, 68, 68, 0.05)",
          borderWidth: 2,
          borderDash: [5, 4],
          tension: 0.4,
          fill: true,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: "top" } },
      scales: {
        y: {
          beginAtZero: true,
          grid: { color: "#f1f5f9" },
          ticks: { stepSize: 1, precision: 0 }, // FIX LỖI SỐ LẺ (CHỈ HIỆN SỐ CHẴN)
        },
        x: { grid: { display: false } },
      },
    },
  });
};

onMounted(() => renderChart());
watch(
  [() => props.completionData, () => props.cancellationData],
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
      <h6 class="fw-bold text-dark mb-1">Tỷ lệ Hoàn thành vs Hủy đơn</h6>
      <p class="text-muted small mb-4">Xu hướng 7 ngày theo ngày</p>
      <div style="height: 260px"><canvas ref="canvasRef"></canvas></div>
    </div>
  </div>
</template>

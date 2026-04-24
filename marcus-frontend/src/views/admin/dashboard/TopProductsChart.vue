<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import Chart from "chart.js/auto";

const props = defineProps({ topProductsData: Object });
const canvasRef = ref(null);
let chartInstance = null;

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

const renderChart = () => {
  if (chartInstance) chartInstance.destroy();

  const dataToRender =
    !props.topProductsData || Object.keys(props.topProductsData).length === 0
      ? {
          "iPhone 15 Pro Max": { revenue: 120000000, quantity: 4 },
          "MacBook Pro M3": { revenue: 95000000, quantity: 2 },
        }
      : props.topProductsData;

  const labels = Object.keys(dataToRender);
  const revenueValues = labels.map((k) => dataToRender[k].revenue);

  chartInstance = new Chart(canvasRef.value, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Doanh thu",
          data: revenueValues,
          backgroundColor: "rgba(67, 94, 190, 0.7)",
          borderRadius: 6,
        },
      ],
    },
    options: {
      indexAxis: "y",
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        // CUSTOM TOOLTIP ĐỂ HIỆN CẢ TIỀN & SỐ LƯỢNG
        tooltip: {
          callbacks: {
            label: (context) => {
              const productName = context.label;
              const productInfo = dataToRender[productName];
              return [
                `Doanh thu: ${formatCurrency(productInfo.revenue)}`,
                `Đã bán: ${productInfo.quantity} chiếc`,
              ];
            },
          },
        },
      },
      scales: {
        x: {
          grid: { color: "#f1f5f9" },
          ticks: { callback: (v) => (v >= 1e6 ? v / 1e6 + "M" : v) },
        },
        y: { grid: { display: false } },
      },
    },
  });
};

onMounted(() => renderChart());
watch(
  () => props.topProductsData,
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
      <h6 class="fw-bold text-dark mb-1">Top Sản phẩm Bán chạy</h6>
      <p class="text-muted small mb-4">Theo doanh thu & số lượng</p>
      <div style="height: 280px"><canvas ref="canvasRef"></canvas></div>
    </div>
  </div>
</template>
<script setup></script>

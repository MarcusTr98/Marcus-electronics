<script setup>
import { ref, onMounted, computed } from "vue";
import api from "../../utils/api";

const isLoading = ref(false);
const reportData = ref(null);

const today = new Date();
const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

const startDate = ref(firstDayOfMonth.toISOString().split("T")[0]);
const endDate = ref(today.toISOString().split("T")[0]);

const currentPage = ref(1);
const itemsPerPage = 6;

const fetchReport = async () => {
  if (startDate.value > endDate.value) {
    alert("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
    return;
  }
  isLoading.value = true;
  try {
    const res = await api.get(
      `/admin/reports/revenue?startDate=${startDate.value}&endDate=${endDate.value}`,
    );
    reportData.value = res.data;
    currentPage.value = 1;
  } catch (error) {
    console.error("Lỗi tải báo cáo:", error);
    alert("Không thể tải dữ liệu báo cáo.");
  } finally {
    isLoading.value = false;
  }
};

const totalPages = computed(() => {
  if (!reportData.value || !reportData.value.dailyRevenues) return 0;
  return Math.ceil(reportData.value.dailyRevenues.length / itemsPerPage);
});

const paginatedRevenues = computed(() => {
  if (!reportData.value || !reportData.value.dailyRevenues) return [];
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return reportData.value.dailyRevenues.slice(start, end);
});

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";
const formatDate = (dateStr) => {
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
};

const exportToExcel = () => {
  if (!reportData.value || reportData.value.dailyRevenues.length === 0) return;

  let tableHTML = `
    <table border="1">
      <thead>
        <tr>
          <th style="background-color: #435ebe; color: #ffffff; font-weight: bold; padding: 10px;">Ngày</th>
          <th style="background-color: #435ebe; color: #ffffff; font-weight: bold; padding: 10px;">Số đơn hoàn thành</th>
          <th style="background-color: #435ebe; color: #ffffff; font-weight: bold; padding: 10px;">Doanh thu (VNĐ)</th>
        </tr>
      </thead>
      <tbody>
  `;

  reportData.value.dailyRevenues.forEach((row) => {
    tableHTML += `
      <tr>
        <td style="padding: 5px;">${formatDate(row.date)}</td>
        <td style="padding: 5px; text-align: center;">${row.totalOrders}</td>
        <td style="padding: 5px; text-align: right;">${row.revenue}</td>
      </tr>
    `;
  });
  tableHTML += `</tbody></table>`;

  const template = `
    <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
      <head><meta charset="UTF-8"></head>
      <body>${tableHTML}</body>
    </html>
  `;

  const base64 = btoa(unescape(encodeURIComponent(template)));
  const link = document.createElement("a");
  link.href = "data:application/vnd.ms-excel;base64," + base64;
  link.download = `BaoCao_DoanhThu_${startDate.value}_den_${endDate.value}.xls`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

onMounted(() => fetchReport());
</script>

<template>
  <div class="page-content">
    <div class="page-heading mb-4">
      <h3 class="page-title-text">Báo Cáo Doanh Thu</h3>
      <p class="page-subtitle">Phân tích chuyên sâu dữ liệu bán hàng</p>
    </div>

    <div v-if="isLoading" class="text-center py-5">
      <div
        class="spinner-border text-primary"
        style="width: 3rem; height: 3rem"
      ></div>
      <p class="mt-3 text-muted fw-medium">Đang trích xuất dữ liệu...</p>
    </div>

    <div v-else-if="reportData">
      <div class="row g-3 mb-4">
        <div class="col-lg-4">
          <div class="card border-0 shadow-sm rounded-4 bg-white mb-0">
            <div
              class="card-body p-4 d-flex flex-column justify-content-center"
            >
              <div
                class="text-muted fw-bold small text-uppercase mb-3"
                style="letter-spacing: 0.5px"
              >
                <i class="bi bi-calendar-range me-1"></i> Bộ lọc thời gian
              </div>
              <div class="d-flex align-items-center gap-2 mb-3">
                <input
                  type="date"
                  v-model="startDate"
                  class="form-control form-control-sm border shadow-none fw-medium text-secondary rounded-3 px-2 py-1"
                />
                <span class="text-muted fw-bold">-</span>
                <input
                  type="date"
                  v-model="endDate"
                  class="form-control form-control-sm border shadow-none fw-medium text-secondary rounded-3 px-2 py-1"
                />
              </div>
              <button
                class="btn btn-primary w-100 fw-bold rounded-3 shadow-sm py-2"
                @click="fetchReport"
                :disabled="isLoading"
              >
                <i class="bi bi-funnel-fill me-1"></i> Trích xuất
              </button>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div
            class="card mb-0 shadow-sm"
            style="
              background: linear-gradient(135deg, #435ebe, #6c8cff);
              color: white;
              border: none;
              border-radius: 16px;
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
                  Tổng doanh thu kỳ này
                </h6>
                <h3 class="mb-0 fw-bold text-white">
                  {{ formatCurrency(reportData.totalRevenue) }}
                </h3>
              </div>
              <div class="fs-1 opacity-50">
                <i class="bi bi-cash-stack"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div
            class="card mb-0 shadow-sm"
            style="
              background: linear-gradient(135deg, #10b981, #34d399);
              color: white;
              border: none;
              border-radius: 16px;
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
                  Đơn hàng hoàn thành
                </h6>
                <h3 class="mb-0 fw-bold text-white">
                  {{ reportData.totalOrders }}
                  <span class="fs-5 text-white-50 fw-normal">đơn</span>
                </h3>
              </div>
              <div class="fs-1 opacity-50">
                <i class="bi bi-cart-check-fill"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card border-0 shadow-sm rounded-4 bg-white overflow-hidden">
        <div class="card-header-custom flex-wrap gap-3">
          <div>
            <h6 class="fw-bold text-dark mb-0 fs-5">
              Chi tiết doanh thu theo ngày
            </h6>
            <p class="text-muted small mb-0 mt-1">
              Hiển thị {{ itemsPerPage }} ngày/trang
            </p>
          </div>
          <button
            class="btn btn-success rounded-pill px-4 py-2 fw-bold shadow-sm transition-hover d-flex align-items-center gap-2"
            @click="exportToExcel"
          >
            <i class="bi bi-file-earmark-excel-fill fs-5"></i> Xuất File Excel
          </button>
        </div>

        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th class="ps-4 py-3 fw-bold fs-6">Ngày</th>
                <th class="text-center fw-bold fs-6">Số đơn hoàn thành</th>
                <th class="text-end pe-4 fw-bold fs-6">Doanh thu ghi nhận</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paginatedRevenues.length === 0">
                <td colspan="3" class="text-center py-5 text-muted fs-5">
                  Không có dữ liệu bán hàng.
                </td>
              </tr>
              <tr v-for="day in paginatedRevenues" :key="day.date">
                <td class="ps-4 fw-bold text-dark fs-6">
                  {{ formatDate(day.date) }}
                </td>
                <td class="text-center">
                  <span
                    class="badge bg-secondary-subtle text-secondary px-4 py-2 rounded-pill fs-6"
                    >{{ day.totalOrders }}</span
                  >
                </td>
                <td
                  class="text-end pe-4 fw-bold fs-5"
                  :class="day.revenue === 0 ? 'text-danger' : 'text-success'"
                >
                  {{
                    day.revenue === 0
                      ? "0 ₫"
                      : "+" + formatCurrency(day.revenue)
                  }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div
          v-if="totalPages > 1"
          class="d-flex justify-content-between align-items-center p-4 bg-white border-top"
        >
          <span class="text-muted small fw-medium"
            >Đang xem trang {{ currentPage }} / {{ totalPages }}</span
          >
          <nav aria-label="Page navigation">
            <ul class="pagination pagination-sm mb-0 shadow-sm">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a
                  class="page-link fw-bold text-secondary"
                  href="#"
                  @click.prevent="changePage(currentPage - 1)"
                  ><i class="bi bi-chevron-left"></i> Trước</a
                >
              </li>
              <li
                class="page-item"
                v-for="p in totalPages"
                :key="p"
                :class="{ active: p === currentPage }"
              >
                <a
                  class="page-link fw-bold"
                  href="#"
                  @click.prevent="changePage(p)"
                  >{{ p }}</a
                >
              </li>
              <li
                class="page-item"
                :class="{ disabled: currentPage === totalPages }"
              >
                <a
                  class="page-link fw-bold text-secondary"
                  href="#"
                  @click.prevent="changePage(currentPage + 1)"
                  >Sau <i class="bi bi-chevron-right"></i
                ></a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Chỉ giữ lại các tùy chỉnh đè nhỏ giọt */
input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.6;
}
input[type="date"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}
</style>

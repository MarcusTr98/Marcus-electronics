<script setup>
import { computed } from "vue";

const props = defineProps({ orders: Array });

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

const getStatusBadge = (status) => {
  switch (status) {
    case "PENDING":
      return { text: "Chờ xử lý", class: "bg-warning text-dark" };
    case "PROCESSING":
      return { text: "Đang xử lý", class: "bg-primary" };
    case "SHIPPED":
      return { text: "Đang giao", class: "bg-info text-dark" };
    case "DELIVERED":
      return { text: "Hoàn thành", class: "bg-success" };
    case "CANCELLED":
      return { text: "Đã hủy", class: "bg-danger" };
    default:
      return { text: status, class: "bg-secondary" };
  }
};
</script>

<template>
  <div class="card border-0 shadow-sm rounded-4 bg-white overflow-hidden">
    <div
      class="card-header bg-white border-bottom p-4 d-flex justify-content-between align-items-center"
    >
      <h6 class="fw-bold text-dark mb-0">Đơn hàng mới nhất</h6>
      <router-link
        to="/admin/orders"
        class="btn btn-sm btn-outline-primary rounded-pill px-3 fw-bold"
      >
        Xem tất cả <i class="bi bi-arrow-right ms-1"></i>
      </router-link>
    </div>

    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light text-muted small">
          <tr>
            <th class="ps-4 py-3">Mã ĐH</th>
            <th>Khách hàng</th>
            <th>Ngày đặt</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="!orders || orders.length === 0">
            <td colspan="5" class="text-center py-5 text-muted">
              Chưa có đơn hàng nào.
            </td>
          </tr>
          <tr v-for="order in orders" :key="order.id">
            <td class="ps-4 fw-bold text-primary">#ORD-{{ order.id }}</td>
            <td>
              <div class="fw-bold text-dark">{{ order.fullName }}</div>
              <div class="small text-muted">{{ order.phoneNumber }}</div>
            </td>
            <td class="text-muted small">
              {{ new Date(order.orderDate).toLocaleDateString("vi-VN") }}
            </td>
            <td class="fw-bold text-danger">
              {{ formatCurrency(order.totalMoney) }}
            </td>
            <td>
              <span
                class="badge rounded-pill px-3 py-2"
                :class="getStatusBadge(order.status).class"
              >
                {{ getStatusBadge(order.status).text }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

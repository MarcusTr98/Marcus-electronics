<template>
  <div class="page-content">
    <div class="page-heading d-flex justify-content-between align-items-center">
      <h3 class="page-title-text">Quản lý Tồn kho</h3>
      <div class="d-flex gap-2">
        <span class="badge bg-danger p-2"
          >Sắp hết hàng: {{ lowStockCount }}</span
        >
        <span class="badge bg-primary p-2">Tổng SKU: {{ allSkus.length }}</span>
      </div>
    </div>

    <div class="card mt-3">
      <div class="card-body">
        <div class="row mb-4">
          <div class="col-md-6">
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-search"></i></span>
              <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Tìm theo tên sản phẩm hoặc mã SKU..."
              />
            </div>
          </div>
          <div class="col-md-3">
            <select v-model="filterStatus" class="form-select">
              <option value="all">Tất cả trạng thái</option>
              <option value="low">Sắp hết hàng (< 10)</option>
              <option value="out">Hết hàng (0)</option>
            </select>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-hover align-middle border">
            <thead class="table-light">
              <tr>
                <th>Sản phẩm gốc</th>
                <th>Mã SKU / Biến thể</th>
                <th class="text-center">Giá hiện tại</th>
                <th class="text-center" style="width: 150px">Số lượng kho</th>
                <th class="text-center">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sku in filteredSkus" :key="sku.skuId">
                <td>
                  <div class="fw-bold">{{ sku.productName }}</div>
                  <small class="text-muted">{{ sku.categoryName }}</small>
                </td>
                <td>
                  <code class="text-primary fw-bold">{{ sku.skuCode }}</code>
                  <div class="small text-secondary">
                    {{ sku.variantDetail }}
                  </div>
                </td>
                <td class="text-center text-danger fw-bold">
                  {{ formatCurrency(sku.price) }}
                </td>
                <td class="text-center">
                  <input
                    type="number"
                    v-model.number="sku.stock"
                    class="form-control form-control-sm text-center"
                    :class="{ 'border-danger text-danger': sku.stock < 10 }"
                    @change="updateStock(sku)"
                  />
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-sm btn-light"
                    @click="viewHistory(sku.skuId)"
                    title="Lịch sử nhập xuất"
                  >
                    <i class="bi bi-clock-history"></i>
                  </button>
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
import { ref, computed, onMounted } from "vue";
import api from "../../utils/api";

const allSkus = ref([]);
const searchQuery = ref("");
const filterStatus = ref("all");

const fetchInventory = async () => {
  try {
    const res = await api.get("/admin/inventory");
    allSkus.value = res.data;
  } catch (err) {
    console.error("Lỗi tải kho hàng", err);
  }
};

const filteredSkus = computed(() => {
  return allSkus.value.filter((s) => {
    const matchSearch =
      s.productName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      s.skuCode.toLowerCase().includes(searchQuery.value.toLowerCase());

    if (filterStatus.value === "low")
      return matchSearch && s.stock < 10 && s.stock > 0;
    if (filterStatus.value === "out") return matchSearch && s.stock <= 0;
    return matchSearch;
  });
});

const lowStockCount = computed(
  () => allSkus.value.filter((s) => s.stock < 10).length,
);

const updateStock = async (sku) => {
  if (sku.stock < 0) return alert("Số lượng không được âm!");
  try {
    await api.patch(`/admin/inventory/${sku.skuId}/stock`, {
      stock: sku.stock,
    });
    console.log(`Đã cập nhật SKU ${sku.skuId} thành ${sku.stock}`);
  } catch (err) {
    alert("Lỗi cập nhật kho!");
    fetchInventory();
  }
};

const viewHistory = (skuId) => {
  alert(`Tính năng đang phát triển: Xem lịch sử của SKU ID ${skuId}`);
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

onMounted(fetchInventory);
</script>

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

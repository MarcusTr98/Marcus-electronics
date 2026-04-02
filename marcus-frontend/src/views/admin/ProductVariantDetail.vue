<template>
  <div class="page-content">
    <div class="page-heading d-flex justify-content-between align-items-center">
      <div>
        <h3 class="page-title-text mb-1">Chi tiết Biến thể Sản phẩm</h3>
        <p class="text-muted mb-0" v-if="product">
          Sản phẩm: <span class="fw-bold text-primary">{{ product.name }}</span>
        </p>
      </div>
      <button
        class="btn btn-secondary"
        @click="$router.push('/admin/products')"
      >
        <i class="bi bi-arrow-left"></i> Quay lại
      </button>
    </div>

    <div class="card mt-3">
      <div class="card-body">
        <div v-if="isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-2">Đang tải dữ liệu...</p>
        </div>

        <div v-else-if="skus.length === 0" class="text-center py-5 text-muted">
          Sản phẩm này chưa có biến thể nào được tạo.
        </div>

        <div class="table-responsive" v-else>
          <table class="table table-bordered align-middle table-hover">
            <thead class="table-light">
              <tr>
                <th style="width: 80px" class="text-center">Ảnh</th>
                <th>Mã SKU</th>
                <th>Cấu hình (Thuộc tính)</th>
                <th>Giá bán</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th class="text-center">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sku in skus" :key="sku.id">
                <td class="text-center">
                  <img
                    :src="sku.imageUrl || 'https://placehold.co/400x400'"
                    class="rounded"
                    style="
                      width: 50px;
                      height: 50px;
                      object-fit: cover;
                      border: 1px solid #ddd;
                    "
                  />
                </td>
                <td>
                  <code class="text-dark fw-bold">{{ sku.skuCode }}</code>
                </td>
                <td>
                  <span
                    v-for="(val, key) in sku.options"
                    :key="key"
                    class="badge bg-light text-dark border me-1 mb-1"
                  >
                    {{ key }}: {{ val }}
                  </span>
                </td>
                <td class="text-danger fw-bold">
                  {{ formatCurrency(sku.price) }}
                </td>
                <td>
                  <span :class="{ 'text-danger fw-bold': sku.stock < 5 }"
                    >{{ sku.stock }} pcs</span
                  >
                </td>
                <td>
                  <span
                    class="badge"
                    :class="sku.active ? 'bg-success' : 'bg-danger'"
                  >
                    {{ sku.active ? "Đang bán" : "Đã khóa" }}
                  </span>
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-sm btn-outline-primary me-2"
                    title="Cập nhật ảnh/giá"
                    @click="openEditModal(sku)"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button
                    v-if="sku.active"
                    class="btn btn-sm btn-outline-danger"
                    @click="toggleSkuStatus(sku.id, false)"
                    title="Khóa SKU này"
                  >
                    <i class="bi bi-lock-fill"></i>
                  </button>
                  <button
                    v-else
                    class="btn btn-sm btn-outline-success"
                    @click="toggleSkuStatus(sku.id, true)"
                    title="Mở khóa SKU"
                  >
                    <i class="bi bi-unlock-fill"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  <div
    class="modal-overlay"
    v-if="showEditModal"
    @click.self="showEditModal = false"
  >
    <div class="modal-box modal-md">
      <div class="modal-header-custom">
        <h5>Cập nhật Biến thể</h5>
        <button class="modal-close" @click="showEditModal = false">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body-custom">
        <div class="row">
          <div class="col-md-4 text-center border-end">
            <label class="d-block mb-2 fw-bold text-sm">Ảnh biến thể</label>
            <div class="img-preview mb-2 mx-auto">
              <img
                :src="editingSku.imageUrl || 'https://placehold.co/400x400'"
                class="img-thumbnail"
                style="height: 120px; width: 120px; object-fit: cover"
              />
            </div>
            <input
              type="file"
              ref="fileInput"
              class="d-none"
              @change="handleFileUpload"
            />
            <button
              class="btn btn-sm btn-outline-primary mt-1"
              @click="fileInput.click()"
            >
              Đổi ảnh
            </button>
          </div>

          <div class="col-md-8">
            <div class="mb-3">
              <label class="form-label text-sm fw-bold">Mã SKU</label>
              <input
                v-model="editingSku.skuCode"
                type="text"
                class="form-control form-control-sm"
              />
            </div>
            <div class="mb-3">
              <label class="form-label text-sm fw-bold">Giá bán (₫)</label>
              <input
                v-model="editingSku.price"
                type="number"
                class="form-control form-control-sm text-danger fw-bold"
              />
            </div>
            <div class="mb-3">
              <label class="form-label text-sm fw-bold">Tồn kho</label>
              <input
                v-model="editingSku.stock"
                type="number"
                class="form-control form-control-sm"
              />
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer-custom">
        <button class="btn-cancel" @click="showEditModal = false">Hủy</button>
        <button class="btn-save" @click="handleSaveSku">Lưu thay đổi</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import api from "../../utils/api";

const route = useRoute();
const productId = route.params.id;

const isLoading = ref(true);
const product = ref(null);
const skus = ref([]);

// Hàm tải chi tiết sản phẩm và SKUs
const fetchProductVariants = async () => {
  isLoading.value = true;
  try {
    // Gọi API lấy detail. Cần đảm bảo Backend trả về mảng skus như ProductDetailResponseDTO bạn từng thiết kế
    const res = await api.get(`/admin/products/${productId}/skus`);

    // Tùy cấu trúc API của bạn, giả sử Backend trả về { product: {...}, skus: [...] }
    product.value = res.data.product;
    skus.value = res.data.skus;
  } catch (error) {
    alert("Lỗi khi tải danh sách biến thể!");
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

// Khóa hoặc mở khóa một SKU cụ thể (Soft Delete)
const toggleSkuStatus = async (skuId, isActive) => {
  const actionText = isActive ? "mở khóa" : "khóa";
  if (!confirm(`Bạn có chắc chắn muốn ${actionText} SKU này?`)) return;

  try {
    // 1. Gọi API
    const response = await api.patch(
      `/admin/products/${productId}/skus/${skuId}/status`,
      {
        active: isActive,
      },
    );

    // 2. Cập nhật lại UI nếu API thành công
    const targetSku = skus.value.find((s) => s.id === skuId);
    if (targetSku) {
      targetSku.active = isActive;
    }

    console.log(`Đã ${actionText} SKU ${skuId} thành công`);
  } catch (error) {
    // 3. Hiển thị chính xác lỗi từ Backend trả về
    console.error("Lỗi khóa SKU:", error);
    const errorMsg =
      error.response?.data?.message || error.response?.data || error.message;
    alert(`Lỗi khi ${actionText} SKU: \n` + errorMsg);
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

onMounted(() => {
  fetchProductVariants();
});

// === Thêm State quản lý Modal Update ===
const showEditModal = ref(false);
const editingSku = ref({
  id: null,
  skuCode: "",
  price: 0,
  stock: 0,
  imageUrl: "",
});
const fileInput = ref(null);

// Mở Modal và đổ dữ liệu
const openEditModal = (sku) => {
  editingSku.value = { ...sku }; // Copy dữ liệu để không mutate trực tiếp state
  showEditModal.value = true;
};

// Upload ảnh (Dùng chung API với sản phẩm gốc của bạn)
const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await api.post("/admin/files/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    editingSku.value.imageUrl = "http://localhost:8080" + res.data;
  } catch (err) {
    alert("Lỗi upload ảnh!");
  }
};

// Lưu thông tin SKU
const handleSaveSku = async () => {
  try {
    const payload = {
      skuCode: editingSku.value.skuCode,
      price: editingSku.value.price,
      stock: editingSku.value.stock,
      imageUrl: editingSku.value.imageUrl,
    };

    await api.put(
      `/admin/products/${productId}/skus/${editingSku.value.id}`,
      payload,
    );

    showEditModal.value = false;
    alert("Cập nhật biến thể thành công!");
    fetchProductVariants(); // Tải lại danh sách
  } catch (error) {
    alert("Lỗi cập nhật: " + (error.response?.data?.message || error.message));
  }
};
</script>

<style scoped>
@import "../../assets/css/product-manage.css";
/* Ép màu chữ đen đậm cho toàn bộ label trong modal */
.modal-body-custom label {
  font-size: 0.85rem;
  font-weight: 700;
  color: #111827; /* Đen đậm thay vì xám nhạt */
  margin-bottom: 6px;
}

/* Ép màu và viền cho các input/select/textarea của Bootstrap */
.modal-body-custom .form-control,
.modal-body-custom .form-select {
  color: #000000; /* Text màu đen tuyền */
  font-weight: 500;
  border: 1px solid #cbd5e1;
  background-color: #f8fafc;
}

.modal-body-custom .form-control:focus,
.modal-body-custom .form-select:focus {
  color: #000000;
  background-color: #ffffff;
  border-color: #435ebe;
  box-shadow: 0 0 0 3px rgba(67, 94, 190, 0.15);
}
</style>

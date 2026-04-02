<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api";
import ProductModal from "./ProductModal.vue";

// ==================== STATE ====================
const allProducts = ref([]);
const categories = ref([]);
const isLoading = ref(false);
const isShowModal = ref(false);
const editingProduct = ref(null);

// ==================== API CALLS ====================
const fetchProducts = async () => {
  isLoading.value = true;
  try {
    const res = await api.get("/admin/products");
    allProducts.value = res.data;
  } catch (error) {
    alert("Lỗi khi tải danh sách sản phẩm!");
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

const fetchCategories = async () => {
  try {
    const res = await api.get("/categories");
    categories.value = res.data;
  } catch (error) {
    console.error("Lỗi tải danh mục:", error);
  }
};

// ==================== MODAL ====================
const openAddModal = () => {
  editingProduct.value = null;
  isShowModal.value = true;
};

// ==================== CRUD ====================
const editProduct = async (productRow) => {
  try {
    const res = await api.get(`/admin/products/${productRow.id}`);
    editingProduct.value = {
      id: res.data.id,
      name: res.data.name,
      slug: res.data.slug,
      categoryId: res.data.category?.id ?? null,
      basePrice: res.data.basePrice,
      thumbnailUrl: res.data.thumbnailUrl,
      weightG: res.data.weightG,
      lengthCm: res.data.lengthCm,
      widthCm: res.data.widthCm,
      heightCm: res.data.heightCm,
      description: res.data.description,
      active: res.data.isActive,
    };
    isShowModal.value = true;
  } catch (error) {
    alert("Không thể tải thông tin chi tiết sản phẩm!");
    console.error(error);
  }
};

const handleSave = async (productData) => {
  try {
    if (editingProduct.value) {
      await api.put(`/admin/products/${editingProduct.value.id}`, productData);
    } else {
      await api.post("/admin/products", productData);
    }
    isShowModal.value = false;
    fetchProducts();
    alert("Thao tác thành công!");
  } catch (err) {
    alert("Lỗi: " + (err.response?.data?.message || err.message));
  }
};

const confirmDelete = async (id) => {
  if (!confirm("Bạn có chắc chắn muốn xóa mềm sản phẩm này?")) return;
  try {
    await api.delete(`/admin/products/${id}`);
    const target = allProducts.value.find((p) => p.id === id);
    if (target) target.active = false;
  } catch (error) {
    alert("Lỗi khi xóa sản phẩm!");
  }
};

const handleRestore = async (id) => {
  if (!confirm("Khôi phục lại sản phẩm này?")) return;
  try {
    await api.put(`/admin/products/${id}/restore`);
    const target = allProducts.value.find((p) => p.id === id);
    if (target) target.active = true;
  } catch (error) {
    alert("Lỗi khi khôi phục sản phẩm!");
  }
};

// ==================== UTILS ====================
const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

// ==================== LIFECYCLE ====================
onMounted(() => {
  fetchCategories();
  fetchProducts();
});
</script>

<template>
  <div class="page-content">
    <div class="page-heading">
      <h3 class="page-title-text">Quản lý sản phẩm gốc</h3>
    </div>

    <div class="card">
      <div class="card-header-custom">
        <h6 class="card-title-text">Danh sách sản phẩm</h6>
        <button class="btn-add" @click="openAddModal">Thêm sản phẩm</button>
      </div>

      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Sản phẩm</th>
              <th>Danh mục</th>
              <th>Biến thể</th>
              <th>Giá cơ bản</th>
              <th>Tổng tồn kho</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(p, i) in allProducts" :key="p.id">
              <td>{{ i + 1 }}</td>
              <td>
                <div class="d-flex align-items-center">
                  <img
                    :src="p.thumbnailUrl || 'https://placehold.co/400x400'"
                    class="me-2 rounded"
                    style="width: 40px; height: 40px; object-fit: cover"
                  />
                  <div>{{ p.name }}</div>
                </div>
              </td>
              <td>{{ p.categoryName }}</td>
              <td>
                <template
                  v-if="
                    p.variantSummary && Object.keys(p.variantSummary).length
                  "
                >
                  <span
                    v-for="(count, name) in p.variantSummary"
                    :key="name"
                    class="badge bg-info text-dark me-1"
                    style="font-size: 0.7rem"
                  >
                    {{ name }}: {{ count }}
                  </span>
                </template>
                <span v-else class="text-muted small">Chưa có SKU</span>
              </td>
              <td class="text-danger fw-bold">
                {{ formatCurrency(p.basePrice) }}
              </td>
              <td class="text-center">{{ p.totalStock || 0 }} pcs</td>
              <td>
                <span :class="p.active ? 'text-success fw-bold' : 'text-muted'">
                  {{ p.active ? "Hoạt động" : "Đã xóa" }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-sm btn-outline-primary me-2"
                  title="Chỉnh sửa"
                  @click="editProduct(p)"
                >
                  <i class="bi bi-pencil"></i>
                </button>

                <button
                  v-if="p.active"
                  class="btn btn-sm btn-outline-danger me-2"
                  title="Xóa mềm"
                  @click="confirmDelete(p.id)"
                >
                  <i class="bi bi-trash"></i>
                </button>

                <button
                  v-else
                  class="btn btn-sm btn-outline-success me-2"
                  title="Khôi phục sản phẩm"
                  @click="handleRestore(p.id)"
                >
                  <i class="bi bi-arrow-counterclockwise"></i>
                </button>

                <router-link
                  :to="`/admin/products/${p.id}/variants`"
                  class="btn btn-sm btn-outline-info"
                  title="Xem chi tiết SKU"
                >
                  <i class="bi bi-eye"></i>
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <ProductModal
      :show="isShowModal"
      :editingProduct="editingProduct"
      :categories="categories"
      @close="isShowModal = false"
      @save="handleSave"
    />
  </div>
</template>

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

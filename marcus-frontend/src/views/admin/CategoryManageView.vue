<template>
  <div class="page-content">
    <div class="page-heading">
      <div class="page-title-row">
        <div>
          <h3 class="page-title-text">Quản lý danh mục</h3>
          <p class="page-subtitle">Thêm, sửa, xóa cấu trúc phân cấp danh mục</p>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header-custom">
        <h6 class="card-title-text">Cây danh mục</h6>
        <button class="btn-add" @click="openAddModal">
          <i class="bi bi-plus-lg"></i> Thêm danh mục
        </button>
      </div>

      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên danh mục</th>
              <th>Slug</th>
              <th>Cấp cha (Parent ID)</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="isLoading">
              <td colspan="5" class="text-center py-4">Đang tải...</td>
            </tr>
            <tr v-else-if="categories.length === 0">
              <td colspan="5" class="text-center py-4">Trống.</td>
            </tr>
            <tr v-for="cat in categories" :key="cat.id" v-else>
              <td>{{ cat.id }}</td>
              <td class="fw-bold text-dark">{{ cat.name }}</td>
              <td class="text-muted">{{ cat.slug }}</td>
              <td>
                <span v-if="cat.parent_id" class="badge bg-secondary"
                  >ID: {{ cat.parent_id }}</span
                >
                <span v-else class="badge bg-success">Danh mục gốc</span>
              </td>
              <td>
                <div class="action-btns">
                  <button class="action-btn edit" @click="editCategory(cat)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button
                    class="action-btn delete"
                    @click="confirmDelete(cat.id)"
                  >
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="modal-overlay" v-if="showModal" @click.self="showModal = false">
      <div class="modal-box modal-sm">
        <div class="modal-header-custom">
          <h5>{{ editingId ? "Sửa danh mục" : "Thêm danh mục mới" }}</h5>
          <button class="modal-close" @click="showModal = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        <div class="modal-body-custom">
          <div class="form-group-custom mb-3">
            <label>Tên danh mục *</label>
            <input
              v-model="form.name"
              type="text"
              placeholder="Ví dụ: Laptop Gaming"
            />
          </div>
          <div class="form-group-custom">
            <label>Thuộc danh mục cha</label>
            <select v-model="form.parent_id">
              <option :value="null">-- Không có (Làm danh mục gốc) --</option>
              <option
                v-for="c in categories"
                :key="c.id"
                :value="c.id"
                :disabled="c.id === editingId"
              >
                {{ c.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="modal-footer-custom">
          <button class="btn-cancel" @click="showModal = false">Hủy</button>
          <button class="btn-save" @click="saveCategory">
            <i class="bi bi-check-lg"></i> Lưu
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/categories";
const categories = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const editingId = ref(null);

const form = ref({ name: "", parent_id: null });

const fetchCategories = async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(API_URL);
    categories.value = res.data;
  } catch (error) {
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

const openAddModal = () => {
  editingId.value = null;
  form.value = { name: "", parent_id: null };
  showModal.value = true;
};

const editCategory = (cat) => {
  editingId.value = cat.id;
  form.value = { name: cat.name, parent_id: cat.parent_id };
  showModal.value = true;
};

const saveCategory = async () => {
  if (!form.value.name.trim()) return alert("Tên không được trống!");

  // Chuẩn bị data gửi đi, đảm bảo field là parent_id
  const payload = {
    name: form.value.name,
    parent_id: form.value.parent_id,
  };

  try {
    if (editingId.value) {
      await axios.put(`${API_URL}/${editingId.value}`, payload);
    } else {
      await axios.post(API_URL, payload);
    }
    showModal.value = false;
    await fetchCategories();
    alert("Thành công!");
  } catch (err) {
    // Hiển thị lỗi chi tiết từ GlobalExceptionHandler
    const errorMsg = err.response?.data?.message || "Lỗi hệ thống";
    alert("Lỗi: " + errorMsg);
  }
};

const confirmDelete = async (id) => {
  if (confirm("Xóa (ẩn) danh mục này?")) {
    try {
      await axios.delete(`${API_URL}/${id}`);
      fetchCategories();
    } catch (err) {
      alert("Lỗi khi xóa!");
    }
  }
};

onMounted(() => fetchCategories());
</script>

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/admin/categories";
const categories = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const editingId = ref(null);

const currentPage = 1;
const pageSize = 10;

const form = ref({ name: "", parentId: null });

const fetchCategories = async () => {
  isLoading.value = true;
  try {
    const res = await axios.get(API_URL);
    categories.value = res.data;
    console.log("DỮ LIỆU THỰC TẾ TỪ BACKEND:", categories.value); // LUÔN LOG ĐỂ KIỂM TRA
  } catch (error) {
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};

const getParentName = (parentId) => {
  if (!parentId) return null;
  const parent = categories.value.find((c) => c.id === parentId);
  return parent ? parent.name : null;
};

const openAddModal = () => {
  editingId.value = null;
  form.value = { name: "", parentId: null };
  showModal.value = true;
};

const editCategory = (cat) => {
  editingId.value = cat.id;
  form.value = { name: cat.name, parentId: cat.parentId || cat.parent_id };
  showModal.value = true;
};

const saveCategory = async () => {
  if (!form.value.name.trim()) return alert("Tên không được trống!");

  // Payload chuẩn camelCase
  const payload = {
    name: form.value.name,
    parentId: form.value.parentId,
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

const confirmRestore = async (id) => {
  if (confirm("Khôi phục (hiển thị lại) danh mục này?")) {
    try {
      await axios.put(`${API_URL}/${id}/restore`);
      await fetchCategories();
    } catch (err) {
      alert("Lỗi khi khôi phục!");
    }
  }
};

onMounted(() => fetchCategories());
</script>

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
              <th>STT</th>
              <th>Tên danh mục</th>
              <th>Slug</th>
              <th>Cấp bậc (Parent)</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <template v-if="isLoading">
              <tr>
                <td colspan="6" class="text-center py-4">Đang tải...</td>
              </tr>
            </template>

            <template v-else-if="categories.length === 0">
              <tr>
                <td colspan="6" class="text-center py-4">Trống.</td>
              </tr>
            </template>

            <template v-else>
              <tr v-for="(cat, index) in categories" :key="cat.id">
                <td class="text-center fw-bold">
                  {{ (currentPage - 1) * pageSize + index + 1 }}
                </td>
                <td class="fw-bold text-dark">{{ cat.name }}</td>
                <td class="text-muted">{{ cat.slug }}</td>
                <td class="fw-bold">
                  <span
                    v-if="getParentName(cat.parentId || cat.parent_id)"
                    class="text-secondary"
                  >
                    Con của: {{ getParentName(cat.parentId || cat.parent_id) }}
                  </span>
                  <span v-else class="text-success">Danh mục gốc</span>
                </td>

                <td class="fw-bold">
                  <span v-if="cat.active || cat.isActive" class="text-success"
                    >Hiển thị</span
                  >
                  <span v-else class="text-muted">Đang ẩn</span>
                </td>
                <td>
                  <div class="action-btns">
                    <button class="action-btn edit" @click="editCategory(cat)">
                      <i class="bi bi-pencil"></i>
                    </button>

                    <button
                      v-if="cat.active"
                      class="action-btn delete"
                      @click="confirmDelete(cat.id)"
                      title="Ẩn danh mục"
                    >
                      <i class="bi bi-trash"></i>
                    </button>

                    <button
                      v-else
                      class="action-btn edit"
                      style="background-color: #198754; color: white"
                      @click="confirmRestore(cat.id)"
                      title="Khôi phục hiển thị"
                    >
                      <i class="bi bi-arrow-counterclockwise"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </template>
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

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

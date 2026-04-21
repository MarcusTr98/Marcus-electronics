<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api"; // Đã đổi sang dùng api.js để có Interceptor JWT

const API_URL = "/admin/categories"; // Rút gọn URL vì api.js đã cấu hình baseURL
const categories = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const editingId = ref(null);

const currentPage = 1;
const pageSize = 10;

// SỬA LỖI TẠI ĐÂY: Khởi tạo biến parentId chuẩn
const form = ref({ name: "", parentId: null });

const fetchCategories = async () => {
  isLoading.value = true;
  try {
    const res = await api.get(API_URL);
    categories.value = res.data;
  } catch (error) {
    console.error("Lỗi tải danh mục:", error);
    if (error.response?.status === 403) {
      alert("Bạn không có quyền truy cập trang này!");
    }
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
  // SỬA LỖI TẠI ĐÂY
  form.value = { name: "", parentId: null };
  showModal.value = true;
};

const editCategory = (cat) => {
  editingId.value = cat.id;
  // Đồng bộ hóa các trường hợp backend trả về
  form.value = { name: cat.name, parentId: cat.parentId || cat.parent_id };
  showModal.value = true;
};

const saveCategory = async () => {
  if (!form.value.name.trim()) return alert("Tên không được trống!");

  // Payload chuẩn camelCase
  const payload = {
    name: form.value.name,
    parentId: form.value.parentId, // Đã khớp biến
  };

  try {
    if (editingId.value) {
      await api.put(`${API_URL}/${editingId.value}`, payload);
    } else {
      await api.post(API_URL, payload);
    }
    showModal.value = false;
    await fetchCategories(); // Tải lại danh sách
    alert("Lưu danh mục thành công!");
  } catch (err) {
    const errorMsg =
      err.response?.data?.message || err.response?.data || "Lỗi hệ thống";
    alert("Lỗi: " + errorMsg);
  }
};

const confirmDelete = async (id) => {
  if (confirm("Bạn có chắc muốn ẩn danh mục này?")) {
    try {
      await api.delete(`${API_URL}/${id}`);
      fetchCategories();
    } catch (err) {
      alert(
        "Lỗi khi ẩn danh mục! Vui lòng kiểm tra xem danh mục này còn chứa sản phẩm không.",
      );
    }
  }
};

const confirmRestore = async (id) => {
  if (confirm("Khôi phục hiển thị danh mục này?")) {
    try {
      // Giả sử Backend có cấu hình API PUT /admin/categories/{id}/restore
      // Nếu API của bạn đang là PATCH hoặc dạng khác, hãy sửa đổi dòng dưới cho khớp
      await api.put(`${API_URL}/${id}/restore`);
      await fetchCategories();
    } catch (err) {
      alert("Lỗi khi khôi phục danh mục!");
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
          <p class="page-subtitle">Thêm, sửa, ẩn cấu trúc phân cấp danh mục</p>
        </div>
      </div>
    </div>

    <div class="card">
      <div
        class="card-header-custom d-flex justify-content-between align-items-center p-3"
      >
        <h6 class="card-title-text m-0">Cây danh mục</h6>
        <button class="btn-add" @click="openAddModal">
          <i class="bi bi-plus-lg"></i> Thêm danh mục
        </button>
      </div>

      <div class="table-responsive p-3">
        <table class="table table-hover align-middle border">
          <thead class="table-light">
            <tr>
              <th class="text-center">STT</th>
              <th>Tên danh mục</th>
              <th>Đường dẫn (Slug)</th>
              <th>Thuộc danh mục</th>
              <th class="text-center">Trạng thái</th>
              <th class="text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <template v-if="isLoading">
              <tr>
                <td colspan="6" class="text-center py-5">
                  <div class="spinner-border text-primary" role="status"></div>
                </td>
              </tr>
            </template>

            <template v-else-if="categories.length === 0">
              <tr>
                <td colspan="6" class="text-center py-5 text-muted">
                  Chưa có danh mục nào.
                </td>
              </tr>
            </template>

            <template v-else>
              <tr v-for="(cat, index) in categories" :key="cat.id">
                <td class="text-center fw-bold text-secondary">
                  {{ (currentPage - 1) * pageSize + index + 1 }}
                </td>
                <td class="fw-bold text-dark">{{ cat.name }}</td>
                <td>
                  <code class="text-muted bg-light px-2 py-1 rounded">{{
                    cat.slug
                  }}</code>
                </td>
                <td class="fw-semibold">
                  <span
                    v-if="cat.parentId || cat.parent_id"
                    class="badge bg-info text-dark border"
                  >
                    <i class="bi bi-diagram-2 me-1"></i>
                    {{ getParentName(cat.parentId || cat.parent_id) }}
                  </span>
                  <span v-else class="badge bg-secondary text-white">
                    <i class="bi bi-folder me-1"></i> Danh mục gốc
                  </span>
                </td>

                <td class="text-center">
                  <span
                    v-if="cat.active || cat.isActive"
                    class="badge bg-success"
                    >Đang hiển thị</span
                  >
                  <span v-else class="badge bg-danger">Đang ẩn</span>
                </td>

                <td class="text-center">
                  <div class="d-flex justify-content-center gap-2">
                    <button
                      class="btn btn-sm btn-outline-primary"
                      @click="editCategory(cat)"
                      title="Chỉnh sửa"
                    >
                      <i class="bi bi-pencil"></i>
                    </button>

                    <button
                      v-if="cat.active || cat.isActive"
                      class="btn btn-sm btn-outline-danger"
                      @click="confirmDelete(cat.id)"
                      title="Ẩn danh mục"
                    >
                      <i class="bi bi-trash"></i>
                    </button>

                    <button
                      v-else
                      class="btn btn-sm btn-success"
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
      <div class="modal-box" style="max-width: 400px">
        <div
          class="modal-header-custom d-flex justify-content-between align-items-center p-3 border-bottom"
        >
          <h5 class="m-0 fw-bold">
            {{ editingId ? "Chỉnh sửa danh mục" : "Thêm danh mục mới" }}
          </h5>
          <button class="btn-close" @click="showModal = false"></button>
        </div>

        <div class="modal-body-custom p-4">
          <div class="mb-4">
            <label class="form-label fw-bold small text-muted text-uppercase"
              >Tên danh mục <span class="text-danger">*</span></label
            >
            <input
              v-model="form.name"
              type="text"
              class="form-control"
              placeholder="Ví dụ: Laptop Gaming"
            />
          </div>

          <div>
            <label class="form-label fw-bold small text-muted text-uppercase"
              >Danh mục cha</label
            >
            <select v-model="form.parentId" class="form-select">
              <option :value="null">-- Trống (Làm danh mục gốc) --</option>
              <template v-for="c in categories" :key="c.id">
                <option v-if="c.id !== editingId" :value="c.id">
                  {{ c.name }}
                </option>
              </template>
            </select>
          </div>
        </div>

        <div
          class="modal-footer-custom p-3 border-top d-flex justify-content-end gap-2"
        >
          <button class="btn-cancel" @click="showModal = false">Hủy</button>
          <button class="btn-save" @click="saveCategory">
            <i class="bi bi-check-lg me-1"></i> Lưu lại
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../../assets/css/product-manage.css";
/* Overlay Modal chung */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-box {
  background: #fff;
  border-radius: 12px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
</style>

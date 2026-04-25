<template>
  <div class="page-content">
    <div class="page-heading mb-4">
      <h3 class="page-title-text">Quản lý Bài viết (Blogs)</h3>
      <p class="page-subtitle">
        Sáng tạo nội dung, đánh giá công nghệ và tin tức
      </p>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-lg-4 col-md-6">
        <div
          class="card mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #435ebe, #6c8cff);
            color: white;
            border: none;
            border-radius: 12px;
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
                Tổng bài viết
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ totalBlogs }}</h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-file-earmark-text"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-4 col-md-6">
        <div
          class="card mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #10b981, #34d399);
            color: white;
            border: none;
            border-radius: 12px;
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
                Đã xuất bản
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ publishedBlogs }}</h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-check-circle-fill"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-4 col-md-6">
        <div
          class="card mb-0 shadow-sm"
          style="
            background: linear-gradient(135deg, #f59e0b, #fbbf24);
            color: white;
            border: none;
            border-radius: 12px;
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
                Bản nháp (Drafts)
              </h6>
              <h3 class="mb-0 fw-bold text-white">{{ draftBlogs }}</h3>
            </div>
            <div class="fs-1 opacity-50">
              <i class="bi bi-pencil-square"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm rounded-4">
      <div class="card-header-custom flex-wrap gap-3">
        <h6 class="card-title-text mb-0 fs-5">Danh sách bài viết</h6>
        <div class="d-flex gap-2">
          <div class="input-group input-group-sm shadow-sm">
            <span class="input-group-text bg-light border-end-0 text-muted"
              ><i class="bi bi-search"></i
            ></span>
            <input
              type="text"
              class="form-control border-start-0 bg-light px-0"
              placeholder="Tìm kiếm tiêu đề..."
              v-model="searchQuery"
            />
          </div>
          <button
            class="btn btn-primary btn-sm transition-hover px-3 fw-bold d-flex align-items-center gap-1 shadow-sm"
            @click="openModal('add')"
          >
            <i class="bi bi-plus-lg"></i> Thêm bài viết
          </button>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th class="ps-4">Ảnh</th>
              <th>Tiêu đề bài viết</th>
              <th>Chuyên mục</th>
              <th>Tác giả</th>
              <th>Ngày tạo</th>
              <th>Trạng thái</th>
              <th class="text-center pe-4">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredBlogs.length === 0">
              <td colspan="7" class="text-center text-muted py-5 fs-5">
                Không tìm thấy bài viết nào.
              </td>
            </tr>
            <tr v-for="blog in filteredBlogs" :key="blog.id">
              <td class="ps-4">
                <img
                  :src="blog.image"
                  alt="thumbnail"
                  class="rounded-3 border object-fit-cover"
                  style="width: 50px; height: 50px"
                />
              </td>
              <td>
                <span class="text-dark fw-bold" style="font-size: 0.95rem">{{
                  blog.title
                }}</span>
              </td>
              <td>
                <span
                  class="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1 rounded-2"
                  >{{ blog.category }}</span
                >
              </td>
              <td class="fw-medium text-dark">{{ blog.author }}</td>
              <td class="text-muted small">{{ blog.date }}</td>
              <td>
                <span
                  class="badge rounded-pill px-3 py-2"
                  :class="
                    blog.status === 'Published'
                      ? 'bg-success'
                      : 'bg-warning text-dark'
                  "
                >
                  {{ blog.status === "Published" ? "Đã xuất bản" : "Bản nháp" }}
                </span>
              </td>
              <td class="text-center pe-4">
                <div class="action-btns justify-content-center">
                  <button
                    class="action-btn edit"
                    @click="openModal('edit', blog)"
                    title="Sửa"
                  >
                    <i class="bi bi-pencil-fill"></i>
                  </button>
                  <button
                    class="action-btn delete"
                    @click="deleteBlog(blog.id)"
                    title="Xóa"
                  >
                    <i class="bi bi-trash-fill"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div
        class="card-footer bg-white border-top p-3 d-flex justify-content-end"
      >
        <ul class="pagination pagination-sm mb-0 shadow-sm">
          <li class="page-item disabled">
            <a class="page-link fw-bold text-secondary" href="#">Trước</a>
          </li>
          <li class="page-item active">
            <a class="page-link fw-bold" href="#">1</a>
          </li>
          <li class="page-item"><a class="page-link fw-bold" href="#">2</a></li>
          <li class="page-item">
            <a class="page-link fw-bold text-secondary" href="#">Sau</a>
          </li>
        </ul>
      </div>
    </div>

    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box modal-dialog-centered" style="max-width: 700px">
        <div class="modal-header-custom">
          <h5 class="card-title-text mb-0">
            {{
              modalMode === "add" ? "Thêm bài viết mới" : "Chỉnh sửa bài viết"
            }}
          </h5>
          <button class="modal-close shadow-none" @click="closeModal">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="modal-body-custom p-4">
          <div class="form-group mb-3">
            <label class="form-label text-muted small fw-bold text-uppercase"
              >Tiêu đề bài viết <span class="text-danger">*</span></label
            >
            <input
              type="text"
              class="form-control"
              v-model="formData.title"
              placeholder="VD: Đánh giá chip Apple M4..."
            />
          </div>

          <div class="row mb-3 g-3">
            <div class="col-md-6">
              <label class="form-label text-muted small fw-bold text-uppercase"
                >Chuyên mục</label
              >
              <select class="form-select" v-model="formData.category">
                <option value="Laptop">Laptop</option>
                <option value="Điện thoại">Điện thoại</option>
                <option value="Tablet">Tablet</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label text-muted small fw-bold text-uppercase"
                >Trạng thái</label
              >
              <select class="form-select" v-model="formData.status">
                <option value="Published">Xuất bản ngay</option>
                <option value="Draft">Lưu bản nháp</option>
              </select>
            </div>
          </div>

          <div class="form-group mb-3">
            <label class="form-label text-muted small fw-bold text-uppercase"
              >URL Ảnh Thumbnail</label
            >
            <input
              type="text"
              class="form-control"
              v-model="formData.image"
              placeholder="https://..."
            />
          </div>

          <div class="form-group mb-2">
            <label class="form-label text-muted small fw-bold text-uppercase"
              >Nội dung tóm tắt</label
            >
            <textarea
              class="form-control"
              rows="4"
              placeholder="Nhập tóm tắt bài viết..."
            ></textarea>
          </div>
        </div>

        <div
          class="modal-footer-custom bg-light border-top p-3 d-flex justify-content-end gap-2 rounded-bottom-3"
        >
          <button class="btn btn-light border fw-bold px-4" @click="closeModal">
            Hủy
          </button>
          <button
            class="btn btn-primary transition-hover fw-bold px-4 shadow-sm"
            @click="saveBlog"
          >
            <i class="bi bi-check-lg me-1"></i>
            {{ modalMode === "add" ? "Lưu bài viết" : "Lưu thay đổi" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const blogs = ref([
  {
    id: 1,
    title: "Top 5 Laptops cho Lập trình viên Java (Spring Boot) năm 2026",
    category: "Laptop",
    author: "Marcus Tran",
    status: "Published",
    date: "2026-04-15",
    image: "https://via.placeholder.com/150",
  },
  {
    id: 2,
    title: "Đánh giá chi tiết iPhone 17 Pro Max sau 6 tháng",
    category: "Điện thoại",
    author: "Admin",
    status: "Published",
    date: "2026-04-10",
    image: "https://via.placeholder.com/150",
  },
  {
    id: 3,
    title: "iPad Pro M4 có thực sự thay thế được Laptop code dạo?",
    category: "Tablet",
    author: "Marcus Tran",
    status: "Draft",
    date: "2026-04-18",
    image: "https://via.placeholder.com/150",
  },
  {
    id: 4,
    title: "So sánh Macbook Pro M3 và Dell XPS 15",
    category: "Laptop",
    author: "Tech Team",
    status: "Published",
    date: "2026-04-01",
    image: "https://via.placeholder.com/150",
  },
]);

const totalBlogs = computed(() => blogs.value.length);
const publishedBlogs = computed(
  () => blogs.value.filter((b) => b.status === "Published").length,
);
const draftBlogs = computed(
  () => blogs.value.filter((b) => b.status === "Draft").length,
);

const searchQuery = ref("");
const filteredBlogs = computed(() => {
  if (!searchQuery.value) return blogs.value;
  return blogs.value.filter((b) =>
    b.title.toLowerCase().includes(searchQuery.value.toLowerCase()),
  );
});

const isModalOpen = ref(false);
const modalMode = ref("add");
const formData = ref({
  id: null,
  title: "",
  category: "Laptop",
  status: "Draft",
  image: "",
});

const openModal = (mode, blog = null) => {
  modalMode.value = mode;
  if (mode === "edit" && blog) {
    formData.value = { ...blog };
  } else {
    formData.value = {
      id: null,
      title: "",
      category: "Laptop",
      status: "Draft",
      image: "",
    };
  }
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const saveBlog = () => {
  if (modalMode.value === "add") {
    const newId =
      blogs.value.length > 0
        ? Math.max(...blogs.value.map((b) => b.id)) + 1
        : 1;
    blogs.value.unshift({
      ...formData.value,
      id: newId,
      author: "Marcus Tran",
      date: new Date().toISOString().split("T")[0],
    });
  } else {
    const index = blogs.value.findIndex((b) => b.id === formData.value.id);
    if (index !== -1) blogs.value[index] = { ...formData.value };
  }
  closeModal();
};

const deleteBlog = (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa bài viết này?")) {
    blogs.value = blogs.value.filter((b) => b.id !== id);
  }
};
</script>

<style scoped></style>

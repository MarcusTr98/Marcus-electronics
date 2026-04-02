<script setup>
import { ref, watch } from "vue";
import api from "../../utils/api";

const props = defineProps({
  show: Boolean,
  editingProduct: Object,
  categories: Array,
});

const emit = defineEmits(["close", "save"]);

const form = ref({
  name: "",
  slug: "",
  categoryId: null,
  basePrice: 0,
  thumbnailUrl: "",
  weightG: 0,
  lengthCm: 0,
  widthCm: 0,
  heightCm: 0,
  description: "",
  active: true,
});

const fileInput = ref(null);

// đẩy dữ liệu vào form khi mở model
watch(
  () => props.show,
  (newVal) => {
    if (newVal && props.editingProduct) {
      form.value = {
        ...props.editingProduct,
        weightG: props.editingProduct.weightG || 0,
        lengthCm: props.editingProduct.lengthCm || 0,
        widthCm: props.editingProduct.widthCm || 0,
        heightCm: props.editingProduct.heightCm || 0,
        description: props.editingProduct.description || "",
      };
      console.log("Dữ liệu nhận được:", form.value);
    } else if (newVal) {
      resetForm();
    }
  },
);

const resetForm = () => {
  form.value = {
    name: "",
    slug: "",
    categoryId: props.categories[0]?.id || null,
    basePrice: 0,
    thumbnailUrl: "",
    weightG: 0,
    lengthCm: 0,
    widthCm: 0,
    heightCm: 0,
    description: "",
    active: true,
  };
};

const generateSlug = () => {
  form.value.slug = form.value.name
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
};

// Logic Upload ảnh
const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await api.post("/admin/files/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    form.value.thumbnailUrl = "http://localhost:8080" + res.data;
  } catch (err) {
    alert("Lỗi upload ảnh!");
  }
};

const submit = () => {
  if (!form.value.name) return alert("Vui lòng nhập tên!");
  emit("save", form.value);
};
</script>

<template>
  <div class="modal-overlay" v-if="show" @click.self="emit('close')">
    <div class="modal-box modal-lg">
      <div class="modal-header-custom">
        <h5>{{ editingProduct ? "Cập nhật" : "Thêm mới" }} Sản phẩm</h5>
        <button class="modal-close" @click="emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body-custom">
        <div class="row">
          <div class="col-md-4 text-center border-end">
            <label class="d-block mb-2 fw-bold">Ảnh đại diện</label>
            <div class="img-preview mb-2 mx-auto">
              <img
                :src="form.thumbnailUrl || 'https://via.placeholder.com/150'"
                class="img-thumbnail"
                style="height: 150px; object-fit: cover"
              />
            </div>
            <input
              type="file"
              ref="fileInput"
              class="d-none"
              @change="handleFileUpload"
            />
            <button
              class="btn btn-sm btn-outline-primary"
              @click="fileInput.click()"
            >
              Chọn ảnh
            </button>
          </div>

          <div class="col-md-8">
            <div class="row g-3">
              <div class="col-12">
                <label>Tên sản phẩm *</label>
                <input
                  v-model="form.name"
                  @input="generateSlug"
                  type="text"
                  class="form-control"
                />
              </div>
              <div class="col-6">
                <label>Danh mục</label>
                <select v-model="form.categoryId" class="form-select">
                  <option v-for="c in categories" :key="c.id" :value="c.id">
                    {{ c.name }}
                  </option>
                </select>
              </div>
              <div class="col-6">
                <label>Giá cơ bản</label>
                <input
                  v-model="form.basePrice"
                  type="number"
                  class="form-control"
                />
              </div>
              <div class="col-12">
                <label>Slug</label>
                <input
                  v-model="form.slug"
                  type="text"
                  class="form-control"
                  readonly
                />
              </div>
            </div>
          </div>
        </div>

        <hr />
        <div class="row g-3">
          <div class="col-3">
            <label>Nặng (g)</label
            ><input v-model="form.weightG" type="number" class="form-control" />
          </div>
          <div class="col-3">
            <label>Dài (cm)</label
            ><input
              v-model="form.lengthCm"
              type="number"
              class="form-control"
            />
          </div>
          <div class="col-3">
            <label>Rộng (cm)</label
            ><input v-model="form.widthCm" type="number" class="form-control" />
          </div>
          <div class="col-3">
            <label>Cao (cm)</label
            ><input
              v-model="form.heightCm"
              type="number"
              class="form-control"
            />
          </div>
          <div class="col-12">
            <label>Mô tả</label>
            <textarea
              v-model="form.description"
              class="form-control"
              rows="3"
            ></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer-custom">
        <button class="btn-cancel" @click="emit('close')">Hủy</button>
        <button class="btn-save" @click="submit">Lưu lại</button>
      </div>
    </div>
  </div>
</template>
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

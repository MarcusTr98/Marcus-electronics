<template>
  <div class="page-content">
    <div class="page-heading">
      <div class="page-title-row">
        <div>
          <h3 class="page-title-text">Quản lý sản phẩm gốc</h3>
          <p class="page-subtitle">
            Quản lý thông tin chung và kích thước ship
          </p>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header-custom">
        <h6 class="card-title-text">Danh sách sản phẩm</h6>
        <button class="btn-add" @click="openAddModal">
          <i class="bi bi-plus-lg"></i> Thêm sản phẩm
        </button>
      </div>

      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Danh mục</th>
              <th>Giá cơ bản</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="isLoading">
              <td colspan="5" class="text-center py-4">Đang tải dữ liệu...</td>
            </tr>
            <tr v-else-if="allProducts.length === 0">
              <td colspan="5" class="text-center py-4">
                Chưa có sản phẩm nào.
              </td>
            </tr>
            <tr v-for="product in allProducts" :key="product.id" v-else>
              <td>
                <div class="product-cell">
                  <div class="product-thumb">
                    <img
                      :src="
                        product.thumbnailUrl || 'https://via.placeholder.com/40'
                      "
                      alt="thumb"
                      style="
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                        border-radius: 8px;
                      "
                    />
                  </div>
                  <div>
                    <div class="product-name">{{ product.name }}</div>
                    <div class="product-sku">Slug: {{ product.slug }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="category-tag">{{ product.categoryName }}</span>
              </td>
              <td class="price-cell">{{ formatCurrency(product.price) }}</td>
              <td>
                <span class="status-pill active">Hoạt động</span>
              </td>
              <td>
                <div class="action-btns">
                  <button
                    class="action-btn edit"
                    @click="editProduct(product)"
                    title="Sửa thông tin gốc"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button
                    class="action-btn view"
                    title="Quản lý biến thể (SKU)"
                  >
                    <i class="bi bi-box-seam"></i>
                  </button>
                  <button
                    class="action-btn delete"
                    @click="confirmDelete(product.id)"
                    title="Xóa"
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
      <div class="modal-box">
        <div class="modal-header-custom">
          <h5>
            {{ editingId ? "Sửa sản phẩm gốc" : "Thêm sản phẩm gốc mới" }}
          </h5>
          <button class="modal-close" @click="showModal = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="modal-body-custom">
          <div class="form-row-2">
            <div class="form-group-custom">
              <label>Tên sản phẩm *</label>
              <input
                v-model="form.name"
                type="text"
                placeholder="Ví dụ: iPhone 15 Pro Max"
              />
            </div>
            <div class="form-group-custom">
              <label>Danh mục *</label>
              <select v-model="form.category_id">
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-row-2">
            <div class="form-group-custom">
              <label>Giá cơ bản (VNĐ) *</label>
              <input v-model="form.base_price" type="number" placeholder="0" />
            </div>
            <div class="form-group-custom">
              <label>URL Ảnh đại diện</label>
              <input
                v-model="form.thumbnail_url"
                type="text"
                placeholder="https://..."
              />
            </div>
          </div>

          <div
            class="mt-3 mb-2"
            style="
              font-size: 0.8rem;
              font-weight: 700;
              color: #1e293b;
              text-transform: uppercase;
            "
          >
            Thông số vận chuyển (Ship)
          </div>
          <div class="form-row-2">
            <div class="form-group-custom">
              <label>Trọng lượng (Gram)</label>
              <input v-model="form.weightG" type="number" placeholder="0" />
            </div>
            <div class="form-group-custom">
              <label>Chiều dài (cm)</label>
              <input v-model="form.lengthCm" type="number" placeholder="0" />
            </div>
          </div>
          <div class="form-row-2">
            <div class="form-group-custom">
              <label>Chiều rộng (cm)</label>
              <input v-model="form.widthCm" type="number" placeholder="0" />
            </div>
            <div class="form-group-custom">
              <label>Chiều cao (cm)</label>
              <input v-model="form.heightCm" type="number" placeholder="0" />
            </div>
          </div>

          <div class="form-group-custom mt-2">
            <label>Mô tả chi tiết</label>
            <textarea
              v-model="form.description"
              rows="4"
              placeholder="Nhập mô tả sản phẩm..."
            ></textarea>
          </div>
        </div>

        <div class="modal-footer-custom">
          <button class="btn-cancel" @click="showModal = false">Hủy</button>
          <button class="btn-save" @click="saveProduct">
            <i class="bi bi-check-lg"></i> Lưu sản phẩm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const API_PRODUCT_URL = "http://localhost:8080/api/v1/products";
const API_CATEGORY_URL = "http://localhost:8080/api/v1/categories";

const allProducts = ref([]);
const categories = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const editingId = ref(null);

const form = ref({
  name: "",
  description: "",
  category_id: null, // Mặc định là null
  thumbnail_url: "",
  base_price: 0,
  weightG: 0,
  lengthCm: 0,
  widthCm: 0,
  heightCm: 0,
});

// Lấy danh sách Sản phẩm
const fetchProducts = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get(API_PRODUCT_URL);
    allProducts.value = response.data;
  } catch (error) {
    console.error("Lỗi khi tải sản phẩm:", error);
  } finally {
    isLoading.value = false;
  }
};

// Lấy danh sách Danh mục để đổ vào thẻ <select>
const fetchCategories = async () => {
  try {
    const response = await axios.get(API_CATEGORY_URL);
    categories.value = response.data;

    // Tự động chọn danh mục đầu tiên nếu form đang trống
    if (categories.value.length > 0 && !form.value.category_id) {
      form.value.category_id = categories.value[0].id;
    }
  } catch (error) {
    console.error("Lỗi khi tải danh mục:", error);
  }
};

const openAddModal = () => {
  editingId.value = null;
  form.value = {
    name: "",
    description: "",
    category_id: categories.value.length > 0 ? categories.value[0].id : null,
    thumbnail_url: "",
    base_price: 0,
    weightG: 0,
    lengthCm: 0,
    widthCm: 0,
    heightCm: 0,
  };
  showModal.value = true;
};

const editProduct = async (product) => {
  editingId.value = product.id;
  try {
    const response = await axios.get(`${API_PRODUCT_URL}/${product.id}`);
    const pd = response.data;

    form.value = {
      name: pd.name,
      description: pd.description,
      category_id: pd.category ? pd.category.id : null,
      thumbnail_url: pd.thumbnailUrl || "",
      base_price: pd.basePrice || 0,
      weightG: pd.weightG || 0,
      lengthCm: pd.lengthCm || 0,
      widthCm: pd.widthCm || 0,
      heightCm: pd.heightCm || 0,
    };
    showModal.value = true;
  } catch (error) {
    console.error("Lỗi lấy chi tiết:", error);
  }
};

const saveProduct = async () => {
  if (!form.value.name.trim()) {
    alert("Tên sản phẩm không được để trống!");
    return;
  }
  if (!form.value.category_id) {
    alert("Vui lòng chọn danh mục!");
    return;
  }

  try {
    if (editingId.value) {
      await axios.put(`${API_PRODUCT_URL}/${editingId.value}`, form.value);
    } else {
      await axios.post(API_PRODUCT_URL, form.value);
    }
    showModal.value = false;
    await fetchProducts();
  } catch (error) {
    console.error("Lỗi khi lưu:", error);
    alert("Lỗi lưu dữ liệu. Hãy ấn F12 xem Network tab.");
  }
};

const confirmDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa (ẩn) sản phẩm này?")) {
    try {
      await axios.delete(`${API_PRODUCT_URL}/${id}`);
      await fetchProducts();
    } catch (error) {
      console.error("Lỗi khi xóa:", error);
    }
  }
};

const formatCurrency = (val) => {
  if (!val) return "0 ₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(val);
};

// Gọi CẢ HAI API khi load trang
onMounted(() => {
  fetchCategories();
  fetchProducts();
});
</script>

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

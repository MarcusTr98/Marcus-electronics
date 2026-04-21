<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api";

// ==================== STATE ====================
const products = ref([]);
const selectedProductId = ref("");
const options = ref([{ name: "", rawValues: "" }]);
const generatedSkus = ref([]);
const bulkPrice = ref(0);
const bulkStock = ref(0);

// ==================== API CALLS ====================
const fetchProducts = async () => {
  try {
    const res = await api.get("/admin/products");
    products.value = res.data;
  } catch (err) {
    console.error("Lỗi tải sản phẩm", err);
  }
};

onMounted(fetchProducts);

// ==================== OPTIONS ====================
const addOption = () => {
  options.value.push({ name: "", rawValues: "" });
};

const removeOption = (index) => {
  options.value.splice(index, 1);
};

// ==================== UTILS ====================

// 1. Hàm tạo mã tiền tố Sản phẩm (VD: Samsung Galaxy S24 Ultra 5G -> SGS24U5)
const getInitials = (str) => {
  // Loại bỏ dấu tiếng Việt và ký tự đặc biệt (giữ lại chữ và số)
  const cleanStr = str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[đĐ]/g, "d")
    .replace(/[^a-zA-Z0-9 ]/g, "")
    .toUpperCase();

  const words = cleanStr.split(" ").filter((word) => word.length > 0);

  let prefix = "";
  words.forEach((word) => {
    // Nếu từ là một con số hoặc chứa con số (VD: 24, S24, 5G, M3), giữ nguyên cả cụm đó (tối đa 4 ký tự)
    if (/\d/.test(word)) {
      prefix += word.substring(0, 4);
    } else {
      // Nếu chỉ là chữ (VD: Samsung, Ultra), lấy chữ cái đầu
      prefix += word[0];
    }
  });

  return prefix;
};

// 2. Hàm tạo mã Hậu tố Biến thể (VD: 256GB -> 256G, Titan Xanh -> TX)
const generateValueCode = (val) => {
  const cleanStr = val
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[đĐ]/g, "d")
    .replace(/[^a-zA-Z0-9 ]/g, "")
    .toUpperCase();

  const words = cleanStr.split(" ").filter((w) => w.length > 0);

  // Nếu giá trị chỉ có 1 từ và chứa số (VD: 256GB), lấy nguyên chữ/số đó (tối đa 5 ký tự)
  if (words.length === 1 && /\d/.test(words[0])) {
    return words[0].substring(0, 5);
  }

  // Nếu có nhiều từ (VD: Xanh Ngọc, Titan Đen), lấy chữ cái đầu của mỗi từ
  return words.map((w) => w[0]).join("");
};

const cartesian = (args) =>
  args.reduce(
    (a, b) =>
      a
        .map((x) => b.map((y) => x.concat([y])))
        .reduce((c, d) => c.concat(d), []),
    [[]],
  );

const getValidOptions = () =>
  options.value
    .filter((o) => o.name.trim() !== "" && o.rawValues.trim() !== "")
    .map((o) => ({
      name: o.name,
      values: o.rawValues
        .split(",")
        .map((v) => v.trim())
        .filter((v) => v !== ""),
    }));

// ==================== GENERATE ====================
const generateVariants = () => {
  const validOptions = getValidOptions();
  if (validOptions.length === 0)
    return alert("Vui lòng nhập ít nhất 1 thuộc tính hợp lệ!");

  const product = products.value.find((p) => p.id === selectedProductId.value);
  const baseCode = product ? getInitials(product.name) : "PRD";
  const combinations = cartesian(validOptions.map((opt) => opt.values));

  generatedSkus.value = combinations.map((combo) => ({
    variantName: combo.join(" - "),
    skuCode: `${baseCode}-${combo.map(generateValueCode).join("-")}`,
    price: bulkPrice.value || 0,
    stock: bulkStock.value || 0,
    comboValues: combo,
  }));
};

// ==================== BULK ====================
const applyBulkSettings = () => {
  if (generatedSkus.value.length === 0)
    return alert("Chưa có biến thể nào được tạo!");

  let appliedCount = 0;
  generatedSkus.value.forEach((sku) => {
    if (bulkPrice.value > 0) {
      sku.price = bulkPrice.value;
      appliedCount++;
    }
    if (bulkStock.value >= 0 && bulkStock.value !== "") {
      sku.stock = bulkStock.value;
      appliedCount++;
    }
  });

  if (appliedCount > 0) alert("Đã cập nhật đồng loạt thành công!");
};

// ==================== SAVE ====================
const saveAllSkus = async () => {
  if (!selectedProductId.value) return alert("Chưa chọn sản phẩm gốc!");
  if (generatedSkus.value.length === 0)
    return alert("Chưa có biến thể nào được tạo!");

  try {
    // Bước 1: Lưu Options lên DB & lập từ điển ID
    const globalValueIdMap = {};
    for (const opt of getValidOptions()) {
      const res = await api.post(
        `/admin/products/${selectedProductId.value}/options`,
        { name: opt.name, values: opt.values },
      );
      Object.assign(globalValueIdMap, res.data.valueIds);
    }

    // Bước 2: Map ID và đẩy đồng loạt SKU lên server
    await Promise.all(
      generatedSkus.value.map((sku) =>
        api.post(`/admin/products/${selectedProductId.value}/skus`, {
          skuCode: sku.skuCode,
          price: sku.price,
          stock: sku.stock,
          imageUrl: "",
          optionValueIds: sku.comboValues.map((val) => globalValueIdMap[val]),
        }),
      ),
    );

    alert("Tuyệt vời! Đã lưu toàn bộ ma trận SKU vào Database thành công!");
    generatedSkus.value = [];
    options.value = [{ name: "", rawValues: "" }];
  } catch (error) {
    console.error("Lỗi khi lưu dữ liệu:", error);
    alert(
      "Hệ thống từ chối lưu: " +
        (error.response?.data?.message || error.message),
    );
  }
};
</script>

<template>
  <div class="page-content">
    <div class="page-heading">
      <h3 class="page-title-text">Quản lý Biến thể Sản phẩm (SKUs)</h3>
    </div>

    <div class="row">
      <!-- CỘT TRÁI: Thiết lập thuộc tính -->
      <div class="col-md-4">
        <div class="card">
          <div class="card-header">
            <h6 class="m-0 font-weight-bold text-primary">
              1. Thiết lập Thuộc tính
            </h6>
          </div>
          <div class="card-body">
            <div class="form-group mb-3">
              <label class="fw-bold">Chọn Sản Phẩm Gốc:</label>
              <select v-model="selectedProductId" class="form-select">
                <option value="" disabled>-- Chọn sản phẩm --</option>
                <option v-for="p in products" :key="p.id" :value="p.id">
                  {{ p.name }}
                </option>
              </select>
            </div>

            <hr />
            <p class="text-muted small">
              Thêm các thuộc tính như Màu sắc, Bộ nhớ...
            </p>

            <div
              v-for="(opt, index) in options"
              :key="index"
              class="border p-3 rounded mb-3 bg-light"
            >
              <div class="d-flex justify-content-between mb-2">
                <label class="fw-bold">Thuộc tính {{ index + 1 }}</label>
                <button
                  class="btn btn-sm btn-danger"
                  @click="removeOption(index)"
                >
                  <i class="bi bi-trash"></i>
                </button>
              </div>
              <input
                v-model="opt.name"
                placeholder="Tên (VD: Màu sắc)"
                class="form-control mb-2"
              />
              <input
                v-model="opt.rawValues"
                placeholder="Giá trị (VD: Đỏ, Xanh, Đen)"
                class="form-control"
              />
            </div>

            <button
              class="btn btn-outline-primary w-100 mb-2"
              @click="addOption"
            >
              <i class="bi bi-plus"></i> Thêm Thuộc Tính
            </button>
            <button
              class="btn btn-success w-100"
              :disabled="!selectedProductId || options.length === 0"
              @click="generateVariants"
            >
              <i class="bi bi-magic"></i> Tạo Ma Trận SKU
            </button>
          </div>
        </div>
      </div>

      <!-- CỘT PHẢI: Danh sách SKUs -->
      <div class="col-md-8">
        <div class="card">
          <div
            class="card-header d-flex justify-content-between align-items-center"
          >
            <h6 class="m-0 font-weight-bold text-primary">
              2. Danh sách SKUs Sinh Ra
            </h6>
            <button
              v-if="generatedSkus.length > 0"
              class="btn btn-primary btn-sm"
              @click="saveAllSkus"
            >
              Lưu toàn bộ SKU
            </button>
          </div>
          <div class="card-body">
            <div
              v-if="generatedSkus.length === 0"
              class="text-center text-muted p-5"
            >
              Vui lòng thiết lập thuộc tính và bấm "Tạo Ma Trận SKU"
            </div>

            <template v-if="generatedSkus.length > 0">
              <!-- Bulk settings -->
              <div class="card-body bg-light border-bottom mb-3 rounded">
                <div class="row align-items-end g-2">
                  <div class="col-md-4">
                    <label class="form-label fw-bold text-sm"
                      >Giá thấp nhất (₫)</label
                    >
                    <input
                      v-model="bulkPrice"
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="VD: 25000000"
                    />
                  </div>
                  <div class="col-md-4">
                    <label class="form-label fw-bold text-sm"
                      >Tồn kho chung</label
                    >
                    <input
                      v-model="bulkStock"
                      type="number"
                      class="form-control form-control-sm"
                      placeholder="VD: 100"
                    />
                  </div>
                  <div class="col-md-4">
                    <button
                      class="btn btn-sm btn-warning w-100 fw-bold"
                      @click="applyBulkSettings"
                    >
                      <i class="bi bi-lightning-charge-fill"></i> Áp dụng hàng
                      loạt
                    </button>
                  </div>
                </div>
                <small class="text-muted mt-2 d-block">
                  * Chức năng này sẽ ghi đè giá và tồn kho cho toàn bộ
                  {{ generatedSkus.length }} biến thể bên dưới.
                </small>
              </div>

              <!-- SKU table -->
              <div class="table-responsive">
                <table class="table table-bordered align-middle">
                  <thead class="table-light">
                    <tr>
                      <th>Biến thể</th>
                      <th>Mã SKU</th>
                      <th>Giá bán (₫)</th>
                      <th>Tồn kho</th>
                      <th>Xóa</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(sku, index) in generatedSkus" :key="index">
                      <td class="fw-bold text-primary">
                        {{ sku.variantName }}
                      </td>
                      <td>
                        <input
                          v-model="sku.skuCode"
                          class="form-control form-control-sm"
                          placeholder="VD: IPH-RED-128"
                        />
                      </td>
                      <td>
                        <input
                          v-model="sku.price"
                          type="number"
                          class="form-control form-control-sm"
                        />
                      </td>
                      <td>
                        <input
                          v-model="sku.stock"
                          type="number"
                          class="form-control form-control-sm"
                        />
                      </td>
                      <td class="text-center">
                        <button
                          class="btn btn-sm btn-outline-danger"
                          @click="generatedSkus.splice(index, 1)"
                        >
                          <i class="bi bi-x"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../../assets/css/product-manage.css";
</style>

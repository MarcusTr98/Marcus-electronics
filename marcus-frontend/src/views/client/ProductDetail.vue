<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import api from "../../utils/api";

// ─── State ───────────────────────────────────────────────
const route = useRoute();
const product = ref(null);
const loading = ref(true);
const quantity = ref(1);
const selectedOptions = ref({});

// ─── Data Fetching ────────────────────────────────────────
const getProductDetail = async () => {
  loading.value = true;
  try {
    const response = await api.get(`/products/slug/${route.params.slug}`);
    product.value = response.data;

    if (product.value.skus?.length > 0) {
      const firstSkuOptions = product.value.skus[0].options;
      for (const key in firstSkuOptions) {
        selectedOptions.value[key] = firstSkuOptions[key];
      }
    }
  } catch (error) {
    console.error("Lỗi tải chi tiết:", error);
  } finally {
    loading.value = false;
  }
};

// ─── Computed ─────────────────────────────────────────────
const hasVariants = computed(() => product.value?.skus?.length > 0);

const availableOptions = computed(() => {
  if (!hasVariants.value) return {};
  const map = {};
  product.value.skus.forEach((sku) => {
    Object.entries(sku.options).forEach(([key, val]) => {
      if (!map[key]) map[key] = new Set();
      map[key].add(val);
    });
  });
  Object.keys(map).forEach((key) => (map[key] = Array.from(map[key])));
  return map;
});

const currentSku = computed(() => {
  if (!hasVariants.value) return null;
  return product.value.skus.find((sku) =>
    Object.keys(availableOptions.value).every(
      (k) => sku.options[k] === selectedOptions.value[k],
    ),
  );
});

const displayImage = computed(() => {
  let path = currentSku.value?.imageUrl || product.value?.thumbnailUrl;
  if (!path) return "https://via.placeholder.com/500";
  if (!path.startsWith("http") && !path.startsWith("/img"))
    return `/img/${path}`;
  return path;
});

const displayPrice = computed(() =>
  hasVariants.value && currentSku.value
    ? currentSku.value.price
    : product.value?.basePrice || 0,
);

const displayStock = computed(() => currentSku.value?.stock ?? 10);

// ─── Methods ──────────────────────────────────────────────
const selectOption = (name, val) => {
  selectedOptions.value[name] = val;
  quantity.value = 1;
};

const decreaseQty = () => {
  if (quantity.value > 1) quantity.value--;
};
const increaseQty = () => {
  if (quantity.value < displayStock.value) quantity.value++;
};

const addToCart = () => {
  if (hasVariants.value && !currentSku.value)
    return alert("Vui lòng chọn đủ phân loại!");

  const targetId = hasVariants.value
    ? currentSku.value.id
    : `PROD_${product.value.id}`;

  let cart = JSON.parse(localStorage.getItem("marcus_cart")) || [];
  const existing = cart.find((item) => item.skuId === targetId);

  if (existing) {
    existing.quantity += quantity.value;
  } else {
    cart.push({
      skuId: targetId,
      name: product.value.name,
      variantDetail: hasVariants.value
        ? Object.values(selectedOptions.value).join(" - ")
        : "Tiêu chuẩn",
      price: displayPrice.value,
      thumbnail: displayImage.value,
      quantity: quantity.value,
    });
  }

  localStorage.setItem("marcus_cart", JSON.stringify(cart));
  window.dispatchEvent(new Event("cart-updated"));
  alert("Đã thêm vào giỏ hàng!");
};

onMounted(getProductDetail);
</script>

<template>
  <div class="container py-4 mt-5">
    <div v-if="loading" class="text-center py-5">
      <div
        class="spinner-border text-primary shadow-sm"
        style="width: 2.5rem; height: 2.5rem"
      ></div>
    </div>

    <div v-else-if="product" class="row justify-content-center g-4">
      <div class="col-lg-5 col-md-12">
        <div class="sticky-top" style="top: 100px">
          <div
            class="product-image-card border rounded-4 p-4 bg-white shadow-sm d-flex align-items-center justify-content-center"
          >
            <img
              :src="displayImage"
              class="img-fluid transition-all"
              style="max-height: 400px; object-fit: contain"
              :alt="product.name"
            />
          </div>
        </div>
      </div>

      <div class="col-lg-6 col-md-12 ps-lg-4">
        <div class="product-info-wrap d-flex flex-column gap-3">
          <div class="category-tag">
            <span
              class="badge bg-light text-primary border border-primary-subtle px-3 py-2 rounded-pill text-uppercase"
              style="font-size: 0.7rem; letter-spacing: 1px"
            >
              {{ product.categoryName }}
            </span>
          </div>

          <h1 class="fw-bold fs-3 text-dark mb-0">{{ product.name }}</h1>

          <div class="price-section d-flex align-items-center gap-3">
            <span class="fw-bold fs-3 text-danger">
              {{ Number(displayPrice).toLocaleString("vi-VN") }} ₫
            </span>
            <span
              v-if="displayStock > 0"
              class="badge bg-success-subtle text-success border border-success-subtle rounded-1 px-2"
              >Sẵn hàng</span
            >
            <span
              v-else
              class="badge bg-danger-subtle text-danger border border-danger-subtle rounded-1 px-2"
              >Hết hàng</span
            >
          </div>

          <hr class="my-1 opacity-50" />

          <div v-if="hasVariants" class="variant-box d-flex flex-column gap-3">
            <div v-for="(values, name) in availableOptions" :key="name">
              <label
                class="small fw-bold text-muted text-uppercase mb-2 d-block"
                style="font-size: 0.75rem"
              >
                {{ name }}:
                <span class="text-dark">{{ selectedOptions[name] }}</span>
              </label>
              <div class="d-flex flex-wrap gap-2">
                <button
                  v-for="v in values"
                  :key="v"
                  @click="selectOption(name, v)"
                  :class="[
                    'btn btn-sm tile-btn transition-all',
                    selectedOptions[name] === v ? 'active' : '',
                  ]"
                >
                  {{ v }}
                </button>
              </div>
            </div>
          </div>

          <div class="short-desc">
            <p
              class="text-secondary small lh-base mb-0"
              v-html="product.description"
            ></p>
          </div>

          <div class="purchase-action d-flex align-items-center gap-3 pt-2">
            <div
              class="qty-picker d-flex align-items-center border border-2 rounded-3 bg-white"
              style="height: 46px"
            >
              <button
                class="btn border-0 px-3 text-dark fw-bold"
                @click="decreaseQty"
                :disabled="quantity <= 1"
              >
                -
              </button>
              <input
                type="text"
                class="form-control border-0 text-center fw-bold p-0 text-dark"
                v-model="quantity"
                readonly
                style="width: 35px; background: none"
              />
              <button
                class="btn border-0 px-3 text-dark fw-bold"
                @click="increaseQty"
                :disabled="quantity >= displayStock"
              >
                +
              </button>
            </div>

            <button
              class="btn btn-primary btn-lg rounded-3 flex-grow-1 fw-bold shadow-sm d-flex align-items-center justify-content-center gap-2"
              :disabled="displayStock <= 0"
              @click="addToCart"
              style="height: 46px; font-size: 0.9rem"
            >
              <i class="bi bi-cart-plus-fill"></i> THÊM GIỎ HÀNG
            </button>
          </div>

          <div v-if="product.attributes?.length" class="mt-3 pt-3 border-top">
            <h6 class="fw-bold text-dark mb-3 small text-uppercase">
              Thông số kỹ thuật
            </h6>
            <div class="specs-grid">
              <div
                v-for="attr in product.attributes"
                :key="attr.name"
                class="d-flex justify-content-between py-1 border-bottom border-light"
              >
                <span class="text-muted small">{{ attr.name }}</span>
                <span class="text-dark fw-semibold small">{{
                  attr.value
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Tile Buttons (Nút biến thể) */
.tile-btn {
  background-color: #fff;
  border: 1px solid #dee2e6;
  color: #495057;
  padding: 0.5rem 1rem;
  font-weight: 500;
  border-radius: 8px;
  min-width: 60px;
}

.tile-btn:hover {
  border-color: #0054a6;
  color: #0054a6;
}

.tile-btn.active {
  background-color: #f0f5ff;
  border-color: #0054a6 !important;
  color: #0054a6 !important;
  font-weight: 700;
  box-shadow: 0 0 0 1px #0054a6;
}

/* Nút tăng giảm số lượng - Ép text đen */
.qty-picker .btn {
  color: #000 !important;
  transition: all 0.2s;
}

.qty-picker .btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  color: #0054a6 !important;
}

.qty-picker input {
  font-size: 0.9rem;
}

/* Responsive Image */
.product-image-card img {
  transition: transform 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
}

.product-image-card:hover img {
  transform: scale(1.05);
}

/* Typography & Utilities */
.transition-all {
  transition: all 0.25s ease;
}

.specs-grid > div:last-child {
  border-bottom: none !important;
}

/* Custom Primary Button */
.btn-primary {
  background-color: #0054a6;
  border-color: #0054a6;
}

.btn-primary:hover {
  background-color: #00448a;
  transform: translateY(-1px);
}
</style>

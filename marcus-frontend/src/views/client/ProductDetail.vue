<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();
const product = ref(null);
const loading = ref(true);
const quantity = ref(1);

// 1. Lấy chi tiết sản phẩm
const getProductDetail = async () => {
  try {
    const id = route.params.id;
    const response = await axios.get("http://localhost:8080/api/v1/products");
    product.value = response.data.find((p) => p.id == id);
  } catch (error) {
    console.error("Lỗi:", error);
  } finally {
    loading.value = false;
  }
};

// 2. HÀM THÊM VÀO GIỎ HÀNG (QUAN TRỌNG)
const addToCart = () => {
  if (!product.value) return;

  let cart = JSON.parse(localStorage.getItem("marcus_cart")) || [];

  const existingItem = cart.find((item) => item.id === product.value.id);

  if (existingItem) {
    existingItem.quantity += quantity.value;
  } else {
    cart.push({
      id: product.value.id,
      name: product.value.name,
      price: product.value.basePrice,
      thumbnail: product.value.thumbnailUrl || "/img/product-1.png",
      quantity: quantity.value,
    });
  }
  localStorage.setItem("marcus_cart", JSON.stringify(cart));
  window.dispatchEvent(new Event("cart-updated"));
  alert("Đã thêm " + quantity.value + " sản phẩm vào giỏ!");
};

onMounted(() => {
  getProductDetail();
});
</script>

<template>
  <div class="container py-5 mt-5">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải dữ liệu...</p>
    </div>

    <div v-else-if="product" class="row g-4 mb-5">
      <div class="col-lg-5">
        <div class="border rounded">
          <img
            :src="product.thumbnailUrl || '/img/product-1.png'"
            class="img-fluid w-100 rounded"
            alt=""
          />
        </div>
      </div>

      <div class="col-lg-7">
        <h2 class="fw-bold mb-3 text-primary">{{ product.name }}</h2>
        <p class="mb-3 text-muted small text-uppercase">
          Danh mục: {{ product.categoryName }}
        </p>
        <h3 class="fw-bold mb-4 text-secondary">
          {{ Number(product.price).toLocaleString("vi-VN") }} ₫
        </h3>
        <p class="mb-4 text-dark">{{ product.description }}</p>

        <div class="d-flex align-items-center mb-4">
          <div class="input-group quantity me-3" style="width: 130px">
            <button
              class="btn btn-light border"
              @click="quantity > 1 ? quantity-- : 1"
            >
              -
            </button>
            <input
              type="text"
              class="form-control text-center border-0"
              v-model="quantity"
            />
            <button class="btn btn-light border" @click="quantity++">+</button>
          </div>

          <button
            class="btn btn-success rounded-pill px-4 py-2"
            @click="addToCart"
          >
            <i class="fa fa-cart-plus me-2"></i> Thêm vào giỏ
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-5">
      <h3 class="text-danger">Không tìm thấy sản phẩm!</h3>
      <router-link to="/" class="btn btn-primary mt-3"
        >Quay lại trang chủ</router-link
      >
    </div>
  </div>
</template>

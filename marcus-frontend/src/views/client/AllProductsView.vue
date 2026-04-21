<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api";
import ProductCard from "../../components/ProductCard.vue";

const products = ref([]);
const isLoading = ref(true);

const fetchAllProducts = async () => {
  isLoading.value = true;
  try {
    // Gọi API lấy TẤT CẢ sản phẩm đang Active
    const res = await api.get("/products");
    products.value = res.data;
  } catch (error) {
    console.error("Lỗi khi tải tất cả sản phẩm:", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchAllProducts();
});
</script>

<template>
  <div class="container py-5 mt-5">
    <div class="text-center mx-auto mb-5" style="max-width: 600px">
      <h5 class="text-primary fw-bold text-uppercase">Khám phá</h5>
      <h2 class="display-6 mb-0">Tất cả Sản phẩm</h2>
    </div>

    <div v-if="isLoading" class="text-center py-5">
      <div
        class="spinner-border text-primary shadow-sm"
        style="width: 3rem; height: 3rem"
      ></div>
    </div>

    <div v-else class="row g-4">
      <div v-if="products.length === 0" class="col-12 text-center text-muted">
        Hiện tại chưa có sản phẩm nào.
      </div>

      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </div>
  </div>
</template>

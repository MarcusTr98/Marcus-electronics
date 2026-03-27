<script setup>
import { ref, onMounted, nextTick } from "vue";
import axios from "axios";
import ProductCard from "../../components/ProductCard.vue";

const products = ref([]);
const loading = ref(true);

const getProducts = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/v1/product");
    products.value = response.data;
  } catch (error) {
    console.error("Lỗi:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await getProducts();
  nextTick(() => {
    if (window.WOW) new window.WOW().init();
  });
});
</script>

<template>
  <div
    class="container-fluid py-5 mb-5 hero-header"
    style="background-color: #e9ecef"
  >
    <div class="container text-center my-5">
      <h1 class="display-4 text-primary mb-4 animated slideInDown">
        Siêu Phẩm Công Nghệ
      </h1>
    </div>
  </div>

  <div class="container-fluid py-5">
    <div class="row g-4 justify-content-center">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </div>
  </div>
</template>

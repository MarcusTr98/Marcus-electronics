<script setup>
import { ref, onMounted, nextTick } from "vue";
import axios from "axios";
import TheHeader from "./components/TheHeader.vue";
import TheFooter from "./components/TheFooter.vue";
import ProductCard from "./components/ProductCard.vue";

const products = ref([]);
const loading = ref(true);

const getProducts = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/v1/product");
    products.value = response.data;
  } catch (error) {
    console.error("Lỗi kết nối Backend:", error);
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
  <div id="app-layout">
    <TheHeader />

    <main class="main-content">
      <div
        class="container-fluid py-5 mb-5 hero-header"
        style="background-color: #e9ecef; margin-top: 0 !important"
      >
        <div class="text-center my-5">
          <h1 class="display-4 text-primary mb-4 animated slideInDown">
            Siêu Phẩm Công Nghệ
          </h1>
          <p class="fs-5 text-dark mb-4">
            iPhone 15 Pro Max & Laptop Gaming chính hãng
          </p>
          <a
            href="#"
            class="btn btn-secondary border-0 rounded-pill py-3 px-5 text-white"
            >Mua ngay</a
          >
        </div>
      </div>

      <div class="container-fluid py-5">
        <div class="mx-auto text-center mb-5">
          <h4
            class="text-primary border-bottom border-primary border-2 d-inline-block p-2"
          >
            Sản phẩm mới
          </h4>
          <h1 class="display-5 text-secondary">Danh sách thiết bị</h1>
        </div>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <div v-else class="row g-4 justify-content-center">
          <ProductCard
            v-for="product in products"
            :key="product.id"
            :product="product"
          />
        </div>
      </div>
    </main>

    <TheFooter />
  </div>
</template>

<style scoped>
#app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.main-content {
  flex: 1;
}
</style>

<template>
  <div>
    <div class="hero-header bg-primary text-white py-5 mb-5">
      <div class="container py-5">
        <div class="row g-5 align-items-center">
          <div class="col-lg-6 text-center text-lg-start">
            <h1 class="display-3 text-white fw-bold mb-4">
              Công nghệ đỉnh cao<br />Trong tầm tay bạn
            </h1>
            <p class="text-white-50 mb-4 pb-2 fs-5">
              Khám phá bộ sưu tập iPhone, Laptop và Phụ kiện chính hãng mới nhất
              tại Marcus Electronics. Cam kết giá tốt nhất thị trường cùng chính
              sách hậu mãi độc quyền.
            </p>
            <a
              href="#featured-products"
              class="btn btn-secondary py-3 px-5 rounded-pill fw-bold"
            >
              Mua sắm ngay
            </a>
          </div>
          <div class="col-lg-6 text-center text-lg-end">
            <img
              class="img-fluid rounded shadow-lg"
              src="https://images.unsplash.com/photo-1606813907291-d86efa9b94db?q=80&w=800&auto=format&fit=crop"
              alt="Hero Image"
              style="max-height: 400px; object-fit: cover"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="container py-5" id="featured-products">
      <div class="text-center mx-auto mb-5" style="max-width: 600px">
        <h5 class="text-primary fw-bold text-uppercase">Gian Hàng</h5>
        <h2 class="display-6 mb-0">Sản phẩm Nổi bật</h2>
      </div>

      <div v-if="isLoading" class="text-center py-5">
        <div
          class="spinner-border text-primary"
          style="width: 3rem; height: 3rem"
          role="status"
        ></div>
        <p class="mt-3 text-muted">Đang tải sản phẩm...</p>
      </div>

      <div v-else class="row g-4">
        <div
          v-if="featuredProducts.length === 0"
          class="col-12 text-center text-muted"
        >
          Hiện tại chưa có sản phẩm nào.
        </div>

        <ProductCard
          v-for="product in featuredProducts"
          :key="product.id"
          :product="product"
        />
      </div>

      <div class="text-center mt-5" v-if="featuredProducts.length > 0">
        <a href="#" class="btn btn-primary py-2 px-4 rounded-pill"
          >Xem tất cả sản phẩm <i class="bi bi-arrow-right ms-2"></i
        ></a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "../../utils/api";
import ProductCard from "../../components/ProductCard.vue";

const featuredProducts = ref([]);
const isLoading = ref(true);

const fetchFeaturedProducts = async () => {
  isLoading.value = true;
  try {
    const res = await api.get("/products/featured");
    featuredProducts.value = res.data;
  } catch (error) {
    console.error("Lỗi khi tải sản phẩm nổi bật:", error);
  } finally {
    isLoading.value = false;
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val || 0) + " ₫";

onMounted(() => {
  fetchFeaturedProducts();
});
</script>

<style scoped>
.transition-hover {
  transition: all 0.3s ease;
}
.transition-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
  border-color: var(--bs-primary) !important;
}

.product-title {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

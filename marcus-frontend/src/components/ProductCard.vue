<script setup>
defineProps({
  product: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <div class="col-md-6 col-lg-4 col-xl-3">
    <div
      class="product-item rounded position-relative h-100 bg-white shadow-sm"
    >
      <div class="product-item-inner border rounded h-100 d-flex flex-column">
        <div class="product-item-inner-item position-relative">
          <img
            :src="product.thumbnailUrl || '/img/product-1.png'"
            class="img-fluid w-100 rounded-top"
            alt=""
          />
        </div>

        <div
          class="text-center p-4 flex-grow-1 d-flex flex-column justify-content-between"
        >
          <div>
            <a href="#" class="d-block mb-2 text-muted small">{{
              product.categoryName
            }}</a>
            <a
              href="#"
              class="d-block h5 mb-3 text-primary fw-bold"
              style="text-decoration: none"
              >{{ product.name }}</a
            >
          </div>
          <div class="fw-bold fs-5 text-secondary">
            {{ Number(product.price).toLocaleString("vi-VN") }} ₫
          </div>
        </div>

        <div class="product-item-add border-top rounded-bottom text-center p-3">
          <a href="#" class="btn btn-success rounded-pill py-2 px-4 w-100">
            <i class="fas fa-shopping-cart me-2"></i> Thêm vào giỏ
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-item {
  transition: 0.3s;
  background: #fff;
}
.product-item:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}
.product-item:hover .product-details {
  opacity: 1 !important;
}
</style>

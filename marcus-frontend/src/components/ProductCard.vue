<script setup>
defineProps({
  product: {
    type: Object,
    required: true,
  },
});

// Hàm thêm vào giỏ (Logic giống hệt trang chi tiết, nhưng số lượng luôn là 1)
const addToCart = (product) => {
  // 1. Lấy giỏ hàng cũ
  let cart = JSON.parse(localStorage.getItem("marcus_cart")) || [];

  // 2. Kiểm tra tồn tại
  const existingItem = cart.find((item) => item.id === product.id);

  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      price: product.value.basePrice,
      thumbnail: product.thumbnailUrl || "/img/product-1.png",
      quantity: 1,
    });
  }

  // 3. Lưu lại
  localStorage.setItem("marcus_cart", JSON.stringify(cart));

  // 4. QUAN TRỌNG: Bắn tín hiệu để Header cập nhật số tiền ngay lập tức
  window.dispatchEvent(new Event("cart-updated"));

  // 5. Thông báo
  alert("Đã thêm " + product.name + " vào giỏ!");
};
</script>

<template>
  <div class="col-md-6 col-lg-4 col-xl-3">
    <div
      class="product-item rounded position-relative h-100 bg-white shadow-sm"
    >
      <div class="product-item-inner border rounded h-100 d-flex flex-column">
        <div class="product-item-inner-item position-relative overflow-hidden">
          <router-link :to="'/product/' + product.id" class="d-block">
            <img
              :src="product.thumbnailUrl || '/img/product-1.png'"
              class="img-fluid w-100 rounded-top"
              alt="Product Image"
              style="height: 250px; object-fit: cover"
            />
          </router-link>

          <div
            class="product-details overlay-pass-through position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
          >
            <router-link
              :to="'/product/' + product.id"
              class="btn btn-primary rounded-circle p-2 clickable-area"
            >
              <i class="fa fa-eye text-white"></i>
            </router-link>
          </div>
        </div>

        <div
          class="text-center p-4 flex-grow-1 d-flex flex-column justify-content-between"
        >
          <div>
            <a
              href="#"
              class="d-block mb-2 text-muted small text-uppercase"
              style="text-decoration: none"
              >{{ product.categoryName }}</a
            >
            <router-link
              :to="'/product/' + product.id"
              class="d-block h5 mb-3 text-primary fw-bold"
              style="text-decoration: none"
            >
              {{ product.name }}
            </router-link>
          </div>
          <div class="fw-bold fs-5 text-secondary">
            {{ Number(product.basePrice).toLocaleString("vi-VN") }} ₫
          </div>
        </div>

        <div class="border-top text-center p-3">
          <button
            class="btn btn-success rounded-pill py-2 px-4 w-100 shadow-sm"
            @click="addToCart(product)"
          >
            <i class="fas fa-shopping-cart me-2"></i> Thêm vào giỏ
          </button>
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
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15) !important;
  transform: translateY(-5px);
}
.product-details {
  background: rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: 0.5s;
}
.product-item:hover .product-details {
  opacity: 1 !important;
}
.overlay-pass-through {
  pointer-events: none;
}
.clickable-area {
  pointer-events: auto;
  cursor: pointer;
}
</style>

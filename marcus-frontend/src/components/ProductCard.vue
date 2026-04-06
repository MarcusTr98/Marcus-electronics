<script setup>
defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const formatImagePath = (path) => {
  if (!path) return "https://placehold.co/300x400";
  if (path.startsWith("http")) return path;
  return path.startsWith("/") ? path : `/${path}`;
};

const addToCart = (product) => {
  let cart = JSON.parse(localStorage.getItem("marcus_cart")) || [];
  const existingItem = cart.find((item) => item.id === product.id);

  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      price: product.basePrice,
      thumbnail: product.thumbnailUrl || "/img/product-1.png",
      quantity: 1,
    });
  }

  localStorage.setItem("marcus_cart", JSON.stringify(cart));
  window.dispatchEvent(new Event("cart-updated"));
  alert("Đã thêm " + product.name + " vào giỏ!");
};
</script>

<template>
  <div class="col-md-6 col-lg-4 col-xl-3">
    <div
      class="product-item rounded position-relative h-100 bg-white shadow-sm transition-hover"
    >
      <div class="product-item-inner border rounded h-100 d-flex flex-column">
        <div
          class="product-img-wrap position-relative overflow-hidden bg-light"
        >
          <router-link
            :to="'/product/' + product.slug"
            class="d-block text-center"
            style="height: 250px"
          >
            <img
              :src="formatImagePath(product.thumbnailUrl)"
              class="img-fluid h-100 w-100 p-4"
              alt="Product Image"
              style="object-fit: contain"
            />
          </router-link>

          <div class="product-overlay">
            <router-link
              :to="'/product/' + product.slug"
              class="eye-btn shadow"
            >
              <i class="fa fa-eye"></i>
            </router-link>
          </div>
        </div>

        <div
          class="text-center p-4 flex-grow-1 d-flex flex-column justify-content-between"
        >
          <div>
            <span class="d-block mb-2 text-muted small text-uppercase">{{
              product.categoryName
            }}</span>
            <router-link
              :to="'/product/' + product.slug"
              class="d-block h5 mb-3 text-dark fw-bold product-title text-decoration-none"
            >
              {{ product.name }}
            </router-link>
          </div>
          <div class="fw-bold fs-5 text-danger">
            {{ Number(product.basePrice).toLocaleString("vi-VN") }} ₫
          </div>
        </div>

        <div class="border-top text-center p-3">
          <button
            class="btn btn-outline-primary rounded-pill py-2 px-4 w-100 fw-bold btn-cart"
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
/* Hiệu ứng khung thẻ */
.transition-hover {
  transition: all 0.3s ease;
}
.transition-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
  border-color: #0054a6 !important;
}

/* OVERLAY CON MẮT TRỰC QUAN */
.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.3); /* Nền mờ sáng sủa, không làm tối ảnh */
  backdrop-filter: blur(2px); /* Hiệu ứng làm mờ nhẹ background của Apple */
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

/* Hiện overlay khi di chuột vào thẻ */
.product-item:hover .product-overlay {
  opacity: 1;
  visibility: visible;
}

/* Nút con mắt tròn xoe */
.eye-btn {
  width: 45px;
  height: 45px;
  background-color: #0054a6;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  font-size: 1.2rem;
  transform: translateY(20px); /* Nút nằm thấp xuống một chút */
  transition: all 0.3s ease;
}

/* Nút nảy lên khi hover thẻ */
.product-item:hover .eye-btn {
  transform: translateY(0);
}

.eye-btn:hover {
  background-color: #ffb800; /* Đổi sang màu cam khi chỉ thẳng vào mắt */
  color: #fff;
}

/* Rút gọn tên sản phẩm */
.product-title {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s;
}
.product-title:hover {
  color: #0054a6 !important;
}

/* Nút giỏ hàng hover */
.btn-cart:hover {
  background-color: #0054a6;
  color: white;
}
</style>

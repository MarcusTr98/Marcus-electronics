<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const cartItems = ref([]);

// 1. Load giỏ hàng từ LocalStorage
const loadCart = () => {
  const savedCart = localStorage.getItem("marcus_cart");
  if (savedCart) {
    cartItems.value = JSON.parse(savedCart);
  }
};

// 2. Tính toán tiền nong
const totalAmount = computed(() => {
  return cartItems.value.reduce(
    (total, item) => total + item.price * item.quantity,
    0,
  );
});

// 3. Cập nhật LocalStorage & Bắn sự kiện cho Header
const syncCart = () => {
  localStorage.setItem("marcus_cart", JSON.stringify(cartItems.value));
  window.dispatchEvent(new Event("cart-updated"));
};

const removeItem = (index) => {
  if (confirm("Xóa sản phẩm này khỏi giỏ hàng?")) {
    cartItems.value.splice(index, 1);
    syncCart();
  }
};

const updateQty = (index, delta) => {
  const item = cartItems.value[index];
  const newQty = item.quantity + delta;
  if (newQty >= 1 && newQty <= 10) {
    // Giới hạn mua tối đa 10
    item.quantity = newQty;
    syncCart();
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";

onMounted(loadCart);
</script>

<template>
  <div class="container py-5 mt-5">
    <div class="row">
      <div class="col-lg-8">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="fw-bold mb-0">Giỏ hàng của bạn</h3>
          <span class="text-muted">({{ cartItems.length }} sản phẩm)</span>
        </div>

        <div
          v-if="cartItems.length === 0"
          class="text-center py-5 bg-white rounded-4 shadow-sm"
        >
          <i class="bi bi-cart-x display-1 text-muted"></i>
          <p class="fs-5 mt-3">Giỏ hàng đang trống!</p>
          <router-link to="/" class="btn btn-primary rounded-pill px-5"
            >Tiếp tục mua sắm</router-link
          >
        </div>

        <div v-else class="cart-list d-flex flex-column gap-3">
          <div
            v-for="(item, index) in cartItems"
            :key="item.skuId"
            class="cart-item bg-white p-3 rounded-4 shadow-sm d-flex align-items-center border"
          >
            <img
              :src="item.thumbnail"
              class="rounded-3 border"
              style="width: 100px; height: 100px; object-fit: contain"
            />

            <div class="ms-4 flex-grow-1">
              <h5 class="fw-bold mb-1 text-dark">{{ item.name }}</h5>
              <p class="small text-primary mb-2 fw-semibold">
                Phân loại:
                <span class="text-muted">{{ item.variantDetail }}</span>
              </p>
              <div class="fw-bold text-danger">
                {{ formatCurrency(item.price) }}
              </div>
            </div>

            <div
              class="qty-picker d-flex align-items-center border rounded-3 overflow-hidden me-4"
              style="height: 38px"
            >
              <button
                class="btn btn-light btn-sm border-0 px-3"
                @click="updateQty(index, -1)"
              >
                -
              </button>
              <input
                type="text"
                class="form-control form-control-sm border-0 text-center fw-bold p-0"
                :value="item.quantity"
                readonly
                style="width: 35px; background: none"
              />
              <button
                class="btn btn-light btn-sm border-0 px-3"
                @click="updateQty(index, 1)"
              >
                +
              </button>
            </div>

            <button
              class="btn btn-outline-danger border-0 rounded-circle"
              @click="removeItem(index)"
            >
              <i class="bi bi-trash3"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="col-lg-4" v-if="cartItems.length > 0">
        <div
          class="card border-0 shadow-sm rounded-4 p-4 sticky-top"
          style="top: 120px"
        >
          <h5 class="fw-bold mb-4">Tóm tắt đơn hàng</h5>
          <div class="d-flex justify-content-between mb-2">
            <span>Tạm tính:</span>
            <span class="fw-bold">{{ formatCurrency(totalAmount) }}</span>
          </div>
          <div class="d-flex justify-content-between mb-3 border-bottom pb-3">
            <span>Giao hàng:</span>
            <span class="text-success fw-bold">Miễn phí</span>
          </div>
          <div class="d-flex justify-content-between align-items-center mb-4">
            <span class="fs-5 fw-bold">TỔNG CỘNG:</span>
            <span class="fs-4 fw-bold text-danger">{{
              formatCurrency(totalAmount)
            }}</span>
          </div>
          <button
            @click="router.push('/checkout')"
            class="btn btn-primary btn-lg w-100 rounded-pill fw-bold py-3 shadow"
          >
            TIẾN HÀNH THANH TOÁN
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

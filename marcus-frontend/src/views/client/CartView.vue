<script setup>
import { ref, computed, onMounted } from "vue";

const cartItems = ref([]);

// 1. Load giỏ hàng từ LocalStorage khi vào trang
onMounted(() => {
  const savedCart = localStorage.getItem("marcus_cart");
  if (savedCart) {
    cartItems.value = JSON.parse(savedCart);
  }
});

// 2. Tính tổng tiền
const totalAmount = computed(() => {
  return cartItems.value.reduce(
    (total, item) => total + item.price * item.quantity,
    0,
  );
});

// 3. Hàm cập nhật LocalStorage khi có thay đổi (xóa, tăng giảm sl)
const updateCart = () => {
  localStorage.setItem("marcus_cart", JSON.stringify(cartItems.value));
};

// 4. Xóa sản phẩm
const removeItem = (index) => {
  if (confirm("Bạn có chắc muốn xóa không?")) {
    cartItems.value.splice(index, 1);
    updateCart();
  }
};

// 5. Tăng giảm số lượng
const decreaseQty = (index) => {
  if (cartItems.value[index].quantity > 1) {
    cartItems.value[index].quantity--;
    updateCart();
  }
};
const increaseQty = (index) => {
  cartItems.value[index].quantity++;
  updateCart();
};
</script>

<template>
  <div class="container-fluid py-5 mt-5">
    <div class="container py-5">
      <h3 class="mb-4 text-primary fw-bold">Giỏ hàng của bạn</h3>

      <div v-if="cartItems.length === 0" class="text-center py-5">
        <p class="fs-5 text-dark">Giỏ hàng đang trống trơn!</p>
        <router-link to="/" class="btn btn-primary rounded-pill px-5"
          >Đi mua sắm ngay</router-link
        >
      </div>

      <div v-else>
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr class="text-center">
                <th scope="col">Sản phẩm</th>
                <th scope="col">Tên</th>
                <th scope="col">Giá</th>
                <th scope="col">Số lượng</th>
                <th scope="col">Thành tiền</th>
                <th scope="col">Xóa</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(item, index) in cartItems"
                :key="item.id"
                class="align-middle text-center"
              >
                <th scope="row">
                  <div class="d-flex align-items-center justify-content-center">
                    <img
                      :src="item.thumbnail"
                      class="img-fluid rounded"
                      style="width: 80px"
                      alt=""
                    />
                  </div>
                </th>
                <td>
                  <p class="mb-0 fw-bold">{{ item.name }}</p>
                </td>
                <td>
                  <p class="mb-0 text-secondary">
                    {{ item.price.toLocaleString("vi-VN") }} ₫
                  </p>
                </td>
                <td>
                  <div
                    class="input-group quantity mx-auto"
                    style="width: 100px"
                  >
                    <div class="input-group-btn">
                      <button
                        class="btn btn-sm btn-minus rounded-circle bg-light border"
                        @click="decreaseQty(index)"
                      >
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <input
                      type="text"
                      class="form-control form-control-sm text-center border-0"
                      :value="item.quantity"
                      readonly
                    />
                    <div class="input-group-btn">
                      <button
                        class="btn btn-sm btn-plus rounded-circle bg-light border"
                        @click="increaseQty(index)"
                      >
                        <i class="fa fa-plus"></i>
                      </button>
                    </div>
                  </div>
                </td>
                <td>
                  <p class="mb-0 fw-bold text-primary">
                    {{ (item.price * item.quantity).toLocaleString("vi-VN") }} ₫
                  </p>
                </td>
                <td>
                  <button
                    class="btn btn-md rounded-circle bg-light border text-danger"
                    @click="removeItem(index)"
                  >
                    <i class="fa fa-times"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="row g-4 justify-content-end mt-4">
          <div class="col-sm-8 col-md-7 col-lg-6 col-xl-4">
            <div class="bg-light rounded p-4">
              <h4 class="mb-4 text-primary">Tổng đơn hàng</h4>
              <div
                class="d-flex justify-content-between mb-3 border-bottom pb-2"
              >
                <h6 class="mb-0">Tổng tiền hàng:</h6>
                <p class="mb-0 fw-bold">
                  {{ totalAmount.toLocaleString("vi-VN") }} ₫
                </p>
              </div>
              <router-link
                to="/checkout"
                class="btn btn-success rounded-pill px-4 py-3 text-uppercase w-100 fw-bold"
              >
                Tiến hành thanh toán
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

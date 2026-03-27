<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const cartItems = ref([]);
const loading = ref(false);

// Dữ liệu Form khách hàng
const orderData = ref({
  fullName: "",
  email: "",
  phoneNumber: "",
  address: "",
  note: "",
});

// 1. Load giỏ hàng
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

// 3. Gửi đơn hàng về Java (QUAN TRỌNG)
const placeOrder = async () => {
  loading.value = true;
  try {
    const payload = {
      // Thông tin khách hàng (Khớp với @JsonProperty)
      full_name: orderData.value.fullName,
      email: orderData.value.email, // Backend không có trường này trong DTO, nhưng gửi thừa cũng không sao
      phone_number: orderData.value.phoneNumber,
      address: orderData.value.address,
      note: orderData.value.note,
      payment_method: "COD",

      // Backend tự tính tiền hoặc không nhận field này nên ta cứ gửi (nếu backend strict mode thì xóa dòng này đi)
      // total_money: totalAmount.value,

      // QUAN TRỌNG: Backend cần "sku_id"
      cart_items: cartItems.value.map((item) => ({
        sku_id: item.id,
        quantity: item.quantity,
      })),
    };

    console.log("Dữ liệu chuẩn gửi đi:", payload);

    const response = await axios.post(
      "http://localhost:8080/api/v1/order",
      payload,
    );

    // Thành công
    alert("Đặt hàng thành công! Mã đơn: " + (response.data.id || "OK"));

    // Dọn dẹp
    localStorage.removeItem("marcus_cart");
    window.dispatchEvent(new Event("cart-updated"));
    router.push("/");
  } catch (error) {
    console.error("LỖI:", error);
    if (error.response && error.response.data) {
      // Hiển thị lỗi rõ ràng từ validation của Java
      alert("Lỗi từ Server: " + JSON.stringify(error.response.data));
    } else {
      alert("Lỗi kết nối: " + error.message);
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="container py-5 mt-5">
    <h2 class="mb-4 text-primary fw-bold text-uppercase">
      Thông tin thanh toán
    </h2>

    <div class="row g-5">
      <div class="col-md-7 col-lg-8">
        <div class="bg-white p-4 rounded shadow-sm border">
          <h4 class="mb-3 text-secondary">Địa chỉ nhận hàng</h4>
          <div class="row g-3">
            <div class="col-12">
              <label class="form-label"
                >Họ và tên <span class="text-danger">*</span></label
              >
              <input
                type="text"
                class="form-control"
                v-model="orderData.fullName"
                placeholder="Nguyễn Văn A"
              />
            </div>

            <div class="col-12">
              <label class="form-label"
                >Số điện thoại <span class="text-danger">*</span></label
              >
              <input
                type="text"
                class="form-control"
                v-model="orderData.phoneNumber"
                placeholder="09xxx"
              />
            </div>

            <div class="col-12">
              <label class="form-label">Email (Tùy chọn)</label>
              <input
                type="email"
                class="form-control"
                v-model="orderData.email"
                placeholder="email@example.com"
              />
            </div>

            <div class="col-12">
              <label class="form-label"
                >Địa chỉ chi tiết <span class="text-danger">*</span></label
              >
              <input
                type="text"
                class="form-control"
                v-model="orderData.address"
                placeholder="Số nhà, đường, phường/xã..."
              />
            </div>

            <div class="col-12">
              <label class="form-label">Ghi chú đơn hàng</label>
              <textarea
                class="form-control"
                v-model="orderData.note"
                rows="3"
                placeholder="Ví dụ: Giao giờ hành chính"
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-5 col-lg-4">
        <div class="bg-light p-4 rounded border">
          <h4 class="d-flex justify-content-between align-items-center mb-3">
            <span class="text-primary">Đơn hàng của bạn</span>
            <span class="badge bg-primary rounded-pill">{{
              cartItems.length
            }}</span>
          </h4>

          <ul class="list-group mb-3">
            <li
              class="list-group-item d-flex justify-content-between lh-sm"
              v-for="item in cartItems"
              :key="item.id"
            >
              <div>
                <h6 class="my-0">{{ item.name }}</h6>
                <small class="text-dark">x {{ item.quantity }}</small>
              </div>
              <span class="text-dark"
                >{{
                  (item.price * item.quantity).toLocaleString("vi-VN")
                }}
                ₫</span
              >
            </li>

            <li class="list-group-item d-flex justify-content-between bg-white">
              <span class="fw-bold">Tổng cộng (VNĐ)</span>
              <strong class="text-secondary fs-5"
                >{{ totalAmount.toLocaleString("vi-VN") }} ₫</strong
              >
            </li>
          </ul>

          <div class="my-3">
            <div class="form-check">
              <input
                id="cod"
                name="paymentMethod"
                type="radio"
                class="form-check-input"
                checked
              />
              <label class="form-check-label" for="cod"
                >Thanh toán khi nhận hàng (COD)</label
              >
            </div>
            <div class="form-check">
              <input
                id="banking"
                name="paymentMethod"
                type="radio"
                class="form-check-input"
                disabled
              />
              <label class="form-check-label" for="banking"
                >Chuyển khoản (Đang bảo trì)</label
              >
            </div>
          </div>

          <button
            class="btn btn-success rounded-pill px-4 py-3 text-uppercase w-100 fw-bold"
            type="button"
            @click="placeOrder"
            :disabled="loading || cartItems.length === 0"
          >
            {{ loading ? "Đang xử lý..." : "ĐẶT HÀNG NGAY" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

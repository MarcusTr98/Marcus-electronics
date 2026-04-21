<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "../../utils/api"; // QUAN TRỌNG: Dùng api.js để có Interceptor JWT

const router = useRouter();
const cartItems = ref([]);
const loading = ref(false);

// Dữ liệu Form khách hàng
const orderData = ref({
  fullName: "",
  phoneNumber: "",
  address: "",
  note: "",
});

// 1. Khởi tạo dữ liệu
onMounted(() => {
  // Load giỏ hàng
  const savedCart = localStorage.getItem("marcus_cart");
  if (savedCart) {
    cartItems.value = JSON.parse(savedCart);
  } else {
    router.push("/cart"); // Giỏ rỗng thì đá về trang giỏ hàng
  }

  // Tự động điền thông tin nếu đã đăng nhập (Lấy từ LocalStorage)
  const savedUser = localStorage.getItem("USERNAME");
  if (savedUser) {
    // Nếu bạn có lưu thêm SĐT/Địa chỉ ở UserInfo thì điền vào đây
    orderData.value.fullName = savedUser;
  }
});

// 2. Tính toán tiền nong
const totalAmount = computed(() => {
  return cartItems.value.reduce(
    (total, item) => total + item.price * item.quantity,
    0,
  );
});

// 3. Gửi đơn hàng (Linh hồn của trang thanh toán)
const placeOrder = async () => {
  // Validation nhẹ phía Frontend
  if (
    !orderData.value.fullName ||
    !orderData.value.phoneNumber ||
    !orderData.value.address
  ) {
    alert("Vui lòng nhập đầy đủ các thông tin bắt buộc (*)");
    return;
  }

  loading.value = true;
  try {
    const payload = {
      full_name: orderData.value.fullName,
      phone_number: orderData.value.phoneNumber,
      address: orderData.value.address,
      note: orderData.value.note,
      payment_method: "COD",
      cart_items: cartItems.value.map((item) => {
        const cleanId =
          typeof item.skuId === "string"
            ? parseInt(item.skuId.replace("PROD_", ""))
            : item.skuId;
        return { sku_id: cleanId, quantity: item.quantity };
      }),
    };

    const response = await api.post("/order", payload);

    // 1. Dọn dẹp giỏ hàng
    localStorage.removeItem("marcus_cart");
    window.dispatchEvent(new Event("cart-updated"));

    // 2. Bóc tách Mã đơn hàng từ chuỗi Backend trả về (Ví dụ lấy số 15)
    const resText = response.data;
    const match = resText.match(/\d+/);
    const orderId = match ? match[0] : "";

    // 3. Chuyển hướng sang trang Thành công kèm ID
    router.push(`/order-success?id=${orderId}`);
  } catch (error) {
    console.error("LỖI ĐẶT HÀNG:", error);
    alert("Đặt hàng thất bại: " + (error.response?.data || error.message));
  } finally {
    loading.value = false;
  }
};

const formatCurrency = (val) =>
  new Intl.NumberFormat("vi-VN").format(val) + " ₫";
</script>

<template>
  <div class="container py-5 mt-5">
    <div class="row justify-content-center">
      <div class="col-xl-10">
        <h2
          class="mb-4 text-dark fw-bold border-start border-4 border-primary ps-3"
        >
          THANH TOÁN
        </h2>

        <div class="row g-4">
          <div class="col-lg-7">
            <div class="card border-0 shadow-sm rounded-4 p-4">
              <h5 class="fw-bold mb-4 d-flex align-items-center">
                <i class="bi bi-geo-alt-fill text-primary me-2"></i> Thông tin
                nhận hàng
              </h5>

              <div class="row g-3">
                <div class="col-12">
                  <label class="form-label small fw-bold text-muted"
                    >HỌ VÀ TÊN *</label
                  >
                  <input
                    type="text"
                    class="form-control form-control-lg fs-6"
                    v-model="orderData.fullName"
                    placeholder="Nhập họ tên người nhận"
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-bold text-muted"
                    >SỐ ĐIỆN THOẠI *</label
                  >
                  <input
                    type="text"
                    class="form-control form-control-lg fs-6"
                    v-model="orderData.phoneNumber"
                    placeholder="Số điện thoại liên hệ"
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-bold text-muted"
                    >EMAIL (NẾU CÓ)</label
                  >
                  <input
                    type="email"
                    class="form-control form-control-lg fs-6"
                    placeholder="để nhận hóa đơn điện tử"
                  />
                </div>

                <div class="col-12">
                  <label class="form-label small fw-bold text-muted"
                    >ĐỊA CHỈ GIAO HÀNG *</label
                  >
                  <input
                    type="text"
                    class="form-control form-control-lg fs-6"
                    v-model="orderData.address"
                    placeholder="Số nhà, tên đường, Phường/Xã, Quận/Huyện..."
                  />
                </div>

                <div class="col-12">
                  <label class="form-label small fw-bold text-muted"
                    >GHI CHÚ</label
                  >
                  <textarea
                    class="form-control fs-6"
                    v-model="orderData.note"
                    rows="3"
                    placeholder="Yêu cầu đặc biệt về giao hàng..."
                  ></textarea>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-5">
            <div class="card border-0 shadow-sm rounded-4 p-4 bg-light">
              <h5 class="fw-bold mb-4">
                Đơn hàng của bạn ({{ cartItems.length }})
              </h5>

              <div class="order-items-list mb-4">
                <div
                  v-for="item in cartItems"
                  :key="item.skuId"
                  class="d-flex align-items-center mb-3 bg-white p-2 rounded-3 border"
                >
                  <img
                    :src="item.thumbnail"
                    class="rounded-2 border"
                    style="width: 60px; height: 60px; object-fit: cover"
                  />
                  <div class="ms-3 flex-grow-1">
                    <h6 class="mb-0 small fw-bold text-dark">
                      {{ item.name }}
                    </h6>
                    <p class="mb-0 text-muted" style="font-size: 0.75rem">
                      {{ item.variantDetail }}
                    </p>
                    <div
                      class="d-flex justify-content-between align-items-center mt-1"
                    >
                      <span class="small">SL: {{ item.quantity }}</span>
                      <span class="fw-bold text-primary small">{{
                        formatCurrency(item.price * item.quantity)
                      }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="payment-methods mb-4">
                <h6 class="fw-bold small text-muted mb-3">
                  PHƯƠNG THỨC THANH TOÁN
                </h6>
                <div class="form-check border p-3 rounded-3 bg-white mb-2">
                  <input
                    class="form-check-input ms-0"
                    type="radio"
                    checked
                    id="codMethod"
                  />
                  <label class="form-check-label ms-2 fw-bold" for="codMethod">
                    Thanh toán khi nhận hàng (COD)
                  </label>
                </div>
                <div
                  class="form-check border p-3 rounded-3 bg-light opacity-50"
                >
                  <input
                    class="form-check-input ms-0"
                    type="radio"
                    disabled
                    id="vnpayMethod"
                  />
                  <label
                    class="form-check-label ms-2 fw-bold"
                    for="vnpayMethod"
                  >
                    VNPay / Chuyển khoản (Đang bảo trì)
                  </label>
                </div>
              </div>

              <div class="total-box border-top pt-3">
                <div class="d-flex justify-content-between mb-2">
                  <span class="text-muted">Tạm tính:</span>
                  <span>{{ formatCurrency(totalAmount) }}</span>
                </div>
                <div class="d-flex justify-content-between mb-3">
                  <span class="text-muted">Phí vận chuyển:</span>
                  <span class="text-success fw-bold">Miễn phí</span>
                </div>
                <div
                  class="d-flex justify-content-between align-items-center border-top pt-3"
                >
                  <h5 class="fw-bold mb-0">TỔNG CỘNG:</h5>
                  <h4 class="fw-bold text-danger mb-0">
                    {{ formatCurrency(totalAmount) }}
                  </h4>
                </div>
              </div>

              <button
                class="btn btn-primary btn-lg w-100 rounded-pill mt-4 fw-bold shadow py-3"
                @click="placeOrder"
                :disabled="loading || cartItems.length === 0"
              >
                <span
                  v-if="loading"
                  class="spinner-border spinner-border-sm me-2"
                ></span>
                {{ loading ? "ĐANG XỬ LÝ..." : "XÁC NHẬN ĐẶT HÀNG" }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-control:focus {
  border-color: #0054a6;
  box-shadow: 0 0 0 0.25rem rgba(0, 84, 166, 0.1);
}
.order-items-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>

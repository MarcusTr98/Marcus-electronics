<script setup>
import { ref } from "vue";
import axios from "axios";

defineProps({ isOpen: Boolean });
const emit = defineEmits(["close"]);

const step = ref(1); // 1: Nhập Email, 2: Nhập OTP & Mật khẩu mới
const form = ref({ email: "", otp: "", newPassword: "" });
const isSubmitting = ref(false);
const errorMsg = ref("");
const successMsg = ref("");

const sendOtp = async () => {
  errorMsg.value = "";
  isSubmitting.value = true;
  try {
    // Gọi API Spring Boot để tạo OTP & gửi mail
    await axios.post(
      "http://localhost:8080/api/v1/auth/forgot-password/send-otp",
      { email: form.value.email },
    );
    successMsg.value =
      "Mã OTP đã được gửi đến email của bạn. Mã có hiệu lực trong 5 phút.";
    step.value = 2; // Chuyển sang bước nhập OTP
  } catch (err) {
    errorMsg.value =
      err.response?.data || "Không thể gửi email. Vui lòng thử lại!";
  } finally {
    isSubmitting.value = false;
  }
};

const resetPassword = async () => {
  errorMsg.value = "";
  isSubmitting.value = true;
  try {
    // Gọi API Spring Boot xác minh OTP và đổi pass
    await axios.post(
      "http://localhost:8080/api/v1/auth/forgot-password/reset",
      {
        email: form.value.email,
        otp: form.value.otp,
        new_password: form.value.newPassword,
      },
    );
    successMsg.value = "Đổi mật khẩu thành công!";
    setTimeout(() => {
      dongModal();
    }, 2000);
  } catch (err) {
    errorMsg.value =
      err.response?.data || "Mã OTP không hợp lệ hoặc đã hết hạn!";
  } finally {
    isSubmitting.value = false;
  }
};

const dongModal = () => {
  step.value = 1;
  form.value = { email: "", otp: "", newPassword: "" };
  errorMsg.value = "";
  successMsg.value = "";
  emit("close");
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="dongModal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>{{ step === 1 ? "Khôi phục mật khẩu" : "Xác thực mã OTP" }}</h3>
        <button class="close-btn" @click="dongModal">×</button>
      </div>

      <div class="modal-body">
        <div v-if="errorMsg" class="alert error">{{ errorMsg }}</div>
        <div v-if="successMsg" class="alert success">{{ successMsg }}</div>

        <form v-if="step === 1" @submit.prevent="sendOtp">
          <div class="field">
            <label>Email đăng ký</label>
            <input
              type="email"
              v-model="form.email"
              required
              placeholder="Nhập email của bạn..."
            />
          </div>
          <button type="submit" class="btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? "Đang gửi..." : "Gửi mã OTP" }}
          </button>
        </form>

        <form v-else @submit.prevent="resetPassword">
          <div class="field">
            <label>Mã OTP (6 số)</label>
            <input
              type="text"
              v-model="form.otp"
              required
              maxlength="6"
              placeholder="123456"
            />
          </div>
          <div class="field">
            <label>Mật khẩu mới</label>
            <input
              type="password"
              v-model="form.newPassword"
              required
              placeholder="Tối thiểu 6 ký tự"
            />
          </div>
          <button type="submit" class="btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? "Đang xử lý..." : "Xác nhận đổi mật khẩu" }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Dùng chung style overlay của TermsModal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #111827;
}
.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}
.field {
  margin-bottom: 16px;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #374151;
}
.field input {
  width: 100%;
  padding: 10px;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  box-sizing: border-box;
}
.btn-primary {
  width: 100%;
  padding: 12px;
  background: #0054a6;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
.alert {
  padding: 10px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 16px;
}
.alert.error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}
.alert.success {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}
</style>

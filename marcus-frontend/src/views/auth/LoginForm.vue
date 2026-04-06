<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import ForgotPasswordModal from "./ForgotPasswordModal.vue";

const router = useRouter();
const emit = defineEmits(["switchTab"]);

const formData = ref({ username: "", password: "" });
const dangTai = ref(false);
const thongBaoLoi = ref("");
const hienMatKhau = ref(false);
const hienModalQuenMatKhau = ref(false);

const dangNhap = async () => {
  thongBaoLoi.value = "";
  if (!formData.value.username || !formData.value.password) {
    thongBaoLoi.value = "Vui lòng nhập đầy đủ thông tin!";
    return;
  }
  dangTai.value = true;
  try {
    const res = await axios.post(
      "http://localhost:8080/api/v1/auth/login",
      formData.value,
    );
    const { token, role, username } = res.data;
    localStorage.setItem("ACCESS_TOKEN", token);
    localStorage.setItem("USER_ROLE", role);
    localStorage.setItem("USERNAME", username);

    role.includes("ADMIN") ? router.push("/admin") : router.push("/");
  } catch (err) {
    thongBaoLoi.value =
      err.response?.status === 401
        ? "Sai tên đăng nhập hoặc mật khẩu!"
        : "Không kết nối được server, vui lòng thử lại!";
  } finally {
    dangTai.value = false;
  }
};
</script>

<template>
  <div class="login-form">
    <div class="form-header">
      <h2>Chào mừng trở lại!</h2>
      <p>Đăng nhập để tiếp tục mua sắm nhé</p>
    </div>

    <!-- Đăng nhập nhanh qua MXH -->
    <div class="social-btns">
      <button type="button" class="social-btn">
        <svg width="18" height="18" viewBox="0 0 24 24">
          <path
            fill="#1877F2"
            d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
          />
        </svg>
        Facebook
      </button>
      <button type="button" class="social-btn">
        <svg width="18" height="18" viewBox="0 0 24 24">
          <path
            fill="#EA4335"
            d="M5.266 9.765A7.077 7.077 0 0 1 12 4.909c1.69 0 3.218.6 4.418 1.582L19.91 3C17.782 1.145 15.055 0 12 0 7.27 0 3.198 2.698 1.24 6.65l4.026 3.115z"
          />
          <path
            fill="#34A853"
            d="M16.04 18.013c-1.09.703-2.474 1.078-4.04 1.078a7.077 7.077 0 0 1-6.723-4.823l-4.04 3.067A11.965 11.965 0 0 0 12 24c2.933 0 5.735-1.043 7.834-3l-3.793-2.987z"
          />
          <path
            fill="#4A90E2"
            d="M19.834 21c2.195-2.048 3.62-5.096 3.62-9 0-.71-.109-1.473-.272-2.182H12v4.637h6.436c-.317 1.559-1.17 2.766-2.395 3.558L19.834 21z"
          />
          <path
            fill="#FBBC05"
            d="M5.277 14.268A7.12 7.12 0 0 1 4.909 12c0-.782.125-1.533.357-2.235L1.24 6.65A11.934 11.934 0 0 0 0 12c0 1.92.445 3.73 1.237 5.335l4.04-3.067z"
          />
        </svg>
        Google
      </button>
    </div>

    <div class="divider"><span>hoặc đăng nhập bằng tài khoản</span></div>

    <!-- Thông báo lỗi -->
    <transition name="fade">
      <div v-if="thongBaoLoi" class="alert-error">
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <circle cx="12" cy="12" r="10" />
          <line x1="12" y1="8" x2="12" y2="12" />
          <line x1="12" y1="16" x2="12.01" y2="16" />
        </svg>
        {{ thongBaoLoi }}
      </div>
    </transition>

    <form @submit.prevent="dangNhap">
      <div class="field">
        <label>Tên đăng nhập hoặc Email</label>
        <input
          type="text"
          v-model="formData.username"
          placeholder="Nhập tên đăng nhập của bạn"
          required
          autocomplete="username"
        />
      </div>

      <div class="field">
        <label>
          Mật khẩu
          <a
            href="#"
            class="forgot-link"
            @click.prevent="hienModalQuenMatKhau = true"
            >Quên mật khẩu?</a
          >
        </label>
        <div class="password-wrap">
          <input
            :type="hienMatKhau ? 'text' : 'password'"
            v-model="formData.password"
            placeholder="Nhập mật khẩu của bạn"
            required
            autocomplete="current-password"
          />
          <button
            type="button"
            class="toggle-pw"
            @click="hienMatKhau = !hienMatKhau"
            :title="hienMatKhau ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'"
          >
            <svg
              v-if="!hienMatKhau"
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
            <svg
              v-else
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
              />
              <line x1="1" y1="1" x2="23" y2="23" />
            </svg>
          </button>
        </div>
      </div>

      <div class="remember-row">
        <label class="checkbox-label">
          <input type="checkbox" checked /> Nhớ tài khoản
        </label>
      </div>

      <button type="submit" class="btn-primary" :disabled="dangTai">
        <span v-if="dangTai" class="spinner"></span>
        {{ dangTai ? "Đang đăng nhập..." : "Đăng nhập" }}
      </button>
    </form>

    <p class="switch-hint">
      Chưa có tài khoản?
      <a href="#" @click.prevent="emit('switchTab', 'register')"
        >Đăng ký ngay</a
      >
    </p>
  </div>
  <ForgotPasswordModal
    :isOpen="hienModalQuenMatKhau"
    @close="hienModalQuenMatKhau = false"
  />
</template>

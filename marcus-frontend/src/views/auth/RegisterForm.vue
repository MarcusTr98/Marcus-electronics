<script setup>
import { ref, computed } from "vue";
import axios from "axios";
import TermsModal from "./TermsModal.vue";

const emit = defineEmits(["switchTab"]);
const hienModalDieuKhoan = ref(false);
const loaiDieuKhoan = ref("tos");
const tieuDeDieuKhoan = ref("");
const moModal = (type, title) => {
  loaiDieuKhoan.value = type;
  tieuDeDieuKhoan.value = title;
  hienModalDieuKhoan.value = true;
};

const form = ref({
  hoTen: "",
  tenDangNhap: "",
  email: "",
  soDienThoai: "",
  matKhau: "",
  xacNhanMatKhau: "",
  dongYDieuKhoan: false,
});

const dangTai = ref(false);
const thongBaoLoi = ref("");
const thongBaoOk = ref("");
const hienMatKhau = ref(false);
const hienXacNhan = ref(false);

// ── Tính độ mạnh mật khẩu ──
const doManh = computed(() => {
  const p = form.value.matKhau;
  if (!p) return 0;
  let diem = 0;
  if (p.length >= 8) diem++;
  if (/[A-Z]/.test(p)) diem++;
  if (/[0-9]/.test(p)) diem++;
  if (/[^A-Za-z0-9]/.test(p)) diem++;
  return diem;
});

const nhanDoManh = computed(
  () => ["", "Yếu", "Trung bình", "Khá", "Mạnh"][doManh.value] ?? "",
);

const mauDoManh = computed(
  () => ["", "#ef4444", "#f59e0b", "#3b82f6", "#22c55e"][doManh.value] ?? "",
);

const matKhauKhop = computed(() =>
  form.value.xacNhanMatKhau
    ? form.value.matKhau === form.value.xacNhanMatKhau
    : null,
);

// ── Xử lý đăng ký ──
const dangKy = async () => {
  thongBaoLoi.value = "";
  if (form.value.matKhau !== form.value.xacNhanMatKhau) {
    thongBaoLoi.value = "Mật khẩu xác nhận không khớp!";
    return;
  }
  if (form.value.matKhau.length < 6) {
    thongBaoLoi.value = "Mật khẩu phải có ít nhất 6 ký tự!";
    return;
  }
  dangTai.value = true;
  try {
    await axios.post("http://localhost:8080/api/v1/auth/register", {
      username: form.value.tenDangNhap,
      password: form.value.matKhau,
      full_name: form.value.hoTen,
      email: form.value.email,
      phone_number: form.value.soDienThoai,
    });
    thongBaoOk.value =
      "Đăng ký thành công! Đang chuyển sang trang đăng nhập...";
    setTimeout(() => {
      emit("switchTab", "login");
      thongBaoOk.value = "";
    }, 1500);
  } catch (err) {
    thongBaoLoi.value =
      typeof err.response?.data === "string"
        ? err.response.data
        : "Đăng ký thất bại, vui lòng thử lại!";
  } finally {
    dangTai.value = false;
  }
};
</script>

<template>
  <div class="register-form">
    <div class="form-header">
      <h2>Tạo tài khoản mới</h2>
      <p>Tham gia Marcus Electronics — Mua sắm thả ga!</p>
    </div>

    <!-- Đăng ký nhanh qua mạng xã hội -->
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

    <div class="divider"><span>hoặc điền thông tin bên dưới</span></div>

    <!-- Thông báo lỗi / thành công -->
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
    <transition name="fade">
      <div v-if="thongBaoOk" class="alert-success">
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
          <polyline points="22 4 12 14.01 9 11.01" />
        </svg>
        {{ thongBaoOk }}
      </div>
    </transition>

    <form @submit.prevent="dangKy">
      <!-- Hàng 1: Họ tên + Tên đăng nhập -->
      <div class="field-row">
        <div class="field">
          <label>Họ và tên</label>
          <input
            type="text"
            v-model="form.hoTen"
            placeholder="Nguyễn Văn A"
            required
            autocomplete="name"
          />
        </div>
        <div class="field">
          <label>Tên đăng nhập</label>
          <input
            type="text"
            v-model="form.tenDangNhap"
            placeholder="nguyenvana123"
            required
            autocomplete="username"
          />
        </div>
      </div>

      <!-- Hàng 2: Email + Số điện thoại -->
      <div class="field-row">
        <div class="field">
          <label>Email</label>
          <input
            type="email"
            v-model="form.email"
            placeholder="email@example.com"
            required
            autocomplete="email"
          />
        </div>
        <div class="field">
          <label>Số điện thoại</label>
          <input
            type="tel"
            v-model="form.soDienThoai"
            placeholder="0901 234 567"
            autocomplete="tel"
          />
        </div>
      </div>

      <!-- Hàng 3: Mật khẩu + Xác nhận -->
      <div class="field-row">
        <div class="field">
          <label>Mật khẩu</label>
          <div class="password-wrap">
            <input
              :type="hienMatKhau ? 'text' : 'password'"
              v-model="form.matKhau"
              placeholder="Tối thiểu 6 ký tự"
              required
              autocomplete="new-password"
            />
            <button
              type="button"
              class="toggle-pw"
              @click="hienMatKhau = !hienMatKhau"
            >
              <svg
                v-if="!hienMatKhau"
                width="15"
                height="15"
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
                width="15"
                height="15"
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
          <!-- Thanh độ mạnh mật khẩu -->
          <div v-if="form.matKhau" class="strength-bar">
            <div class="strength-track">
              <div
                class="strength-fill"
                :style="{ width: doManh * 25 + '%', background: mauDoManh }"
              ></div>
            </div>
            <span :style="{ color: mauDoManh }">{{ nhanDoManh }}</span>
          </div>
        </div>

        <div class="field">
          <label>Xác nhận mật khẩu</label>
          <div class="password-wrap">
            <input
              :type="hienXacNhan ? 'text' : 'password'"
              v-model="form.xacNhanMatKhau"
              placeholder="Nhập lại mật khẩu"
              required
              autocomplete="new-password"
              :class="{
                'input-ok': matKhauKhop === true,
                'input-err': matKhauKhop === false,
              }"
            />
            <button
              type="button"
              class="toggle-pw"
              @click="hienXacNhan = !hienXacNhan"
            >
              <svg
                v-if="!hienXacNhan"
                width="15"
                height="15"
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
                width="15"
                height="15"
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
          <p v-if="matKhauKhop === false" class="match-hint">
            Mật khẩu chưa khớp
          </p>
          <p v-if="matKhauKhop === true" class="match-ok">
            Mật khẩu khớp rồi ✓
          </p>
        </div>
      </div>

      <!-- Đồng ý điều khoản -->
      <label class="checkbox-label terms-row">
        <input type="checkbox" v-model="form.dongYDieuKhoan" required />
        Tôi đồng ý với
        <a href="#" @click.prevent="moModal('tos', 'Điều khoản dịch vụ')"
          >Điều khoản dịch vụ</a
        >
        và
        <a href="#" @click.prevent="moModal('privacy', 'Chính sách bảo mật')"
          >Chính sách bảo mật</a
        >
      </label>

      <button
        type="submit"
        class="btn-primary"
        :disabled="dangTai || !form.dongYDieuKhoan"
      >
        <span v-if="dangTai" class="spinner"></span>
        {{ dangTai ? "Đang tạo tài khoản..." : "Đăng ký ngay" }}
      </button>
    </form>

    <p class="switch-hint">
      Đã có tài khoản?
      <a href="#" @click.prevent="emit('switchTab', 'login')">Đăng nhập</a>
    </p>
  </div>
  <TermsModal
    :isOpen="hienModalDieuKhoan"
    :title="tieuDeDieuKhoan"
    :type="loaiDieuKhoan"
    @close="hienModalDieuKhoan = false"
  />
</template>

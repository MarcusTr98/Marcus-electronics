<script setup>
import { ref } from "vue";
import LoginForm from "./LoginForm.vue";
import RegisterForm from "./RegisterForm.vue";
import "../../assets/auth.css";

const activeTab = ref("login");
</script>

<template>
  <div class="auth-wrapper">
    <!-- ── Panel trái: Thương hiệu ── -->
    <div class="auth-left">
      <router-link to="/" class="brand-link">
        <div class="brand-logo">
          <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
            <rect width="36" height="36" rx="8" fill="#0054a6" />
            <path
              d="M8 10h20M8 18h14M8 26h20"
              stroke="white"
              stroke-width="2.5"
              stroke-linecap="round"
            />
          </svg>
        </div>
        <h1 class="brand-name">
          <span class="brand-blue">MARCUS</span
          ><span class="brand-dark">ELECTRONICS</span>
        </h1>
      </router-link>
      <p class="brand-tagline">Điện tử chính hãng — Giá tốt mỗi ngày</p>

      <div class="features">
        <div class="feature-item">
          <div class="feature-icon">
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke-width="2"
              stroke-linecap="round"
            >
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <span>Nền tảng an toàn &amp; đáng tin cậy</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke-width="2"
              stroke-linecap="round"
            >
              <circle cx="12" cy="12" r="10" />
              <path d="M12 6v6l4 2" />
            </svg>
          </div>
          <span>Giao hàng nhanh toàn quốc</span>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke-width="2"
              stroke-linecap="round"
            >
              <path
                d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
              />
            </svg>
          </div>
          <span>Hơn 100.000 khách hàng hài lòng</span>
        </div>
      </div>
    </div>

    <!-- ── Panel phải: Form ── -->
    <div class="auth-right">
      <div class="auth-card">
        <div class="tab-switcher">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            Đăng nhập
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            Đăng ký
          </button>
        </div>

        <LoginForm
          v-if="activeTab === 'login'"
          @switchTab="activeTab = $event"
        />
        <RegisterForm
          v-if="activeTab === 'register'"
          @switchTab="activeTab = $event"
        />
      </div>
    </div>
  </div>
</template>

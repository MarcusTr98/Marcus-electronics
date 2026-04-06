<template>
  <footer class="modern-footer pt-4 mt-4">
    <div class="container px-4 py-4">
      <div
        class="row g-3 mb-4 pb-4 border-bottom border-secondary-subtle info-row"
      >
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center info-card">
            <div class="icon-circle shadow-sm me-3">
              <i class="fas fa-map-marker-alt"></i>
            </div>
            <div>
              <h6 class="mb-1 fw-bold footer-main-text">Trụ sở chính</h6>
              <p class="mb-0 small footer-sub-text">Hải Phòng, Việt Nam</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center info-card">
            <div class="icon-circle shadow-sm me-3">
              <i class="fas fa-envelope"></i>
            </div>
            <div>
              <h6 class="mb-1 fw-bold footer-main-text">Email Hỗ trợ</h6>
              <p class="mb-0 small footer-sub-text">support@marcus.com</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center info-card">
            <div class="icon-circle shadow-sm me-3">
              <i class="fa fa-phone-alt"></i>
            </div>
            <div>
              <h6 class="mb-1 fw-bold footer-main-text">Hotline (24/7)</h6>
              <p class="mb-0 small footer-sub-text">(+84) 907 640 098</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center info-card">
            <div class="icon-circle shadow-sm me-3">
              <i class="fa fa-clock"></i>
            </div>
            <div>
              <h6 class="mb-1 fw-bold footer-main-text">Giờ làm việc</h6>
              <p class="mb-0 small footer-sub-text">
                08:00 - 21:00 (Hàng ngày)
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <div class="col-lg-4 col-md-6 pe-lg-5">
          <h3
            class="mb-3 fw-black tracking-tight d-flex align-items-center footer-logo-text"
          >
            <i class="fas fa-bolt me-2 text-white"></i>
            <span style="font-size: 1.5rem">Marcus Electronics</span>
          </h3>
          <p
            class="mb-4 pe-lg-4 lh-base footer-sub-text"
            style="font-size: 14px"
          >
            Hệ thống bán lẻ Marcus Electronics chuyên cung cấp thiết bị công
            nghệ chính hãng giá tốt nhất thị trường. Cam kết bảo hành uy tín,
            hậu mãi dài lâu.
          </p>
          <div class="d-flex gap-2">
            <a class="social-btn" href="#"><i class="fab fa-facebook-f"></i></a>
            <a class="social-btn" href="#"><i class="fab fa-youtube"></i></a>
            <a class="social-btn" href="#"><i class="fab fa-tiktok"></i></a>
            <a class="social-btn" href="#"><i class="fab fa-instagram"></i></a>
          </div>
        </div>

        <div class="col-lg-2 col-md-6">
          <h6
            class="mb-3 fw-bold text-uppercase tracking-wider footer-main-text"
          >
            Danh mục
          </h6>
          <nav class="d-flex flex-column gap-2">
            <router-link class="footer-link" to="/category/dien-thoai"
              >Điện thoại iPhone</router-link
            >
            <router-link class="footer-link" to="/category/laptop"
              >Laptop Gaming</router-link
            >
            <router-link class="footer-link" to="/category/phu-kien"
              >Phụ kiện công nghệ</router-link
            >
            <router-link class="footer-link" to="/category/dong-ho"
              >Đồng hồ thông minh</router-link
            >
          </nav>
        </div>

        <div class="col-lg-2 col-md-6">
          <h6
            class="mb-3 fw-bold text-uppercase tracking-wider footer-main-text"
          >
            Chính sách
          </h6>
          <nav class="d-flex flex-column gap-2">
            <router-link class="footer-link" to="/bao-hanh"
              >Trung tâm bảo hành</router-link
            >
            <router-link class="footer-link" to="/doi-tra"
              >Chính sách đổi trả</router-link
            >
            <router-link class="footer-link" to="/thanh-toan"
              >Giao hàng & Thanh toán</router-link
            >
            <router-link class="footer-link" to="/bao-mat"
              >Bảo mật thông tin</router-link
            >
          </nav>
        </div>

        <div class="col-lg-4 col-md-6">
          <h6
            class="mb-3 fw-bold text-uppercase tracking-wider footer-main-text"
          >
            Nhận tin khuyến mãi
          </h6>
          <p class="mb-3 footer-sub-text" style="font-size: 14px">
            Nhận ngay voucher giảm giá 100K khi đăng ký email.
          </p>
          <div class="subscribe-form">
            <input
              class="form-control rounded-pill bg-transparent px-3 py-2 custom-input"
              type="email"
              placeholder="Nhập email của bạn..."
            />
            <button
              class="btn rounded-pill px-4 py-2 mt-2 w-100 fw-semibold shadow-sm custom-btn-dark"
            >
              Đăng ký ngay <i class="fas fa-paper-plane ms-2"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="copyright-bar mt-4">
      <div class="container py-3 border-top border-secondary-subtle">
        <div class="row align-items-center">
          <div class="col-md-6 text-center text-md-start mb-2 mb-md-0">
            <span class="small footer-sub-text">
              &copy; 2026
              <router-link
                to="/"
                class="text-decoration-none fw-semibold hover-dark footer-main-text"
              >
                Marcus Electronics </router-link
              >. All Rights Reserved.
            </span>
          </div>
          <div class="col-md-6 text-center text-md-end">
            <span class="small footer-sub-text">
              Crafted logically by
              <a
                class="text-decoration-none fw-semibold hover-dark footer-main-text"
                href="#"
              >
                Marcus Tran
              </a>
            </span>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.modern-footer {
  background-color: #ff6b00; /* Màu cam chủ đạo của brand */
  color: #ffffff;
  font-family: "Inter", sans-serif;
}

/* Utilities for high contrast on orange background */
.footer-main-text {
  color: #ffffff !important;
}
.footer-sub-text {
  color: rgba(255, 255, 255, 0.9) !important;
}
.footer-logo-text {
  color: #ffffff !important;
}
.border-secondary-subtle {
  border-color: rgba(255, 255, 255, 0.25) !important;
}
.fw-black {
  font-weight: 800;
}
.tracking-tight {
  letter-spacing: -0.5px;
}
.tracking-wider {
  letter-spacing: 1px;
  font-size: 12px;
}

/* Info Cards */
.icon-circle {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: all 0.3s ease;
}
.info-card:hover .icon-circle {
  background: #ffffff;
  color: #ff6b00; /* Trả lại màu cam khi hover để tạo điểm nhấn */
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Links */
.footer-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-size: 13px;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
}
.footer-link::before {
  content: "";
  display: inline-block;
  width: 0;
  height: 2px;
  background-color: #ffffff;
  margin-right: 0;
  transition: all 0.3s ease;
}
.footer-link:hover {
  color: #ffffff;
}
.footer-link:hover::before {
  width: 10px;
  margin-right: 8px;
}

/* Social Buttons */
.social-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  transition: all 0.3s ease;
}
.social-btn:hover {
  background: #ffffff;
  color: #ff6b00;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

/* Form inputs */
.custom-input {
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #ffffff !important;
}
.custom-input:focus {
  background: rgba(255, 255, 255, 0.15) !important;
  border-color: #ffffff;
  box-shadow: none;
}
.custom-input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

/* Dark Subscribe Button - Dùng màu đen/xám đậm để cân bằng màu cam */
.custom-btn-dark {
  background-color: #111827;
  border-color: #111827;
  color: #ffffff;
  transition: background-color 0.2s;
}
.custom-btn-dark:hover {
  background-color: #1f2937;
  color: #ffffff;
}

.hover-dark:hover {
  color: #111827 !important;
}
</style>

<template>
  <div class="fixed-top-header">
    <div class="container-fluid topbar bg-light px-5 d-none d-lg-block">
      <div class="row gx-0 align-items-center">
        <div class="col-lg-4 text-center text-lg-start mb-lg-0">
          <div class="d-inline-flex align-items-center" style="height: 45px">
            <a href="#" class="text-muted me-2 small">Chính sách</a
            ><small class="text-muted"> / </small>
            <a href="#" class="text-muted mx-2 small">Hỗ trợ</a
            ><small class="text-muted"> / </small>
            <a href="#" class="text-muted ms-2 small">Liên hệ</a>
          </div>
        </div>

        <div class="col-lg-4 text-center">
          <div class="d-inline-flex align-items-center justify-content-center">
            <small class="text-dark me-2">Hotline tư vấn:</small>
            <a href="tel:0907640098" class="text-primary fw-bold mb-0"
              >(+84) 907 640 098</a
            >
          </div>
        </div>

        <div class="col-lg-4 text-center text-lg-end">
          <div class="d-inline-flex align-items-center" style="height: 45px">
            <a href="#" class="text-dark small"
              ><i class="fa fa-user text-primary me-2"></i> Đăng nhập / Đăng
              ký</a
            >
          </div>
        </div>
      </div>
    </div>

    <div class="container-fluid px-5 py-4 bg-white border-bottom">
      <div class="row align-items-center">
        <div class="col-md-3">
          <a href="/" class="navbar-brand">
            <h1 class="display-5 text-primary m-0 fw-bold">
              <i class="fas fa-shopping-bag text-secondary me-2"></i>Marcus
            </h1>
          </a>
        </div>

        <div class="col-md-6">
          <div class="position-relative">
            <div class="input-group">
              <input
                type="text"
                class="form-control border-2 border-primary py-3 rounded-start-pill"
                placeholder="Bạn tìm gì hôm nay? (iPhone 15, Laptop Gaming...)"
              />
              <button
                class="btn btn-primary border-2 border-primary py-3 px-4 rounded-end-pill"
              >
                <i class="fas fa-search text-white"></i>
              </button>
            </div>
          </div>
        </div>

        <div class="col-md-3 text-end">
          <div class="d-inline-flex align-items-center">
            <a href="#" class="position-relative me-3">
              <i class="fa fa-heart fa-2x text-muted"></i>
            </a>
            <a href="#" class="position-relative">
              <span
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-success"
              >
                2
              </span>
              <i class="fa fa-shopping-cart fa-2x text-secondary"></i>
            </a>
            <div class="ms-3 text-start">
              <p class="small text-muted mb-0">Giỏ hàng</p>
              <h6 class="text-dark m-0">12.500.000 ₫</h6>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container-fluid bg-primary px-5">
      <nav class="navbar navbar-expand-lg navbar-dark p-0">
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarCollapse"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarCollapse">
          <div class="navbar-nav me-auto">
            <a href="/" class="nav-item nav-link active px-4">TRANG CHỦ</a>
            <a href="#" class="nav-item nav-link px-4">ĐIỆN THOẠI</a>
            <a href="#" class="nav-item nav-link px-4">LAPTOP</a>
            <a href="#" class="nav-item nav-link px-4">PHỤ KIỆN</a>
            <a href="#" class="nav-item nav-link px-4">TIN TỨC</a>
            <a href="#" class="nav-item nav-link px-4">LIÊN HỆ</a>
          </div>

          <div class="d-none d-lg-block">
            <a href="#" class="btn btn-secondary rounded-pill px-4 text-white">
              <i class="fas fa-bolt me-2"></i>Flash Sale
            </a>
          </div>
        </div>
      </nav>
    </div>
  </div>
</template>

<script setup>
// Logic header (xử lý search, login, cart state...) sẽ viết sau
</script>

<style scoped>
/* CSS bổ sung để tinh chỉnh Header */
.topbar {
  font-size: 14px;
}
.navbar-dark .navbar-nav .nav-link {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding-top: 15px;
  padding-bottom: 15px;
  transition: 0.3s;
}
.navbar-dark .navbar-nav .nav-link:hover,
.navbar-dark .navbar-nav .nav-link.active {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.1); /* Hiệu ứng hover nhẹ */
}
</style>

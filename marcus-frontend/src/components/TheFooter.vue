<template>
  <div
    class="container-fluid footer pt-5 mt-5 wow fadeIn"
    data-wow-delay="0.1s"
  >
    <div class="container-fluid px-5 py-5">
      <div class="row g-4 mb-5 pb-4 border-bottom border-secondary">
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center">
            <div
              class="btn-square bg-secondary rounded-circle text-white p-4 me-3"
            >
              <i class="fas fa-map-marker-alt fa-2x"></i>
            </div>
            <div>
              <h5 class="text-white mb-1">Địa chỉ</h5>
              <p class="mb-0 text-white-50">Hải Phòng, Việt Nam</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center">
            <div
              class="btn-square bg-secondary rounded-circle text-white p-4 me-3"
            >
              <i class="fas fa-envelope fa-2x"></i>
            </div>
            <div>
              <h5 class="text-white mb-1">Email Hỗ trợ</h5>
              <p class="mb-0 text-white-50">support@marcus.com</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center">
            <div
              class="btn-square bg-secondary rounded-circle text-white p-4 me-3"
            >
              <i class="fa fa-phone-alt fa-2x"></i>
            </div>
            <div>
              <h5 class="text-white mb-1">Tổng đài</h5>
              <p class="mb-0 text-white-50">(+84) 907 640 098</p>
            </div>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <div class="d-flex align-items-center">
            <div
              class="btn-square bg-secondary rounded-circle text-white p-4 me-3"
            >
              <i class="fa fa-clock fa-2x"></i>
            </div>
            <div>
              <h5 class="text-white mb-1">Giờ làm việc</h5>
              <p class="mb-0 text-white-50">08:00 - 21:00 (Hàng ngày)</p>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-5">
        <div class="col-lg-4 col-md-6">
          <h1 class="text-primary mb-4">
            <i class="fas fa-shopping-bag text-secondary me-2"></i>Marcus
          </h1>
          <p class="text-white-50 mb-4">
            Hệ thống bán lẻ Marcus Electronics chuyên cung cấp iPhone, Laptop,
            Phụ kiện công nghệ chính hãng giá tốt nhất thị trường. Cam kết bảo
            hành uy tín, hậu mãi dài lâu.
          </p>
          <div class="d-flex pt-2">
            <a class="btn btn-outline-light btn-social me-2" href="#"
              ><i class="fab fa-twitter"></i
            ></a>
            <a class="btn btn-outline-light btn-social me-2" href="#"
              ><i class="fab fa-facebook-f"></i
            ></a>
            <a class="btn btn-outline-light btn-social me-2" href="#"
              ><i class="fab fa-youtube"></i
            ></a>
            <a class="btn btn-outline-light btn-social" href="#"
              ><i class="fab fa-tiktok"></i
            ></a>
          </div>
        </div>

        <div class="col-lg-2 col-md-6">
          <h4 class="text-white mb-4">Mua hàng</h4>
          <router-link
            class="btn btn-link text-white-50"
            to="/category/dien-thoai"
            >Điện thoại iPhone</router-link
          >
          <router-link class="btn btn-link text-white-50" to="/category/laptop"
            >Laptop Gaming</router-link
          >
          <router-link
            class="btn btn-link text-white-50"
            to="/category/phu-kien"
            >Phụ kiện giá rẻ</router-link
          >
        </div>

        <div class="col-lg-2 col-md-6">
          <h4 class="text-white mb-4">Hỗ trợ</h4>
          <router-link class="btn btn-link text-white-50" to="/bao-hanh"
            >Trung tâm bảo hành</router-link
          >
          <router-link class="btn btn-link text-white-50" to="/doi-tra"
            >Chính sách đổi trả</router-link
          >
          <router-link class="btn btn-link text-white-50" to="/thanh-toan"
            >Giao hàng & Thanh toán</router-link
          >
        </div>

        <div class="col-lg-4 col-md-6">
          <h4 class="text-white mb-4">Đăng ký nhận tin</h4>
          <p class="text-white-50">
            Nhận ngay voucher giảm giá 100K khi đăng ký email nhận tin khuyến
            mãi.
          </p>
          <div class="position-relative mx-auto" style="max-width: 400px">
            <input
              class="form-control bg-transparent w-100 py-3 ps-4 pe-5 text-white"
              type="text"
              placeholder="Email của bạn"
            />
            <button
              type="button"
              class="btn btn-secondary py-2 position-absolute top-0 end-0 mt-2 me-2"
            >
              Đăng ký
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="container-fluid copyright bg-primary py-4 px-5">
      <div class="row">
        <div class="col-md-6 text-center text-md-start mb-3 mb-md-0">
          <span class="text-light">
            &copy; 2026
            <router-link to="/" class="text-white border-bottom"
              >Marcus Electronics</router-link
            >. All Right Reserved.
          </span>
        </div>
        <div class="col-md-6 text-center text-md-end text-white">
          Designed By
          <a class="border-bottom text-white" href="#">Marcus Tran</a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Giữ nguyên CSS bạn đã cung cấp */
.footer {
  background-color: #0b1c2c;
  color: #ffffff;
}
.footer .btn.btn-social {
  margin-right: 5px;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bs-white);
  border: 1px solid rgba(256, 256, 256, 0.1);
  border-radius: 35px;
  transition: 0.3s;
}
.footer .btn.btn-social:hover {
  color: var(--bs-primary);
  background: var(--bs-white);
}
.footer .btn-link {
  display: block;
  margin-bottom: 5px;
  padding: 0;
  text-align: left;
  color: rgba(255, 255, 255, 0.5);
  font-weight: normal;
  text-transform: capitalize;
  transition: 0.3s;
  text-decoration: none;
}
.footer .btn-link::before {
  position: relative;
  content: "\f105";
  font-family: "Font Awesome 5 Free";
  font-weight: 900;
  margin-right: 10px;
}
.footer .btn-link:hover {
  letter-spacing: 1px;
  box-shadow: none;
  color: var(--bs-secondary);
}
.btn-square {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

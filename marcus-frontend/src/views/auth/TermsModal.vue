<script setup>
defineProps({
  isOpen: Boolean,
  title: String,
  type: String,
});
const emit = defineEmits(["close"]);
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button class="close-btn" @click="emit('close')">×</button>
      </div>
      <div class="modal-body">
        <p v-if="type === 'tos'">
          Chào mừng bạn đến với Marcus Electronics. Khi đăng ký tài khoản và sử
          dụng dịch vụ, bạn đồng ý cung cấp thông tin chính xác và chịu trách
          nhiệm bảo mật tài khoản của mình. Chúng tôi nghiêm cấm mọi hành vi
          gian lận, phá hoại hoặc sử dụng nền tảng cho mục đích vi phạm pháp
          luật. Marcus Electronics có quyền đình chỉ hoặc khóa vĩnh viễn các tài
          khoản vi phạm mà không cần thông báo trước. Các điều khoản có thể được
          cập nhật để phù hợp với quy định hiện hành.
        </p>
        <p v-else-if="type === 'privacy'">
          Marcus Electronics cam kết bảo vệ dữ liệu cá nhân của bạn. Chúng tôi
          chỉ thu thập các thông tin cần thiết (Họ tên, Email, Số điện thoại) để
          phục vụ việc xử lý đơn hàng, hỗ trợ kỹ thuật và nâng cao trải nghiệm
          mua sắm. Dữ liệu của bạn được mã hóa và lưu trữ an toàn. Chúng tôi
          tuyệt đối không bán, trao đổi hoặc cung cấp thông tin cá nhân của bạn
          cho bất kỳ bên thứ ba nào khi chưa có sự đồng ý, ngoại trừ các trường
          hợp có yêu cầu từ cơ quan pháp luật.
        </p>
      </div>
      <div class="modal-footer">
        <button class="btn-primary" @click="emit('close')">Đã hiểu</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
  max-width: 450px;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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
.modal-body p {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  text-align: justify;
}
.modal-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.btn-primary {
  padding: 8px 16px;
  background: #0054a6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>

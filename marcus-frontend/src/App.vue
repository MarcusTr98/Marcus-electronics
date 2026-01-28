<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

// 1. Biến chứa danh sách sản phẩm
const products = ref([]);

// 2. Hàm gọi API từ Java (Cổng 8088)
const getProducts = async () => {
  try {
    // Gọi API: GET http://localhost:8088/api/v1/product
    const response = await axios.get("http://localhost:8088/api/v1/product");

    // Lấy dữ liệu gán vào biến products
    products.value = response.data;
    console.log("Dữ liệu lấy về:", response.data); // Xem log ở F12 nếu lỗi
  } catch (error) {
    console.error("Lỗi kết nối Backend:", error);
    alert(
      "Không lấy được dữ liệu! Bạn đã chạy Backend chưa? Đã thêm @CrossOrigin chưa?",
    );
  }
};

// 3. Gọi hàm ngay khi trang web tải xong
onMounted(() => {
  getProducts();
});
</script>

<template>
  <div class="shop-container">
    <header>
      <h1>🛒 Marcus Electronics</h1>
      <p>Chuyên Laptop & Điện thoại chất lượng cao</p>
    </header>

    <div v-if="products.length === 0" class="loading">
      Đang tải sản phẩm... (Hoặc chưa có dữ liệu)
    </div>

    <div class="product-grid">
      <div v-for="product in products" :key="product.id" class="card">
        <div class="card-img">
          <img
            :src="
              product.thumbnailUrl ||
              'https://placehold.co/600x400?text=No+Image'
            "
            alt="Product"
          />
        </div>
        <div class="card-body">
          <h3>{{ product.name }}</h3>
          <p class="category">{{ product.categoryName }}</p>
          <p class="price">{{ Number(product.price).toLocaleString() }} VNĐ</p>
          <button>Thêm vào giỏ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* CSS trang trí cho đẹp */
.shop-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

header {
  text-align: center;
  margin-bottom: 40px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
}

.card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  background: white;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-img img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.card-body {
  padding: 15px;
  text-align: center;
}

.price {
  font-size: 1.2rem;
  color: #e74c3c;
  font-weight: bold;
  margin: 10px 0;
}

button {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s;
}

button:hover {
  background-color: #2980b9;
}
</style>

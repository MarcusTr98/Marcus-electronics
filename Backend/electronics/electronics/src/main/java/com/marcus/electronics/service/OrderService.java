package com.marcus.electronics.service;

import com.marcus.electronics.dto.OrderDetailResponseDTO;
import com.marcus.electronics.dto.OrderRequestDTO;
import com.marcus.electronics.dto.OrderResponseDTO;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SkuRepository skuRepository;
    private final UserRepository userRepository;

    @Transactional // Quan trọng: Nếu lỗi ở bước nào, Rollback lại hết (không trừ kho bậy)
    public Order createOrder(OrderRequestDTO requestDTO) {

        // 1. Tạo Order cha trước
        Order order = Order.builder()
                .fullName(requestDTO.getFullName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .address(requestDTO.getAddress())
                .note(requestDTO.getNote())
                .paymentMethod(requestDTO.getPaymentMethod())
                .status("PENDING")
                .totalMoney(BigDecimal.ZERO) // Tính sau
                .build();

        // Save tạm để lấy ID
        Order savedOrder = orderRepository.save(order);

        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Duyệt qua từng món trong giỏ hàng
        for (OrderRequestDTO.CartItemDTO item : requestDTO.getCartItems()) {

            // Tìm SKU trong kho
            Sku sku = skuRepository.findById(item.getSkuId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            // CHECK TỒN KHO
            if (sku.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + sku.getSkuCode() + " không đủ hàng!");
            }

            // TRỪ KHO (Cập nhật lại kho)
            sku.setStock(sku.getStock() - item.getQuantity());
            skuRepository.save(sku);

            // Tính tiền: Giá lúc mua * Số lượng
            BigDecimal itemTotal = sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(itemTotal);

            // Tạo OrderDetail (Lưu vết giá snapshot)
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .sku(sku)
                    .price(sku.getPrice()) // QUAN TRỌNG: Lưu giá hiện tại vào đây
                    .numberOfProducts(item.getQuantity())
                    .totalMoney(itemTotal)
                    .build();

            orderDetailRepository.save(detail);
        }

        // 3. Update lại tổng tiền cho Order cha
        savedOrder.setTotalMoney(grandTotal);
        return orderRepository.save(savedOrder);
    }

    // PHẦN ADMIN: QUẢN LÝ ĐƠN HÀNG

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        // Lấy tất cả đơn hàng, sắp xếp mới nhất lên đầu
        List<Order> orders = orderRepository.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"));

        return orders.stream().map(o -> OrderResponseDTO.builder()
                .id(o.getId())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .fullName(o.getFullName())
                .phoneNumber(o.getPhoneNumber())
                .address(o.getAddress())
                .note(o.getNote())
                .orderDate(o.getOrderDate())
                .status(o.getStatus())
                .totalMoney(o.getTotalMoney())
                .paymentMethod(o.getPaymentMethod())
                .build()).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // LOGIC HOÀN KHO (RESTOCK):
        // Chỉ hoàn kho khi trạng thái mới là CANCELLED và trạng thái cũ chưa phải là
        // CANCELLED
        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(order.getStatus())) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                Sku sku = detail.getSku();
                // Cộng lại số lượng đã trừ
                sku.setStock(sku.getStock() + detail.getNumberOfProducts());
                skuRepository.save(sku);
            }
        }
        // Nếu chuyển từ CANCELLED sang trạng thái khác (Admin phục hồi đơn) -> Phải trừ
        // kho lại
        else if (!"CANCELLED".equals(newStatus) && "CANCELLED".equals(order.getStatus())) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                Sku sku = detail.getSku();
                if (sku.getStock() < detail.getNumberOfProducts()) {
                    throw new RuntimeException(
                            "Không thể phục hồi đơn! Mã SKU " + sku.getSkuCode() + " đã hết hàng trong kho.");
                }
                sku.setStock(sku.getStock() - detail.getNumberOfProducts());
                skuRepository.save(sku);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // Lấy chi tiết ruột đơn hàng cho Admin
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getOrderDetailsAdmin(Long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);

        return details.stream().map(d -> {
            // Nối chuỗi cấu hình biến thể (Ví dụ: Màu sắc: Đen | Dung lượng: 128GB)
            String variantInfo = "";
            if (d.getSku().getSkuValues() != null && !d.getSku().getSkuValues().isEmpty()) {
                variantInfo = d.getSku().getSkuValues().stream()
                        .map(sv -> sv.getOptionValue().getOption().getName() + ": " + sv.getOptionValue().getValue())
                        .collect(java.util.stream.Collectors.joining(" | "));
            }

            // Ưu tiên lấy ảnh của SKU, nếu SKU không có ảnh thì lấy ảnh của Product
            String imgUrl = d.getSku().getImageUrl();
            if (imgUrl == null || imgUrl.isEmpty()) {
                imgUrl = d.getSku().getProduct().getThumbnailUrl();
            }

            return OrderDetailResponseDTO.builder()
                    .detailId(d.getId())
                    .productName(d.getSku().getProduct().getName())
                    .skuCode(d.getSku().getSkuCode())
                    .variantDetail(variantInfo)
                    .imageUrl(imgUrl)
                    .quantity(d.getNumberOfProducts())
                    .unitPrice(d.getPrice()) // Lấy giá từ bảng OrderDetail (giá lịch sử)
                    .lineTotal(d.getTotalMoney())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }
}
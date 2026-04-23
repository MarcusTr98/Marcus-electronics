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
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SkuRepository skuRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(OrderRequestDTO requestDTO, Long userId) {
        // Tìm User nếu khách đã đăng nhập
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        // 1. Tạo Order cha trước
        Order order = Order.builder()
                .user(user) // SỬA LỖI: Gán User vào đơn hàng để lưu lịch sử
                .fullName(requestDTO.getFullName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .address(requestDTO.getAddress())
                .note(requestDTO.getNote())
                .paymentMethod(requestDTO.getPaymentMethod())
                .status("PENDING")
                .totalMoney(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(order);
        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Duyệt qua từng món trong giỏ hàng
        for (OrderRequestDTO.CartItemDTO item : requestDTO.getCartItems()) {
            Sku sku = skuRepository.findById(item.getSkuId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (sku.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + sku.getSkuCode() + " không đủ hàng!");
            }

            // Trừ kho
            sku.setStock(sku.getStock() - item.getQuantity());
            skuRepository.save(sku);

            BigDecimal itemTotal = sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(itemTotal);

            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .sku(sku)
                    .price(sku.getPrice())
                    .numberOfProducts(item.getQuantity())
                    .totalMoney(itemTotal)
                    .build();

            orderDetailRepository.save(detail);
        }

        // 3. Update lại tổng tiền
        savedOrder.setTotalMoney(grandTotal);
        return orderRepository.save(savedOrder);
    }

    // ==========================================
    // PHẦN ADMIN: QUẢN LÝ ĐƠN HÀNG
    // ==========================================
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrdersForAdmin() {
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

        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(order.getStatus())) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                Sku sku = detail.getSku();
                sku.setStock(sku.getStock() + detail.getNumberOfProducts());
                skuRepository.save(sku);
            }
        } else if (!"CANCELLED".equals(newStatus) && "CANCELLED".equals(order.getStatus())) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                Sku sku = detail.getSku();
                if (sku.getStock() < detail.getNumberOfProducts()) {
                    throw new RuntimeException("Mã SKU " + sku.getSkuCode() + " đã hết hàng.");
                }
                sku.setStock(sku.getStock() - detail.getNumberOfProducts());
                skuRepository.save(sku);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getOrderDetailsAdmin(Long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);

        return details.stream().map(d -> {
            String variantInfo = "";
            if (d.getSku().getSkuValues() != null && !d.getSku().getSkuValues().isEmpty()) {
                variantInfo = d.getSku().getSkuValues().stream()
                        .map(sv -> sv.getOptionValue().getOption().getName() + ": " + sv.getOptionValue().getValue())
                        .collect(java.util.stream.Collectors.joining(" | "));
            }

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
                    .unitPrice(d.getPrice())
                    .lineTotal(d.getTotalMoney())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    // ==========================================
    // PHẦN CLIENT: LỊCH SỬ MUA HÀNG
    // ==========================================
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

        return orders.stream().map(o -> OrderResponseDTO.builder()
                .id(o.getId())
                .fullName(o.getFullName())
                .phoneNumber(o.getPhoneNumber())
                .address(o.getAddress())
                .orderDate(o.getOrderDate())
                .status(o.getStatus())
                .totalMoney(o.getTotalMoney())
                .paymentMethod(o.getPaymentMethod())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrderByClient(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng đã được xử lý, không thể tự hủy. Vui lòng liên hệ tổng đài.");
        }

        order.setStatus("CANCELLED");

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail detail : details) {
            Sku sku = detail.getSku();
            sku.setStock(sku.getStock() + detail.getNumberOfProducts()); // Hoàn kho
            skuRepository.save(sku);
        }

        orderRepository.save(order);
    }
}
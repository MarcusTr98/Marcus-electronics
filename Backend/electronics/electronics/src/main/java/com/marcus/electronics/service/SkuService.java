package com.marcus.electronics.service;

import com.marcus.electronics.dto.InventoryResponseDTO;
import com.marcus.electronics.dto.OptionResponseDTO;
import com.marcus.electronics.dto.SkuRequestDTO;
import com.marcus.electronics.dto.SkuUpdateRequestDTO;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuService {

    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final SkuValueRepository skuValueRepository;

    // PHẦN 1: QUẢN LÝ THUỘC TÍNH (OPTION & VALUE)

    @Transactional
    public OptionResponseDTO createOptionWithOptionsValues(Long productId, String optionName, List<String> values) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // 1. Lưu Option (VD: "Màu sắc")
        Option option = new Option();
        option.setProduct(product);
        option.setName(optionName);
        Option savedOption = optionRepository.save(option);

        // 2. Lưu danh sách OptionValue và gom ID lại
        // Khởi tạo Map để chứa kết quả trả về
        java.util.Map<String, Long> valueIdsMap = new java.util.HashMap<>();

        for (String val : values) {
            OptionValue optionValue = new OptionValue();
            optionValue.setOption(savedOption);
            optionValue.setValue(val);

            // Lưu xuống DB và lấy Entity đã có ID
            OptionValue savedValue = optionValueRepository.save(optionValue);

            // Nhét vào Map (Key = "Đỏ", Value = 15)
            valueIdsMap.put(savedValue.getValue(), savedValue.getId());
        }

        // 3. Đóng gói trả về cho Frontend
        return OptionResponseDTO.builder()
                .optionId(savedOption.getId())
                .optionName(savedOption.getName())
                .valueIds(valueIdsMap)
                .build();
    }

    // PHẦN 2: QUẢN LÝ SKU VÀ SKU_VALUE

    @Transactional
    public void createSkuForProduct(Long productId, SkuRequestDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // 1. Lưu thông tin SKU cơ bản
        Sku sku = Sku.builder()
                .product(product)
                .skuCode(dto.getSkuCode())
                .price(dto.getPrice())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .imageUrl(dto.getImageUrl())
                .isActive(true)
                .build();

        Sku savedSku = skuRepository.save(sku);

        // 2. Lưu map SkuValue (Kết nối Sku với OptionValue)
        if (dto.getOptionValueIds() != null && !dto.getOptionValueIds().isEmpty()) {
            for (Long optionValueId : dto.getOptionValueIds()) {
                OptionValue optionValue = optionValueRepository.findById(optionValueId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy OptionValue ID: " + optionValueId));

                SkuValue skuValue = new SkuValue();
                skuValue.setSku(savedSku);
                skuValue.setOptionValue(optionValue);

                skuValueRepository.save(skuValue);
            }
        }
    }

    @Transactional
    public void deleteSku(Long skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU"));
        sku.setIsActive(false); // Soft delete
        skuRepository.save(sku);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInventoryReport() {
        List<Sku> allSkus = skuRepository.findAllForInventory();

        return allSkus.stream().map(sku -> {

            // 1. Gia cố an toàn: Nếu SKU chưa có thuộc tính nào thì trả về rỗng, tránh lỗi
            // Stream
            String detail = "";
            if (sku.getSkuValues() != null && !sku.getSkuValues().isEmpty()) {
                detail = sku.getSkuValues().stream()
                        .map(sv -> sv.getOptionValue().getOption().getName() + ": " + sv.getOptionValue().getValue())
                        .collect(java.util.stream.Collectors.joining(" | "));
            }

            // 2. Gia cố an toàn NullPointerException cho Category
            String categoryName = "Chưa phân loại";
            if (sku.getProduct() != null && sku.getProduct().getCategory() != null) {
                categoryName = sku.getProduct().getCategory().getName();
            }

            // 3. Đóng gói DTO an toàn
            return InventoryResponseDTO.builder()
                    .skuId(sku.getId())
                    .productName(sku.getProduct() != null ? sku.getProduct().getName() : "Không tên")
                    .categoryName(categoryName)
                    .skuCode(sku.getSkuCode())
                    .variantDetail(detail)
                    .price(sku.getPrice())
                    .stock(sku.getStock())
                    .build();

        }).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void updateStock(Long skuId, Integer newStock) {
        if (newStock < 0)
            throw new RuntimeException("Số lượng kho không được âm");

        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU"));

        sku.setStock(newStock);
        skuRepository.save(sku);
    }

    // PHẦN 3: XEM CHI TIẾT & CẬP NHẬT TRẠNG THÁI SKU

    @Transactional(readOnly = true)
    public Map<String, Object> getProductVariantsDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        List<Sku> skus = skuRepository.findAllByProductIdForAdmin(productId);

        List<Map<String, Object>> skuList = skus.stream().map(sku -> {
            Map<String, Object> skuMap = new java.util.HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("skuCode", sku.getSkuCode());
            skuMap.put("price", sku.getPrice());
            skuMap.put("stock", sku.getStock());
            skuMap.put("active", sku.getIsActive());
            skuMap.put("imageUrl", sku.getImageUrl());

            Map<String, String> options = new java.util.HashMap<>();
            for (SkuValue sv : sku.getSkuValues()) {
                options.put(sv.getOptionValue().getOption().getName(), sv.getOptionValue().getValue());
            }
            skuMap.put("options", options);

            return skuMap;
        }).collect(java.util.stream.Collectors.toList());
        Map<String, Object> response = new java.util.HashMap<>();
        Map<String, Object> productInfo = new java.util.HashMap<>();
        productInfo.put("id", product.getId());
        productInfo.put("name", product.getName());
        response.put("product", productInfo);
        response.put("skus", skuList);
        return response;
    }

    @Transactional
    public void toggleSkuStatus(Long skuId, Boolean isActive) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU"));
        sku.setIsActive(isActive);
        skuRepository.save(sku);
    }

    @Transactional
    public void updateSkuInfo(Long skuId, SkuUpdateRequestDTO dto) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU"));

        // Chỉ cập nhật các thông tin cơ bản, tuyệt đối không chạm vào SkuValues
        sku.setPrice(dto.getPrice());
        if (dto.getStock() != null)
            sku.setStock(dto.getStock());
        if (dto.getImageUrl() != null)
            sku.setImageUrl(dto.getImageUrl());
        if (dto.getSkuCode() != null)
            sku.setSkuCode(dto.getSkuCode());
        skuRepository.save(sku);
    }

}
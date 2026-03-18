package dev.products.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record CreateProductRequest(

        @NotBlank(message = "이름은 필수값입니다.")
        @Schema(description = "상품명", example = "맥북 pro")
        String name,

        @Length(max = 999, message = "1000자 미만입니다.")
        @Schema(description = "상품 설명", example = "M3 칩이 탑재된 고성능 노트북")
        String description,

        @NotNull(message = "가격은 필수값입니다.")
        @DecimalMin("0.0")
        @Schema(description = "상품 가격", example = "3500000")
        Double price,

        @NotNull(message = "재고는 필수값입니다.")
        @Positive(message = "재고는 최소 1개 이상입니다.")
        @Schema(description = "상품 재고", example = "5")
        Integer stock,

        @NotBlank(message = "카테고리 명은 필수값입니다.")
        @Schema(description = "상품 카테고리", example = "전자제품")
        String category

) {}
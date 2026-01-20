package com.example.avakids_backend.DTO.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductVariantRequest {

    @Size(max = 255, message = "VARIANT_NAME_TOO_LONG")
    private String variantName;

    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_INVALID")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "SALE_PRICE_INVALID")
    private BigDecimal salePrice;

    @Min(value = 0, message = "STOCK_NEGATIVE")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "WEIGHT_INVALID")
    private BigDecimal weight;

    @Size(max = 100, message = "DIMENSION_TOO_LONG")
    private String dimensions;

    @Size(max = 50, message = "BARCODE_TOO_LONG")
    private String barcode;

    private Boolean isDefault;

    private List<@NotNull(message = "OPTION_VALUE_ID_NULL") Long> optionValueIds;
}

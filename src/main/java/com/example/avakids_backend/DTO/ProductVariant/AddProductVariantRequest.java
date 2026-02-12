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
public class AddProductVariantRequest {

    @NotBlank(message = "VARIANT_NAME_NOT_BLANK")
    @Size(max = 255, message = "VARIANT_NAME_TOO_LONG")
    private String variantName;

    @NotNull(message = "PRICE_NOT_NULL")
    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_INVALID")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "SALE_PRICE_INVALID")
    private BigDecimal salePrice;

    @NotNull(message = "STOCK_NOT_NULL")
    @Min(value = 0, message = "STOCK_NEGATIVE")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "WEIGHT_INVALID")
    private BigDecimal weight;

    @Size(max = 100, message = "DIMENSION_TOO_LONG")
    private String dimensions;

    @Size(max = 50, message = "BARCODE_TOO_LONG")
    private String barcode;

    @NotNull(message = "IS_DEFAULT_NOT_NULL")
    private Boolean isDefault;

    @NotEmpty(message = "OPTION_VALUE_NOT_NULL")
    private List<@NotNull(message = "OPTION_VALUE_ID_NULL") Long> optionValueIds;
}

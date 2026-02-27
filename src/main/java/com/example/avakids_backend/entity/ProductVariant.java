package com.example.avakids_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(
        name = "product_variants",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_product_variant_sku",
                    columnNames = {"product_id", "sku"})
        },
        indexes = {
            @Index(name = "idx_variants_is_default", columnList = "product_id, is_default"),
            @Index(name = "idx_variants_sku", columnList = "sku"),
            @Index(name = "idx_variants_stock", columnList = "product_id, stock_quantity")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(name = "variant_name")
    private String variantName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Builder.Default
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Builder.Default
    @Column(name = "sold_count")
    private Integer soldCount = 0;

    private BigDecimal weight;

    private String dimensions;

    private String barcode;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "variant_option_values",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "option_value_id"))
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductOptionValue> optionValues = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductVariantImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InventoryTransaction> inventoryTransactions = new ArrayList<>();
}

package com.example.avakids_backend.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "product_variant_images",
        indexes = {
            @Index(name = "idx_variant_images_variant", columnList = "variant_id"),
            @Index(name = "idx_variant_images_primary", columnList = "variant_id, is_primary")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductVariant variant;
}

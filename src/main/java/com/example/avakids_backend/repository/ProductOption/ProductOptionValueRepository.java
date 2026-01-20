package com.example.avakids_backend.repository.ProductOption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.avakids_backend.entity.ProductOptionValue;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {

    List<ProductOptionValue> findByOptionIdOrderByDisplayOrderAsc(Long optionId);

    @Query(
            """
		SELECT ov
		FROM ProductOptionValue ov
		WHERE ov.id IN :ids
		AND ov.option.product.id = :productId
	""")
    List<ProductOptionValue> findAllByIdAndProductId(@Param("ids") List<Long> ids, @Param("productId") Long productId);
}

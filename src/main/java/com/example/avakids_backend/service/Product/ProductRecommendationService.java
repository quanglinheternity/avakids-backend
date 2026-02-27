package com.example.avakids_backend.service.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.entity.Category;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.repository.Order.OrderRepository;
import com.example.avakids_backend.repository.Product.ProductRepository;
import com.example.avakids_backend.repository.ProductImage.ProductImageRepository;
import com.example.avakids_backend.repository.User.UserRepository;
import com.example.avakids_backend.repository.Wishlist.WishlistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private static final int LIMIT = 20;
    private static final int MIN_STOCK = 5;

    public List<ProductResponse> recommendProducts(Long customerId, Long currentProductId, int limit) {

        // 1. Thu thập dữ liệu
        User customer = Optional.ofNullable(customerId)
                .flatMap(userRepository::findById)
                .orElse(null);

        Product currentProduct = Optional.ofNullable(currentProductId)
                .flatMap(productRepository::findById)
                .orElse(null);

        // 2. Lấy danh sách sản phẩm
        List<Product> candidates = getCandidateProducts(customer, currentProduct);
        //        log.info("Candidate products size = {}", candidates.size());
        //        log.info(
        //                "Candidate product ids = {}",
        //                candidates.stream().map(Product::getId).toList());

        // 3. Tính điểm sản phẩm
        Map<Product, Double> productScores = new HashMap<>();

        for (Product product : candidates) {
            double score = calculateProductScore(product, customer, currentProduct);
            productScores.put(product, score);
            log.info("Scoring productId={}, score={}", product.getId(), score);
        }
        log.info("Candidate productScores size = {}", productScores.size());

        List<Product> topProducts = productScores.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
        List<Long> productIds = topProducts.stream().map(Product::getId).toList();

        Map<Long, String> imageMap = productImageRepository.loadPrimaryImages(productIds);

        Set<Long> favoriteProductIds;

        if (customerId != null) {
            favoriteProductIds = new HashSet<>(wishlistRepository.findProductIdsByUserId(customerId));
        } else {
            favoriteProductIds = new HashSet<>();
        }
        return topProducts.stream()
                .map(product -> {
                    Category category = product.getCategory();

                    return ProductResponse.builder()
                            .id(product.getId())
                            .sku(product.getSku())
                            .name(product.getName())
                            .slug(product.getSlug())
                            .imageUlr(imageMap.get(product.getId()))
                            .categoryId(category != null ? category.getId() : null)
                            .categoryName(category != null ? category.getName() : null)
                            .description(product.getDescription())
                            .hasVariants(product.getHasVariants())
                            .price(product.getPrice())
                            .salePrice(product.getSalePrice())
                            .minPrice(product.getMinPrice())
                            .maxPrice(product.getMaxPrice())
                            .totalStock(product.getTotalStock())
                            .isActive(product.getIsActive())
                            .isFeatured(product.getIsFeatured())
                            .avgRating(product.getAvgRating())
                            .reviewCount(product.getReviewCount())
                            .soldCount(product.getSoldCount())
                            .isFavorite(favoriteProductIds.contains(product.getId()))
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    public List<Product> getCandidateProducts(User customer, Product currentProduct) {

        Set<Long> excludedIds = new HashSet<>();
        List<Product> result = new ArrayList<>();

        if (currentProduct != null) {
            excludedIds.add(currentProduct.getId());
        }

        //   1. Cùng category sản phẩm đang xem
        if (currentProduct != null && currentProduct.getCategory() != null) {
            List<Product> sameCategory = productRepository.findByCategoryAndExcludeIds(
                    currentProduct.getCategory().getId(), excludedIds, MIN_STOCK, LIMIT);
            log.info("[STEP 1] Found {} products", sameCategory.size());

            sameCategory.forEach(p -> log.info("  + productId={}, name={}", p.getId(), p.getName()));

            result.addAll(sameCategory);
            sameCategory.forEach(p -> excludedIds.add(p.getId()));
        }

        //   2. Category khách đã mua
        if (customer != null && result.size() < LIMIT) {
            Set<Long> purchasedCategoryIds = orderRepository.findPurchasedCategoryIds(customer.getId());

            if (!purchasedCategoryIds.isEmpty()) {
                List<Product> purchasedCategoryProducts = productRepository.findByCategoryIdsExcludeIds(
                        purchasedCategoryIds, excludedIds, MIN_STOCK, LIMIT - result.size());
                log.info("[STEP 2] Found {} products", purchasedCategoryProducts.size());

                purchasedCategoryProducts.forEach(p -> log.info("  + productId={}, name={}", p.getId(), p.getName()));
                result.addAll(purchasedCategoryProducts);
                purchasedCategoryProducts.forEach(p -> excludedIds.add(p.getId()));
            }
        }

        //          3. Sản phẩm phổ biến / best seller
        if (result.size() < LIMIT) {
            List<Product> popularProducts =
                    productRepository.findPopularExcludeIds(excludedIds, MIN_STOCK, LIMIT - result.size());

            log.info("[STEP 3] Found {} products", popularProducts.size());

            popularProducts.forEach(p -> log.info("  + productId={}, name={}", p.getId(), p.getName()));
            result.addAll(popularProducts);
            popularProducts.forEach(p -> excludedIds.add(p.getId()));
        }

        //         4. Fallback: còn hàng
        if (result.size() < LIMIT) {
            List<Product> fallback = productRepository.findAnyInStockExcludeIds(excludedIds, LIMIT - result.size());
            log.info("[STEP 4] Found {} products", fallback.size());

            fallback.forEach(p -> log.info("  + productId={}, name={}", p.getId(), p.getPrice()));
            result.addAll(fallback);
        }

        return result;
    }

    //      Tính điểm tổng hợp cho sản phẩm
    private double calculateProductScore(Product product, User customer, Product currentProduct) {

        double totalScore = 0.0;
        ScoringWeights weights = new ScoringWeights();

        // 1. Điểm lịch sử cá nhân (từ variant đã mua)
        if (customer != null) {
            double personalScore = calculatePersonalHistoryScore(product, customer);
            totalScore += personalScore * weights.PERSONAL_HISTORY;
        }

        // 2. Điểm độ phổ biến
        double popularityScore = calculatePopularityScore(product);
        totalScore += popularityScore * weights.POPULARITY;

        // 3. Điểm tồn kho
        double inventoryScore = calculateInventoryScore(product);
        totalScore += inventoryScore * weights.INVENTORY;

        // 4. Điểm phù hợp giá
        if (customer != null) {
            double priceScore = calculatePriceAffinityScore(product, customer);
            totalScore += priceScore * weights.PRICE_AFFINITY;
        }

        // 5. Điểm danh mục
        if (currentProduct != null) {
            double categoryScore = calculateCategoryScore(product, currentProduct);
            totalScore += categoryScore * weights.CATEGORY_MATCH;
        }

        // 6. Điểm sản phẩm nổi bật
        double featuredScore = product.getIsFeatured() ? 1.0 : 0.0;
        totalScore += featuredScore * weights.FEATURED;

        return totalScore;
    }

    private double calculatePersonalHistoryScore(Product product, User customer) {
        // 1. Kiểm tra khách hàng đã mua bất kỳ variant nào của product này chưa
        boolean hasPurchasedProduct =
                orderRepository.hasPurchasedAnyVariantOfProduct(customer.getId(), product.getId());
        if (hasPurchasedProduct) return 0.8;

        // 2. Đếm số lần mua sản phẩm trong cùng danh mục (qua các variant đã mua)
        if (product.getCategory() != null) {
            Long categoryId = product.getCategory().getId();
            int purchaseCount = orderRepository.countPurchasesInCategory(customer.getId(), categoryId);
            return Math.min(1.0, purchaseCount * 0.2);
        }

        return 0.0;
    }

    private double calculatePopularityScore(Product product) {
        int soldCount = product.getSoldCount() != null ? product.getSoldCount() : 0;
        BigDecimal rating = product.getAvgRating();
        int reviewCount = product.getReviewCount() != null ? product.getReviewCount() : 0;

        double salesScore = Math.min(soldCount / 1000.0, 1.0);

        double ratingScore = rating != null ? rating.doubleValue() / 5.0 : 0.3;

        double reviewScore = Math.min(reviewCount / 100.0, 1.0);

        return (salesScore * 0.4) + (ratingScore * 0.4) + (reviewScore * 0.2);
    }

    private double calculateInventoryScore(Product product) {
        int stock = product.getTotalStock() != null ? product.getTotalStock() : 0;

        // Có tồn kho nhưng không quá nhiều
        if (stock == 0) return 0.0;
        if (stock <= 10) return 1.0;
        if (stock <= 50) return 0.8;
        if (stock <= 100) return 0.5;
        return 0.2;
    }

    private double calculatePriceAffinityScore(Product product, User customer) {

        BigDecimal avgOrderValue = orderRepository.getAverageOrderValue(customer.getId());
        BigDecimal productPrice = product.getMinPrice();

        if (avgOrderValue == null || productPrice == null || avgOrderValue.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.5;
        }

        double ratio =
                productPrice.divide(avgOrderValue, 6, RoundingMode.HALF_UP).doubleValue();

        double distance = Math.abs(Math.log(ratio));

        // sigma: độ linh hoạt giá (0.7 là chuẩn ecommerce)
        double sigma = 0.7;

        double score = Math.exp(-(distance * distance) / (2 * sigma * sigma));

        return Math.max(0.0, Math.min(1.0, score));
    }

    private double calculateCategoryScore(Product product, Product currentProduct) {
        if (product.getCategory() == null || currentProduct.getCategory() == null) {
            return 0.0;
        }

        Long productCategoryId = product.getCategory().getId();
        Long currentCategoryId = currentProduct.getCategory().getId();

        return productCategoryId.equals(currentCategoryId) ? 1.0 : 0.0;
    }

    @Cacheable(value = "product_recommendations", key = "{#customerId, #currentProductId, #limit}")
    public List<ProductResponse> getCachedRecommendations(Long customerId, Long currentProductId, int limit) {
        return recommendProducts(customerId, currentProductId, limit);
    }

    private static class ScoringWeights {
        final double PERSONAL_HISTORY = 0.30;
        final double POPULARITY = 0.25;
        final double INVENTORY = 0.15;
        final double PRICE_AFFINITY = 0.15;
        final double CATEGORY_MATCH = 0.10;
        final double FEATURED = 0.05;
    }
}

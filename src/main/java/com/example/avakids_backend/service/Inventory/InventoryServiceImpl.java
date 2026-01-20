package com.example.avakids_backend.service.Inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.entity.InventoryTransaction;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.repository.inventory.InventoryTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Override
    public void increaseStock(ProductVariant variant, int quantity, String note, Order order) {
        if (quantity <= 0) {
            return;
        }

        applyTransaction(variant, quantity, InventoryTransaction.TransactionType.IN, note, order);
    }

    @Override
    public void decreaseStock(ProductVariant variant, int quantity, String note, Order order) {
        if (quantity <= 0) {
            return;
        }

        if (variant.getStockQuantity() < quantity) {
            return;
        }

        applyTransaction(variant, quantity, InventoryTransaction.TransactionType.OUT, note, order);
    }

    @Override
    public void adjustStock(ProductVariant variant, int newQuantity, String note) {
        if (newQuantity < 0) {
            return;
        }

        int current = variant.getStockQuantity();
        int diff = newQuantity - current;

        if (diff == 0) return;

        InventoryTransaction.TransactionType type = diff > 0
                ? InventoryTransaction.TransactionType.ADJUSTMENT_IN
                : InventoryTransaction.TransactionType.ADJUSTMENT_OUT;

        applyTransaction(variant, Math.abs(diff), type, note, null);
    }

    private void applyTransaction(
            ProductVariant variant, int quantity, InventoryTransaction.TransactionType type, String note, Order order) {
        int before = variant.getStockQuantity();
        int after;
        int productStockChange;

        Product product = variant.getProduct();

        switch (type) {
            case OUT, ADJUSTMENT_OUT -> {
                after = before - quantity;
                productStockChange = -quantity;
            }
            case IN, ADJUSTMENT_IN -> {
                after = before + quantity;
                productStockChange = quantity;
            }
            default -> throw new IllegalStateException("Invalid transaction type: " + type);
        }

        if (after < 0) {
            throw new IllegalStateException(String.format(
                    "Stock would become negative. Current: %d, Change: %d",
                    before,
                    type == InventoryTransaction.TransactionType.OUT
                                    || type == InventoryTransaction.TransactionType.ADJUSTMENT_OUT
                            ? -quantity
                            : quantity));
        }

        variant.setStockQuantity(after);

        product.setTotalStock(product.getTotalStock() + productStockChange);
        InventoryTransaction transaction = InventoryTransaction.builder()
                .variant(variant)
                .order(order)
                .transactionType(type)
                .quantity(quantity)
                .beforeQuantity(before)
                .afterQuantity(after)
                .note(note)
                .build();

        inventoryTransactionRepository.save(transaction);
    }
}

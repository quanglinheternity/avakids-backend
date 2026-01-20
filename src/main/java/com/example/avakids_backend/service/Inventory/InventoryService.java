package com.example.avakids_backend.service.Inventory;

import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.ProductVariant;

public interface InventoryService {

    void increaseStock(ProductVariant variant, int quantity, String note, Order order);

    void decreaseStock(ProductVariant variant, int quantity, String note, Order order);

    void adjustStock(ProductVariant variant, int newQuantity, String note);
}

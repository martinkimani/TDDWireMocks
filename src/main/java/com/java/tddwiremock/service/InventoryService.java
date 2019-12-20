package com.java.tddwiremock.service;

import com.java.tddwiremock.model.InventoryRecord;

import java.util.Optional;

public interface InventoryService {
    Optional<InventoryRecord> getInventoryRecord(Integer productId);
    Optional<InventoryRecord> purchaseProduct(Integer productId, Integer quantity);
}

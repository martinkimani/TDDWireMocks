/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.java.tddwiremock.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.java.tddwiremock.model.InventoryRecord;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 *
 * @author martin
 * 
 * in this class we have externalized the initial configs of the requests stubs to the mappings folder 
 * wiremock server when started will check the mappings folder and configure automatically 
 * this is a much cleaner way than posting doing it in code.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class InventoryServiceMappingsTest {
    
    @Autowired
    private InventoryService service;

    private WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        // Start the WireMock Server
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.stop();
    }
    
    @Test
    void testGetInventoryRecordsSuccess() {
        Optional<InventoryRecord> record = service.getInventoryRecord(1);
        Assertions.assertTrue(record.isPresent(), "InventoryRecord should be present");
        
        Assertions.assertEquals(500, record.get().getQuantity().intValue(), "quantity should be 500");
    }
    
    @Test
    void testGetInventoryRecordNotFound() {
        Optional<InventoryRecord> record = service.getInventoryRecord(2);
        Assertions.assertFalse(record.isPresent(), "InventoryRecord should not be present");
    }
    
    @Test
    void testPurchaseProductSuccess() {
        Optional<InventoryRecord> record = service.purchaseProduct(1, 1);
        Assertions.assertTrue(record.isPresent(), "InventoryRecord should be present");
        Assertions.assertEquals(499, record.get().getQuantity().intValue(), "the quantity should be 499");
    }
}

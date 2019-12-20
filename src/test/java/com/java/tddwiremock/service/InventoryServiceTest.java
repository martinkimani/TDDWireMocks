/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.java.tddwiremock.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.java.tddwiremock.model.InventoryRecord;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author martin
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class InventoryServiceTest {
    
    @Autowired
    InventoryService service;
    
    private WireMockServer wireMockServer;
    
    @BeforeEach
    void beforeEach() {
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        
        //configure requests
        wireMockServer.stubFor(get(urlEqualTo("/inventory/1"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("json/inventory-response.json")));
        
        wireMockServer.stubFor(get(urlEqualTo("/inventory/2"))
                .willReturn(aResponse().withStatus(404)));
        
        wireMockServer.stubFor(post("/inventory/1/purchaseRecord")
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(containing("\"productId\":1"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("json/inventory-response-after-post.json")));
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

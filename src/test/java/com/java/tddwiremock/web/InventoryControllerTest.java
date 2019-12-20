/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.java.tddwiremock.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.tddwiremock.model.InventoryRecord;
import com.java.tddwiremock.model.PurchaseRecord;
import com.java.tddwiremock.service.InventoryService;
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.doReturn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author martin
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {
    
    @MockBean
    InventoryService service;
    
    @Autowired
    MockMvc mockMvc;
    
    @Test
    @DisplayName("GET /inventory/id - success")
    void testGetInventoryById() throws Exception {
        InventoryRecord mockInventoryRecord = new InventoryRecord(1, 50, "product 1", "second hand");
        doReturn(Optional.of(mockInventoryRecord)).when(service).getInventoryRecord(1);
        
        mockMvc.perform(get("/inventory/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))
                
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("product 1")))
                .andExpect(jsonPath("$.quantity", is(50)))
                .andExpect(jsonPath("$.productCategory", is("second hand")));
    }
    
    @Test
    @DisplayName("GET /inventory/2 - Not Found")
    void testGetInventoryByIdNotFound() throws Exception {
        // Setup our mocked service
        doReturn(Optional.empty()).when(service).getInventoryRecord(2);

        // Execute the GET request
        mockMvc.perform(get("/inventory/{id}", 2))

                // Validate the response code is 404 Not Found
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /inventory/purchase-record - Success")
    void testCreatePurchaseRecord() throws Exception {
        // Setup mocked service
        InventoryRecord mockRecord = new InventoryRecord(1, 10,
                "Product 1", "Great Products");
        doReturn(Optional.of(mockRecord)).when(service).purchaseProduct(1, 5);


        mockMvc.perform(post("/inventory/purchase-record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new PurchaseRecord(1, 5))))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.productName", is("Product 1")))
                .andExpect(jsonPath("$.productCategory", is("Great Products")));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

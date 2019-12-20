/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.java.tddwiremock.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.java.tddwiremock.model.InventoryRecord;
import com.java.tddwiremock.model.PurchaseRecord;
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author martin
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class InventoryIntegrationTest {
    
    @Autowired
    MockMvc mockMvc;
    
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
    @DisplayName("GET /inventory/id - success")
    void testGetInventoryById() throws Exception {
        
        mockMvc.perform(get("/inventory/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))
                
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("Super Great Product")))
                .andExpect(jsonPath("$.quantity", is(500)))
                .andExpect(jsonPath("$.productCategory", is("Great Products")));
    }
    
    @Test
    @DisplayName("GET /inventory/2 - Not Found")
    void testGetInventoryByIdNotFound() throws Exception {
        
        mockMvc.perform(get("/inventory/{id}", 2))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /inventory/purchase-record - Success")
    void testCreatePurchaseRecord() throws Exception {

        mockMvc.perform(post("/inventory/purchase-record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new PurchaseRecord(1, 1))))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(499)))
                .andExpect(jsonPath("$.productName", is("Super Great Product")))
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

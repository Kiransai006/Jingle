package com.jingle.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Boots the full app against in-memory H2 (seeded by DataSeeder).
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class ApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listsSeededRestaurants() throws Exception {
        mvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void searchFiltersRestaurants() throws Exception {
        mvc.perform(get("/api/restaurants").param("search", "biryani"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nizam's Dum Biryani House"));
    }

    @Test
    void returnsMenuForRestaurant() throws Exception {
        mvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void unknownRestaurantReturns404() throws Exception {
        mvc.perform(get("/api/restaurants/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void placesOrder() throws Exception {
        String body = """
                {"customerName":"Kiran","phone":"0870000000","address":"Dublin 1",
                 "items":[{"menuItemId":1,"quantity":2}]}
                """;
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PLACED"));
    }

    @Test
    void rejectsInvalidOrder() throws Exception {
        String body = """
                {"customerName":"","phone":"","address":"","items":[]}
                """;
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void exposesPrometheusMetrics() throws Exception {
        mvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk());
    }
}
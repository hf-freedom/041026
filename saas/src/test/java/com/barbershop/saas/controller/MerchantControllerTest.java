package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private Merchant testMerchant;

    @BeforeEach
    void setUp() throws Exception {
        merchantRepository.deleteAll();

        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setShopName("Test Shop");
        request.setPhone("13800138000");
        request.setAddress("Test Address");
        request.setContactPerson("Test Person");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));

        testMerchant = merchantRepository.findByUsername("testuser").orElseThrow(() -> new RuntimeException("Merchant not found"));
        testMerchant.setStatus(Merchant.MerchantStatus.APPROVED);
        testMerchant = merchantRepository.save(testMerchant);

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = objectMapper.readTree(response).path("data").path("token").asText();
    }

    @Test
    void testRegister() throws Exception {
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setShopName("New Shop");
        request.setPhone("13900139000");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setShopName("Another Shop");
        request.setPhone("13900139001");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin() throws Exception {
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMerchantById() throws Exception {
        mockMvc.perform(get("/api/merchants/" + testMerchant.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/merchants/" + testMerchant.getId()))
                .andExpect(status().isForbidden());
    }
}

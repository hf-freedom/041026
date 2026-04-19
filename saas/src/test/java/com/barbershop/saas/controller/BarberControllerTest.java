package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BarberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        merchantRepository.deleteAll();

        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("barbertest");
        request.setPassword("password123");
        request.setShopName("Barber Test Shop");
        request.setPhone("13800138003");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Merchant merchant = merchantRepository.findByUsername("barbertest").orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantRepository.save(merchant);

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("barbertest");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = objectMapper.readTree(response).path("data").path("token").asText();
    }

    @Test
    @Order(1)
    void testCreateBarber() throws Exception {
        BarberRequest request = new BarberRequest();
        request.setName("理发师张三");
        request.setPhone("15100151001");
        request.setLevel(Barber.BarberLevel.SENIOR);
        request.setCommissionRate(new BigDecimal("0.35"));

        mockMvc.perform(post("/api/barbers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("理发师张三"))
                .andExpect(jsonPath("$.data.level").value("SENIOR"));
    }

    @Test
    @Order(2)
    void testGetBarbers() throws Exception {
        BarberRequest request = new BarberRequest();
        request.setName("理发师李四");
        request.setLevel(Barber.BarberLevel.JUNIOR);

        mockMvc.perform(post("/api/barbers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/barbers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(3)
    void testUpdateBarber() throws Exception {
        BarberRequest createRequest = new BarberRequest();
        createRequest.setName("理发师王五");
        createRequest.setLevel(Barber.BarberLevel.JUNIOR);

        MvcResult createResult = mockMvc.perform(post("/api/barbers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long barberId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        BarberRequest updateRequest = new BarberRequest();
        updateRequest.setName("理发师王五(升级)");
        updateRequest.setLevel(Barber.BarberLevel.SENIOR);
        updateRequest.setCommissionRate(new BigDecimal("0.40"));

        mockMvc.perform(put("/api/barbers/" + barberId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.level").value("SENIOR"));
    }
}

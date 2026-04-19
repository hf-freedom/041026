package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HairstyleControllerTest {

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
        request.setUsername("hairstyletest");
        request.setPassword("password123");
        request.setShopName("Hairstyle Test Shop");
        request.setPhone("13800138004");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Merchant merchant = merchantRepository.findByUsername("hairstyletest").orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantRepository.save(merchant);

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("hairstyletest");
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
    void testCreateHairstyleType() throws Exception {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("短发");
        request.setDescription("清爽短发造型");
        request.setImageUrl("http://example.com/short.jpg");

        mockMvc.perform(post("/api/hairstyles/types")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("短发"));
    }

    @Test
    @Order(2)
    void testCreateHairstyleOption() throws Exception {
        HairstyleOptionRequest request = new HairstyleOptionRequest();
        request.setName("渐变");
        request.setCategory("修剪方式");
        request.setDescription("两侧渐变效果");

        mockMvc.perform(post("/api/hairstyles/options")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("渐变"));
    }

    @Test
    @Order(3)
    void testGetHairstyleTypes() throws Exception {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("长发");

        mockMvc.perform(post("/api/hairstyles/types")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hairstyles/types")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
}

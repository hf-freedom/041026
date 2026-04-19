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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    private String token;
    private Long merchantId;

    @BeforeEach
    void setUp() throws Exception {
        merchantRepository.deleteAll();

        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("testuser2");
        request.setPassword("password123");
        request.setShopName("Test Shop 2");
        request.setPhone("13800138002");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Merchant merchant = merchantRepository.findByUsername("testuser2").orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchant = merchantRepository.save(merchant);
        merchantId = merchant.getId();

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("testuser2");
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
    void testCreateMember() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("张三");
        request.setPhone("15000150001");

        mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    @Order(2)
    void testGetMembers() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("李四");
        request.setPhone("15000150002");

        mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("李四"));
    }

    @Test
    @Order(3)
    void testRecharge() throws Exception {
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setName("王五");
        memberRequest.setPhone("15000150003");

        MvcResult createResult = mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long memberId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setMemberId(memberId);
        rechargeRequest.setAmount(new java.math.BigDecimal("100.00"));
        rechargeRequest.setRemark("首次充值");

        mockMvc.perform(post("/api/members/recharge")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.amount").value(100.00));
    }
}

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
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsumptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    private String token;
    private Long memberId;
    private Long barberId;

    @BeforeEach
    void setUp() throws Exception {
        merchantRepository.deleteAll();

        MerchantRegisterRequest merchantRequest = new MerchantRegisterRequest();
        merchantRequest.setUsername("consumptiontest");
        merchantRequest.setPassword("password123");
        merchantRequest.setShopName("Consumption Test Shop");
        merchantRequest.setPhone("13800138005");

        mockMvc.perform(post("/api/merchants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantRequest)))
                .andExpect(status().isOk());

        Merchant merchant = merchantRepository.findByUsername("consumptiontest").orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantRepository.save(merchant);

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("consumptiontest");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        token = objectMapper.readTree(response).path("data").path("token").asText();

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setName("消费测试会员");
        memberRequest.setPhone("15200152001");

        MvcResult memberResult = mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andReturn();

        memberId = objectMapper.readTree(memberResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setMemberId(memberId);
        rechargeRequest.setAmount(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/members/recharge")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isOk());

        BarberRequest barberRequest = new BarberRequest();
        barberRequest.setName("消费测试理发师");
        barberRequest.setLevel(Barber.BarberLevel.SENIOR);
        barberRequest.setCommissionRate(new BigDecimal("0.30"));

        MvcResult barberResult = mockMvc.perform(post("/api/barbers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(barberRequest)))
                .andExpect(status().isOk())
                .andReturn();

        barberId = objectMapper.readTree(barberResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(1)
    void testConsume() throws Exception {
        ConsumeRequest request = new ConsumeRequest();
        request.setMemberId(memberId);
        request.setBarberId(barberId);
        request.setOriginalAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(com.barbershop.saas.entity.ConsumptionRecord.PaymentMethod.BALANCE);
        request.setRemark("测试消费");

        mockMvc.perform(post("/api/consumptions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.actualAmount").exists());
    }

    @Test
    @Order(2)
    void testMatchBarber() throws Exception {
        MatchBarberRequest request = new MatchBarberRequest();
        request.setHairstyleOptionIds(Collections.emptyList());

        mockMvc.perform(post("/api/consumptions/match-barber")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    void testGetConsumptionRecords() throws Exception {
        ConsumeRequest consumeRequest = new ConsumeRequest();
        consumeRequest.setMemberId(memberId);
        consumeRequest.setBarberId(barberId);
        consumeRequest.setOriginalAmount(new BigDecimal("50.00"));
        consumeRequest.setPaymentMethod(com.barbershop.saas.entity.ConsumptionRecord.PaymentMethod.BALANCE);

        mockMvc.perform(post("/api/consumptions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consumeRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/consumptions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }
}

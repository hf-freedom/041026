package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/merchants")
@Tag(name = "商家管理", description = "商家申请、审核、登录等接口")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/register")
    @Operation(summary = "商家注册申请", description = "商家提交注册申请，等待审核")
    public ResponseEntity<ApiResponse<MerchantResponse>> register(@Valid @RequestBody MerchantRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("注册申请已提交，等待审核", merchantService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "商家登录", description = "商家登录获取Token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody MerchantLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(merchantService.login(request)));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待审核商家列表", description = "管理员获取待审核的商家列表")
    public ResponseEntity<ApiResponse<Page<MerchantResponse>>> getPendingMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                merchantService.getPendingMerchants(PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审核通过", description = "管理员审核通过商家申请")
    public ResponseEntity<ApiResponse<MerchantResponse>> approveMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("审核通过", merchantService.approveMerchant(id)));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "审核拒绝", description = "管理员拒绝商家申请")
    public ResponseEntity<ApiResponse<MerchantResponse>> rejectMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("已拒绝", merchantService.rejectMerchant(id)));
    }

    @PostMapping("/{id}/online")
    @Operation(summary = "商家上线", description = "将商家状态设为上线")
    public ResponseEntity<ApiResponse<MerchantResponse>> onlineMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("已上线", merchantService.onlineMerchant(id)));
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "商家下线", description = "将商家状态设为下线")
    public ResponseEntity<ApiResponse<MerchantResponse>> offlineMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("已下线", merchantService.offlineMerchant(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商家详情", description = "根据ID获取商家详情")
    public ResponseEntity<ApiResponse<MerchantResponse>> getMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(merchantService.getMerchantById(id)));
    }

    @GetMapping
    @Operation(summary = "获取所有商家列表", description = "管理员获取所有商家列表")
    public ResponseEntity<ApiResponse<Page<MerchantResponse>>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                merchantService.getAllMerchants(PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }
}

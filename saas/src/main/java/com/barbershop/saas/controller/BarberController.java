package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.service.BarberService;
import com.barbershop.saas.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/barbers")
@Tag(name = "理发师管理", description = "理发师增删改查、级别设置等接口")
public class BarberController {
    private final BarberService barberService;
    private final SecurityUtil securityUtil;

    public BarberController(BarberService barberService, SecurityUtil securityUtil) {
        this.barberService = barberService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    @Operation(summary = "创建理发师", description = "添加新理发师")
    public ResponseEntity<ApiResponse<BarberResponse>> createBarber(@Valid @RequestBody BarberRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("理发师创建成功", barberService.createBarber(merchantId, request)));
    }

    @GetMapping
    @Operation(summary = "获取理发师列表", description = "分页获取当前商家的理发师列表")
    public ResponseEntity<ApiResponse<Page<BarberResponse>>> getBarbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                barberService.getBarbers(merchantId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @GetMapping("/active")
    @Operation(summary = "获取活跃理发师列表", description = "获取当前商家所有活跃的理发师")
    public ResponseEntity<ApiResponse<java.util.List<BarberResponse>>> getActiveBarbers() {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(barberService.getActiveBarbers(merchantId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取理发师详情", description = "根据ID获取理发师详情")
    public ResponseEntity<ApiResponse<BarberResponse>> getBarberById(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(barberService.getBarberById(merchantId, id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新理发师", description = "更新理发师信息")
    public ResponseEntity<ApiResponse<BarberResponse>> updateBarber(
            @PathVariable Long id,
            @Valid @RequestBody BarberRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("更新成功", barberService.updateBarber(merchantId, id, request)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新理发师状态", description = "启用或禁用理发师")
    public ResponseEntity<ApiResponse<BarberResponse>> updateBarberStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", barberService.updateBarberStatus(merchantId, id, active)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除理发师", description = "删除理发师")
    public ResponseEntity<ApiResponse<Void>> deleteBarber(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        barberService.deleteBarber(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}

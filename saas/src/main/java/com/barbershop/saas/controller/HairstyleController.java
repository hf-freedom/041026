package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.service.HairstyleService;
import com.barbershop.saas.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/hairstyles")
@Tag(name = "发型管理", description = "发型类型和选项管理接口")
public class HairstyleController {
    private final HairstyleService hairstyleService;
    private final SecurityUtil securityUtil;

    public HairstyleController(HairstyleService hairstyleService, SecurityUtil securityUtil) {
        this.hairstyleService = hairstyleService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/types")
    @Operation(summary = "创建发型类型", description = "添加新的发型类型")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> createHairstyleType(@Valid @RequestBody HairstyleTypeRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("发型类型创建成功", hairstyleService.createHairstyleType(merchantId, request)));
    }

    @GetMapping("/types")
    @Operation(summary = "获取发型类型列表", description = "分页获取发型类型列表")
    public ResponseEntity<ApiResponse<Page<HairstyleTypeResponse>>> getHairstyleTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                hairstyleService.getHairstyleTypes(merchantId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @GetMapping("/types/active")
    @Operation(summary = "获取活跃发型类型列表", description = "获取所有活跃的发型类型")
    public ResponseEntity<ApiResponse<List<HairstyleTypeResponse>>> getActiveHairstyleTypes() {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(hairstyleService.getActiveHairstyleTypes(merchantId)));
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "获取发型类型详情", description = "根据ID获取发型类型详情")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> getHairstyleTypeById(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(hairstyleService.getHairstyleTypeById(merchantId, id)));
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "更新发型类型", description = "更新发型类型信息")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> updateHairstyleType(
            @PathVariable Long id,
            @Valid @RequestBody HairstyleTypeRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("更新成功", hairstyleService.updateHairstyleType(merchantId, id, request)));
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除发型类型", description = "删除发型类型")
    public ResponseEntity<ApiResponse<Void>> deleteHairstyleType(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        hairstyleService.deleteHairstyleType(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PutMapping("/types/{id}/toggle")
    @Operation(summary = "切换发型类型状态", description = "启用或禁用发型类型")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> toggleHairstyleTypeStatus(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("状态切换成功", hairstyleService.toggleHairstyleTypeStatus(merchantId, id)));
    }

    @PostMapping("/options")
    @Operation(summary = "创建发型选项", description = "添加新的发型选项")
    public ResponseEntity<ApiResponse<HairstyleOptionResponse>> createHairstyleOption(@Valid @RequestBody HairstyleOptionRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("发型选项创建成功", hairstyleService.createHairstyleOption(merchantId, request)));
    }

    @GetMapping("/options")
    @Operation(summary = "获取发型选项列表", description = "分页获取发型选项列表")
    public ResponseEntity<ApiResponse<Page<HairstyleOptionResponse>>> getHairstyleOptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                hairstyleService.getHairstyleOptions(merchantId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @GetMapping("/options/active")
    @Operation(summary = "获取活跃发型选项列表", description = "获取所有活跃的发型选项")
    public ResponseEntity<ApiResponse<List<HairstyleOptionResponse>>> getActiveHairstyleOptions() {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(hairstyleService.getActiveHairstyleOptions(merchantId)));
    }

    @GetMapping("/options/category/{category}")
    @Operation(summary = "按分类获取发型选项", description = "根据分类获取发型选项列表")
    public ResponseEntity<ApiResponse<List<HairstyleOptionResponse>>> getHairstyleOptionsByCategory(@PathVariable String category) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(hairstyleService.getHairstyleOptionsByCategory(merchantId, category)));
    }

    @GetMapping("/options/{id}")
    @Operation(summary = "获取发型选项详情", description = "根据ID获取发型选项详情")
    public ResponseEntity<ApiResponse<HairstyleOptionResponse>> getHairstyleOptionById(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(hairstyleService.getHairstyleOptionById(merchantId, id)));
    }

    @PutMapping("/options/{id}")
    @Operation(summary = "更新发型选项", description = "更新发型选项信息")
    public ResponseEntity<ApiResponse<HairstyleOptionResponse>> updateHairstyleOption(
            @PathVariable Long id,
            @Valid @RequestBody HairstyleOptionRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("更新成功", hairstyleService.updateHairstyleOption(merchantId, id, request)));
    }

    @DeleteMapping("/options/{id}")
    @Operation(summary = "删除发型选项", description = "删除发型选项")
    public ResponseEntity<ApiResponse<Void>> deleteHairstyleOption(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        hairstyleService.deleteHairstyleOption(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}

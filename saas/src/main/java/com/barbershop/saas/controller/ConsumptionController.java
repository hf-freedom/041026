package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.service.ConsumptionService;
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
@RequestMapping("/api/consumptions")
@Tag(name = "消费管理", description = "消费记录、发型匹配理发师等接口")
public class ConsumptionController {
    private final ConsumptionService consumptionService;
    private final SecurityUtil securityUtil;

    public ConsumptionController(ConsumptionService consumptionService, SecurityUtil securityUtil) {
        this.consumptionService = consumptionService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    @Operation(summary = "创建消费记录", description = "记录会员消费")
    public ResponseEntity<ApiResponse<ConsumptionRecordResponse>> consume(@Valid @RequestBody ConsumeRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("消费记录创建成功", consumptionService.consume(merchantId, request)));
    }

    @GetMapping
    @Operation(summary = "获取消费记录列表", description = "分页获取消费记录")
    public ResponseEntity<ApiResponse<Page<ConsumptionRecordResponse>>> getConsumptionRecords(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                consumptionService.getConsumptionRecords(merchantId, memberId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @GetMapping("/barber/{barberId}")
    @Operation(summary = "获取理发师的消费记录", description = "分页获取指定理发师的消费记录")
    public ResponseEntity<ApiResponse<Page<ConsumptionRecordResponse>>> getBarberConsumptionRecords(
            @PathVariable Long barberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                consumptionService.getBarberConsumptionRecords(merchantId, barberId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @PostMapping("/match-barber")
    @Operation(summary = "匹配理发师", description = "根据发型选择匹配最适合的理发师")
    public ResponseEntity<ApiResponse<List<MatchedBarberResponse>>> matchBarber(@RequestBody MatchBarberRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(consumptionService.matchBarber(merchantId, request)));
    }
}

package com.barbershop.saas.controller;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.service.MemberService;
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
@RequestMapping("/api/members")
@Tag(name = "会员管理", description = "会员录入、充值、消费等接口")
public class MemberController {
    private final MemberService memberService;
    private final SecurityUtil securityUtil;

    public MemberController(MemberService memberService, SecurityUtil securityUtil) {
        this.memberService = memberService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    @Operation(summary = "创建会员", description = "录入新会员")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody MemberRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("会员创建成功", memberService.createMember(merchantId, request)));
    }

    @GetMapping
    @Operation(summary = "获取会员列表", description = "分页获取当前商家的会员列表")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                memberService.getMembers(merchantId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取会员详情", description = "根据ID获取会员详情")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberById(merchantId, id)));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "根据手机号查询会员", description = "根据手机号查询会员信息")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByPhone(@PathVariable String phone) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberByPhone(merchantId, phone)));
    }

    @PostMapping("/recharge")
    @Operation(summary = "会员充值", description = "为会员充值")
    public ResponseEntity<ApiResponse<TransactionResponse>> recharge(@Valid @RequestBody RechargeRequest request) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("充值成功", memberService.recharge(merchantId, request)));
    }

    @GetMapping("/transactions")
    @Operation(summary = "获取交易记录", description = "获取会员交易记录")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success(
                memberService.getTransactions(merchantId, memberId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
        ));
    }

    @PutMapping("/{id}/level")
    @Operation(summary = "更新会员等级", description = "手动更新会员等级")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberLevel(
            @PathVariable Long id,
            @RequestParam Member.MemberLevel level) {
        Long merchantId = securityUtil.getCurrentMerchantId();
        return ResponseEntity.ok(ApiResponse.success("等级更新成功", memberService.updateMemberLevel(merchantId, id, level)));
    }
}

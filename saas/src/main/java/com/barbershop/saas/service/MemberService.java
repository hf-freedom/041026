package com.barbershop.saas.service;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.Transaction;
import com.barbershop.saas.repository.MemberRepository;
import com.barbershop.saas.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public MemberService(MemberRepository memberRepository, TransactionRepository transactionRepository) {
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public MemberResponse createMember(Long merchantId, MemberRequest request) {
        if (memberRepository.existsByMerchantIdAndPhone(merchantId, request.getPhone())) {
            throw new RuntimeException("该手机号已注册会员");
        }

        Member member = new Member();
        member.setMerchantId(merchantId);
        member.setName(request.getName());
        member.setPhone(request.getPhone());

        return MemberResponse.from(memberRepository.save(member));
    }

    public Page<MemberResponse> getMembers(Long merchantId, Pageable pageable) {
        return memberRepository.findByMerchantId(merchantId, pageable)
                .map(MemberResponse::from);
    }

    public MemberResponse getMemberById(Long merchantId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!member.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权访问该会员");
        }

        return MemberResponse.from(member);
    }

    public MemberResponse getMemberByPhone(Long merchantId, String phone) {
        Member member = memberRepository.findByMerchantIdAndPhone(merchantId, phone)
                .orElseThrow(() -> new RuntimeException("会员不存在"));
        return MemberResponse.from(member);
    }

    @Transactional
    public TransactionResponse recharge(Long merchantId, RechargeRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!member.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该会员");
        }

        BigDecimal newBalance = member.getBalance().add(request.getAmount());
        member.setBalance(newBalance);
        memberRepository.save(member);

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
        transaction.setMemberId(member.getId());
        transaction.setType(Transaction.TransactionType.RECHARGE);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setRemark(request.getRemark());
        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void consume(Long merchantId, Long memberId, BigDecimal amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!member.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该会员");
        }

        if (member.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        BigDecimal newBalance = member.getBalance().subtract(amount);
        member.setBalance(newBalance);
        member.setTotalConsumption(member.getTotalConsumption().add(amount));
        updateMemberLevel(member);
        memberRepository.save(member);

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
        transaction.setMemberId(member.getId());
        transaction.setType(Transaction.TransactionType.CONSUME);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transactionRepository.save(transaction);
    }

    private void updateMemberLevel(Member member) {
        BigDecimal total = member.getTotalConsumption();
        if (total.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            member.setLevel(Member.MemberLevel.DIAMOND);
        } else if (total.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            member.setLevel(Member.MemberLevel.PLATINUM);
        } else if (total.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            member.setLevel(Member.MemberLevel.GOLD);
        } else if (total.compareTo(BigDecimal.valueOf(500)) >= 0) {
            member.setLevel(Member.MemberLevel.SILVER);
        }
    }

    public Page<TransactionResponse> getTransactions(Long merchantId, Long memberId, Pageable pageable) {
        if (memberId != null) {
            return transactionRepository.findByMerchantIdAndMemberId(merchantId, memberId, pageable)
                    .map(TransactionResponse::from);
        }
        return transactionRepository.findByMerchantId(merchantId, pageable)
                .map(TransactionResponse::from);
    }

    @Transactional
    public MemberResponse updateMemberLevel(Long merchantId, Long memberId, Member.MemberLevel level) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!member.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该会员");
        }

        member.setLevel(level);
        return MemberResponse.from(memberRepository.save(member));
    }

    public BigDecimal calculateDiscountedAmount(Member member, BigDecimal originalAmount) {
        double discount = member.getLevel().getDiscount();
        return originalAmount.multiply(BigDecimal.valueOf(discount))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

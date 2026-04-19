package com.barbershop.saas.service;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.*;
import com.barbershop.saas.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {
    private final MemberRepository memberRepository;
    private final BarberRepository barberRepository;
    private final HairstyleTypeRepository hairstyleTypeRepository;
    private final HairstyleOptionRepository hairstyleOptionRepository;
    private final ConsumptionRecordRepository consumptionRecordRepository;
    private final TransactionRepository transactionRepository;
    private final MemberService memberService;
    private final BarberService barberService;

    public ConsumptionService(MemberRepository memberRepository,
                              BarberRepository barberRepository,
                              HairstyleTypeRepository hairstyleTypeRepository,
                              HairstyleOptionRepository hairstyleOptionRepository,
                              ConsumptionRecordRepository consumptionRecordRepository,
                              TransactionRepository transactionRepository,
                              MemberService memberService,
                              BarberService barberService) {
        this.memberRepository = memberRepository;
        this.barberRepository = barberRepository;
        this.hairstyleTypeRepository = hairstyleTypeRepository;
        this.hairstyleOptionRepository = hairstyleOptionRepository;
        this.consumptionRecordRepository = consumptionRecordRepository;
        this.transactionRepository = transactionRepository;
        this.memberService = memberService;
        this.barberService = barberService;
    }

    @Transactional
    public ConsumptionRecordResponse consume(Long merchantId, ConsumeRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        if (!member.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该会员");
        }

        Barber barber = barberRepository.findById(request.getBarberId())
                .orElseThrow(() -> new RuntimeException("理发师不存在"));

        if (!barber.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该理发师");
        }

        if (!barber.getActive()) {
            throw new RuntimeException("理发师已离职或不可用");
        }

        BigDecimal discountAmount = memberService.calculateDiscountedAmount(member, request.getOriginalAmount());
        BigDecimal actualAmount = discountAmount;

        if (request.getPaymentMethod() == ConsumptionRecord.PaymentMethod.BALANCE) {
            if (member.getBalance().compareTo(actualAmount) < 0) {
                throw new RuntimeException("会员余额不足");
            }
            member.setBalance(member.getBalance().subtract(actualAmount));
        }

        member.setTotalConsumption(member.getTotalConsumption().add(actualAmount));
        updateMemberLevel(member);
        memberRepository.save(member);

        BigDecimal commission = barberService.calculateCommission(barber, actualAmount);

        ConsumptionRecord record = new ConsumptionRecord();
        record.setMerchantId(merchantId);
        record.setMemberId(member.getId());
        record.setBarberId(barber.getId());
        record.setHairstyleTypeId(request.getHairstyleTypeId());
        record.setOriginalAmount(request.getOriginalAmount());
        record.setDiscountAmount(discountAmount);
        record.setActualAmount(actualAmount);
        record.setBarberCommission(commission);
        record.setPaymentMethod(request.getPaymentMethod());
        record.setRemark(request.getRemark());

        if (request.getHairstyleOptionIds() != null && !request.getHairstyleOptionIds().isEmpty()) {
            List<HairstyleOption> options = hairstyleOptionRepository.findAllById(request.getHairstyleOptionIds());
            record.setHairstyleOptions(options.stream()
                    .map(HairstyleOption::getName)
                    .collect(Collectors.joining(",")));
        }

        ConsumptionRecord savedRecord = consumptionRecordRepository.save(record);

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
        transaction.setMemberId(member.getId());
        transaction.setType(Transaction.TransactionType.CONSUME);
        transaction.setAmount(actualAmount);
        transaction.setBalanceAfter(member.getBalance());
        transaction.setRemark("消费记录ID: " + savedRecord.getId());
        transactionRepository.save(transaction);

        ConsumptionRecordResponse response = ConsumptionRecordResponse.from(savedRecord);
        response.setMemberName(member.getName());
        response.setBarberName(barber.getName());

        if (request.getHairstyleTypeId() != null) {
            hairstyleTypeRepository.findById(request.getHairstyleTypeId())
                    .ifPresent(type -> response.setHairstyleTypeName(type.getName()));
        }

        return response;
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

    public Page<ConsumptionRecordResponse> getConsumptionRecords(Long merchantId, Long memberId, Pageable pageable) {
        Page<ConsumptionRecord> records;
        if (memberId != null) {
            records = consumptionRecordRepository.findByMerchantIdAndMemberId(merchantId, memberId, pageable);
        } else {
            records = consumptionRecordRepository.findByMerchantId(merchantId, pageable);
        }

        return records.map(record -> {
            ConsumptionRecordResponse response = ConsumptionRecordResponse.from(record);
            memberRepository.findById(record.getMemberId())
                    .ifPresent(m -> response.setMemberName(m.getName()));
            barberRepository.findById(record.getBarberId())
                    .ifPresent(b -> response.setBarberName(b.getName()));
            if (record.getHairstyleTypeId() != null) {
                hairstyleTypeRepository.findById(record.getHairstyleTypeId())
                        .ifPresent(t -> response.setHairstyleTypeName(t.getName()));
            }
            return response;
        });
    }

    public Page<ConsumptionRecordResponse> getBarberConsumptionRecords(Long merchantId, Long barberId, Pageable pageable) {
        return consumptionRecordRepository.findByMerchantIdAndBarberId(merchantId, barberId, pageable)
                .map(record -> {
                    ConsumptionRecordResponse response = ConsumptionRecordResponse.from(record);
                    memberRepository.findById(record.getMemberId())
                            .ifPresent(m -> response.setMemberName(m.getName()));
                    barberRepository.findById(record.getBarberId())
                            .ifPresent(b -> response.setBarberName(b.getName()));
                    return response;
                });
    }

    public List<MatchedBarberResponse> matchBarber(Long merchantId, MatchBarberRequest request) {
        List<Barber> activeBarbers = barberRepository.findByMerchantIdAndActiveTrue(merchantId);

        if (activeBarbers.isEmpty()) {
            throw new RuntimeException("没有可用的理发师");
        }

        Map<Barber.BarberLevel, Integer> levelScores = new HashMap<>();
        levelScores.put(Barber.BarberLevel.CHIEF, 5);
        levelScores.put(Barber.BarberLevel.MASTER, 4);
        levelScores.put(Barber.BarberLevel.SENIOR, 3);
        levelScores.put(Barber.BarberLevel.INTERMEDIATE, 2);
        levelScores.put(Barber.BarberLevel.JUNIOR, 1);

        List<MatchedBarberResponse> matchedBarbers = activeBarbers.stream()
                .map(barber -> {
                    int score = levelScores.getOrDefault(barber.getLevel(), 1);
                    return MatchedBarberResponse.from(barber, score);
                })
                .sorted(Comparator.comparingInt(MatchedBarberResponse::getMatchScore).reversed())
                .collect(Collectors.toList());

        return matchedBarbers;
    }
}

package com.barbershop.saas.repository;

import com.barbershop.saas.entity.ConsumptionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumptionRecordRepository extends JpaRepository<ConsumptionRecord, Long> {
    Page<ConsumptionRecord> findByMerchantId(Long merchantId, Pageable pageable);
    
    Page<ConsumptionRecord> findByMerchantIdAndMemberId(Long merchantId, Long memberId, Pageable pageable);
    
    Page<ConsumptionRecord> findByMerchantIdAndBarberId(Long merchantId, Long barberId, Pageable pageable);
    
    List<ConsumptionRecord> findByBarberId(Long barberId);
}

package com.barbershop.saas.repository;

import com.barbershop.saas.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByMerchantId(Long merchantId, Pageable pageable);
    
    Page<Transaction> findByMerchantIdAndMemberId(Long merchantId, Long memberId, Pageable pageable);
    
    List<Transaction> findByMemberId(Long memberId);
}

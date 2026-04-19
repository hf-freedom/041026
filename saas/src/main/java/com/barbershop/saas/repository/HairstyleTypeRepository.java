package com.barbershop.saas.repository;

import com.barbershop.saas.entity.HairstyleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HairstyleTypeRepository extends JpaRepository<HairstyleType, Long> {
    Page<HairstyleType> findByMerchantId(Long merchantId, Pageable pageable);
    
    List<HairstyleType> findByMerchantIdAndActiveTrue(Long merchantId);
}

package com.barbershop.saas.repository;

import com.barbershop.saas.entity.HairstyleOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HairstyleOptionRepository extends JpaRepository<HairstyleOption, Long> {
    Page<HairstyleOption> findByMerchantId(Long merchantId, Pageable pageable);
    
    List<HairstyleOption> findByMerchantIdAndActiveTrue(Long merchantId);
    
    List<HairstyleOption> findByMerchantIdAndCategoryAndActiveTrue(Long merchantId, String category);
}

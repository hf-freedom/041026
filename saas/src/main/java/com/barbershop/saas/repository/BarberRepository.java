package com.barbershop.saas.repository;

import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.Barber.BarberLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {
    Page<Barber> findByMerchantId(Long merchantId, Pageable pageable);
    
    List<Barber> findByMerchantIdAndActiveTrue(Long merchantId);
    
    List<Barber> findByMerchantIdAndLevelAndActiveTrue(Long merchantId, BarberLevel level);
    
    List<Barber> findByMerchantIdAndActive(Long merchantId, Boolean active);
}

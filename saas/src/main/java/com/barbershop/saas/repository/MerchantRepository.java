package com.barbershop.saas.repository;

import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.entity.Merchant.MerchantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    Page<Merchant> findByStatus(MerchantStatus status, Pageable pageable);
    
    List<Merchant> findByStatusIn(List<MerchantStatus> statuses);
}

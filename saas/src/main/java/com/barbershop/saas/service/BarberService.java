package com.barbershop.saas.service;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.repository.BarberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarberService {
    private final BarberRepository barberRepository;

    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Transactional
    public BarberResponse createBarber(Long merchantId, BarberRequest request) {
        Barber barber = new Barber();
        barber.setMerchantId(merchantId);
        barber.setName(request.getName());
        barber.setPhone(request.getPhone());
        barber.setLevel(request.getLevel() != null ? request.getLevel() : Barber.BarberLevel.JUNIOR);
        barber.setCommissionRate(request.getCommissionRate() != null ? request.getCommissionRate() : BigDecimal.valueOf(0.3));
        barber.setActive(true);

        return BarberResponse.from(barberRepository.save(barber));
    }

    public Page<BarberResponse> getBarbers(Long merchantId, Pageable pageable) {
        return barberRepository.findByMerchantId(merchantId, pageable)
                .map(BarberResponse::from);
    }

    public BarberResponse getBarberById(Long merchantId, Long barberId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("理发师不存在"));

        if (!barber.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权访问该理发师");
        }

        return BarberResponse.from(barber);
    }

    @Transactional
    public BarberResponse updateBarber(Long merchantId, Long barberId, BarberRequest request) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("理发师不存在"));

        if (!barber.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该理发师");
        }

        barber.setName(request.getName());
        barber.setPhone(request.getPhone());
        if (request.getLevel() != null) {
            barber.setLevel(request.getLevel());
        }
        if (request.getCommissionRate() != null) {
            barber.setCommissionRate(request.getCommissionRate());
        }

        return BarberResponse.from(barberRepository.save(barber));
    }

    @Transactional
    public BarberResponse updateBarberStatus(Long merchantId, Long barberId, Boolean active) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("理发师不存在"));

        if (!barber.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该理发师");
        }

        barber.setActive(active);
        return BarberResponse.from(barberRepository.save(barber));
    }

    @Transactional
    public void deleteBarber(Long merchantId, Long barberId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException("理发师不存在"));

        if (!barber.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该理发师");
        }

        barberRepository.delete(barber);
    }

    public List<BarberResponse> getActiveBarbers(Long merchantId) {
        return barberRepository.findByMerchantIdAndActiveTrue(merchantId).stream()
                .map(BarberResponse::from)
                .collect(Collectors.toList());
    }

    public List<Barber> getActiveBarbersByLevel(Long merchantId, Barber.BarberLevel level) {
        return barberRepository.findByMerchantIdAndLevelAndActiveTrue(merchantId, level);
    }

    public BigDecimal calculateCommission(Barber barber, BigDecimal amount) {
        return amount.multiply(barber.getCommissionRate())
                .setScale(2, RoundingMode.HALF_UP);
    }
}

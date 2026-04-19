package com.barbershop.saas.service;

import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.HairstyleOption;
import com.barbershop.saas.entity.HairstyleType;
import com.barbershop.saas.repository.HairstyleOptionRepository;
import com.barbershop.saas.repository.HairstyleTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HairstyleService {
    private final HairstyleTypeRepository hairstyleTypeRepository;
    private final HairstyleOptionRepository hairstyleOptionRepository;

    public HairstyleService(HairstyleTypeRepository hairstyleTypeRepository, HairstyleOptionRepository hairstyleOptionRepository) {
        this.hairstyleTypeRepository = hairstyleTypeRepository;
        this.hairstyleOptionRepository = hairstyleOptionRepository;
    }

    @Transactional
    public HairstyleTypeResponse createHairstyleType(Long merchantId, HairstyleTypeRequest request) {
        HairstyleType type = new HairstyleType();
        type.setMerchantId(merchantId);
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setImageUrl(request.getImageUrl());
        type.setActive(true);

        return HairstyleTypeResponse.from(hairstyleTypeRepository.save(type));
    }

    public Page<HairstyleTypeResponse> getHairstyleTypes(Long merchantId, Pageable pageable) {
        return hairstyleTypeRepository.findByMerchantId(merchantId, pageable)
                .map(HairstyleTypeResponse::from);
    }

    public HairstyleTypeResponse getHairstyleTypeById(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("发型类型不存在"));

        if (!type.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权访问该发型类型");
        }

        return HairstyleTypeResponse.from(type);
    }

    @Transactional
    public HairstyleTypeResponse updateHairstyleType(Long merchantId, Long typeId, HairstyleTypeRequest request) {
        HairstyleType type = hairstyleTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("发型类型不存在"));

        if (!type.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该发型类型");
        }

        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setImageUrl(request.getImageUrl());

        return HairstyleTypeResponse.from(hairstyleTypeRepository.save(type));
    }

    @Transactional
    public void deleteHairstyleType(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("发型类型不存在"));

        if (!type.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该发型类型");
        }

        hairstyleTypeRepository.delete(type);
    }

    @Transactional
    public HairstyleTypeResponse toggleHairstyleTypeStatus(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("发型类型不存在"));

        if (!type.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该发型类型");
        }

        type.setActive(!type.getActive());
        return HairstyleTypeResponse.from(hairstyleTypeRepository.save(type));
    }

    @Transactional
    public HairstyleOptionResponse createHairstyleOption(Long merchantId, HairstyleOptionRequest request) {
        HairstyleOption option = new HairstyleOption();
        option.setMerchantId(merchantId);
        option.setName(request.getName());
        option.setCategory(request.getCategory());
        option.setDescription(request.getDescription());
        option.setActive(true);

        return HairstyleOptionResponse.from(hairstyleOptionRepository.save(option));
    }

    public Page<HairstyleOptionResponse> getHairstyleOptions(Long merchantId, Pageable pageable) {
        return hairstyleOptionRepository.findByMerchantId(merchantId, pageable)
                .map(HairstyleOptionResponse::from);
    }

    public HairstyleOptionResponse getHairstyleOptionById(Long merchantId, Long optionId) {
        HairstyleOption option = hairstyleOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("发型选项不存在"));

        if (!option.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权访问该发型选项");
        }

        return HairstyleOptionResponse.from(option);
    }

    @Transactional
    public HairstyleOptionResponse updateHairstyleOption(Long merchantId, Long optionId, HairstyleOptionRequest request) {
        HairstyleOption option = hairstyleOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("发型选项不存在"));

        if (!option.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该发型选项");
        }

        option.setName(request.getName());
        option.setCategory(request.getCategory());
        option.setDescription(request.getDescription());

        return HairstyleOptionResponse.from(hairstyleOptionRepository.save(option));
    }

    @Transactional
    public void deleteHairstyleOption(Long merchantId, Long optionId) {
        HairstyleOption option = hairstyleOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("发型选项不存在"));

        if (!option.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该发型选项");
        }

        hairstyleOptionRepository.delete(option);
    }

    public List<HairstyleOptionResponse> getActiveHairstyleOptions(Long merchantId) {
        return hairstyleOptionRepository.findByMerchantIdAndActiveTrue(merchantId).stream()
                .map(HairstyleOptionResponse::from)
                .collect(Collectors.toList());
    }

    public List<HairstyleOptionResponse> getHairstyleOptionsByCategory(Long merchantId, String category) {
        return hairstyleOptionRepository.findByMerchantIdAndCategoryAndActiveTrue(merchantId, category).stream()
                .map(HairstyleOptionResponse::from)
                .collect(Collectors.toList());
    }

    public List<HairstyleTypeResponse> getActiveHairstyleTypes(Long merchantId) {
        return hairstyleTypeRepository.findByMerchantIdAndActiveTrue(merchantId).stream()
                .map(HairstyleTypeResponse::from)
                .collect(Collectors.toList());
    }
}

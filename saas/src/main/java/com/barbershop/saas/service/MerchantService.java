package com.barbershop.saas.service;

import com.barbershop.saas.config.JwtTokenProvider;
import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.repository.MerchantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MerchantService(MerchantRepository merchantRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public MerchantResponse register(MerchantRegisterRequest request) {
        if (merchantRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        Merchant merchant = new Merchant();
        merchant.setUsername(request.getUsername());
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));
        merchant.setShopName(request.getShopName());
        merchant.setAddress(request.getAddress());
        merchant.setPhone(request.getPhone());
        merchant.setContactPerson(request.getContactPerson());
        merchant.setBusinessLicense(request.getBusinessLicense());
        merchant.setStatus(Merchant.MerchantStatus.PENDING);

        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    public LoginResponse login(MerchantLoginRequest request) {
        Merchant merchant = merchantRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), merchant.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (merchant.getStatus() == Merchant.MerchantStatus.PENDING) {
            throw new RuntimeException("账号待审核，请等待管理员审核");
        }

        if (merchant.getStatus() == Merchant.MerchantStatus.REJECTED) {
            throw new RuntimeException("账号审核未通过");
        }

        if (merchant.getStatus() == Merchant.MerchantStatus.OFFLINE) {
            throw new RuntimeException("店铺已下线，请联系管理员");
        }

        String token = jwtTokenProvider.generateToken(merchant.getId(), merchant.getUsername());
        return new LoginResponse(token, MerchantResponse.from(merchant));
    }

    public Page<MerchantResponse> getPendingMerchants(Pageable pageable) {
        return merchantRepository.findByStatus(Merchant.MerchantStatus.PENDING, pageable)
                .map(MerchantResponse::from);
    }

    @Transactional
    public MerchantResponse approveMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        if (merchant.getStatus() != Merchant.MerchantStatus.PENDING) {
            throw new RuntimeException("商家状态不允许审核");
        }

        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchant.setApprovedAt(LocalDateTime.now());
        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantResponse rejectMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        if (merchant.getStatus() != Merchant.MerchantStatus.PENDING) {
            throw new RuntimeException("商家状态不允许审核");
        }

        merchant.setStatus(Merchant.MerchantStatus.REJECTED);
        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantResponse onlineMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        if (merchant.getStatus() != Merchant.MerchantStatus.APPROVED && 
            merchant.getStatus() != Merchant.MerchantStatus.OFFLINE) {
            throw new RuntimeException("商家状态不允许上线");
        }

        merchant.setStatus(Merchant.MerchantStatus.ONLINE);
        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantResponse offlineMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        if (merchant.getStatus() != Merchant.MerchantStatus.ONLINE && 
            merchant.getStatus() != Merchant.MerchantStatus.APPROVED) {
            throw new RuntimeException("商家状态不允许下线");
        }

        merchant.setStatus(Merchant.MerchantStatus.OFFLINE);
        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    public MerchantResponse getMerchantById(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        return MerchantResponse.from(merchant);
    }

    public Page<MerchantResponse> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(MerchantResponse::from);
    }

    public List<Merchant> getActiveMerchants() {
        return merchantRepository.findByStatusIn(
                Arrays.asList(Merchant.MerchantStatus.ONLINE, Merchant.MerchantStatus.APPROVED)
        );
    }
}

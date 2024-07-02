package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.VoucherRequest;
import com.samoylenko.bookingservice.model.exception.VoucherNotFoundException;
import com.samoylenko.bookingservice.model.promotion.DiscountRequest;
import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import com.samoylenko.bookingservice.model.spec.VoucherSpecification;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherCreateDto;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import com.samoylenko.bookingservice.repository.VoucherRepository;
import com.samoylenko.bookingservice.service.utils.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PromotionService {
    private final VoucherRepository voucherRepository;
    private final CodeGenerator codeGenerator;
    private final ModelMapper modelMapper;

    public VoucherDto createVoucher(@Valid VoucherCreateDto createDto) {
        if (createDto.getCode() == null) {
            createDto.setCode(codeGenerator.getCode());
        }
        var entity = voucherRepository.save(VoucherEntity.builder()
                .type(createDto.getType())
                .status(VoucherStatus.ACTIVE)
                .code(createDto.getCode())
                .distributor(createDto.getDistributor())
                .routeId(createDto.getRouteId())
                .expiredAt(createDto.getExpiredAt())
                .discountAbsolute(createDto.getDiscountAbsolute())
                .discountPercent(createDto.getDiscountPercent())
                .count(0)
                .build());

        return modelMapper.map(entity, VoucherDto.class);
    }

    public VoucherDto getVoucherById(@NotBlank String id) {
        return voucherRepository.findById(id)
                .map(voucher -> modelMapper.map(voucher, VoucherDto.class))
                .orElseThrow(() -> new VoucherNotFoundException(id));
    }

    public List<VoucherDto> getVouchers(VoucherRequest request) {
        var spec = VoucherSpecification
                .withStatus(request.getStatus())
                .and(VoucherSpecification.withRoute(request.getRoute()));
        var sort = Sort.by("createdDate").descending();

        return voucherRepository.findAll(spec, sort).stream()
                .map(voucher -> modelMapper.map(voucher, VoucherDto.class))
                .toList();
    }

    public void applyVoucher(DiscountRequest discountRequest) {
        var voucher = voucherRepository.findByCode(discountRequest.getCode())
                .orElseThrow(() -> new EntityNotFoundException(discountRequest.getCode()));
        voucher.setCount(voucher.getCount() + 1);
        if (voucher.getType().equals(DiscountType.CERTIFICATE)) {
            voucher.setStatus(VoucherStatus.APPLIED);
        }
        voucherRepository.save(voucher);
    }

    public VoucherDto getVoucherByCode(String code) {
        if (code == null) return null;
        return voucherRepository.findByCode(code)
                .map(voucher -> modelMapper.map(voucher, VoucherDto.class))
                .orElse(null);
    }
}

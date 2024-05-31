package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.VoucherRequest;
import com.samoylenko.bookingservice.model.exception.VoucherNotFoundException;
import com.samoylenko.bookingservice.model.spec.VoucherSpecification;
import com.samoylenko.bookingservice.model.status.ValidateResult;
import com.samoylenko.bookingservice.model.status.VoucherStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherCreateDto;
import com.samoylenko.bookingservice.model.voucher.VoucherDto;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import com.samoylenko.bookingservice.model.voucher.VoucherType;
import com.samoylenko.bookingservice.repository.VoucherRepository;
import com.samoylenko.bookingservice.service.utils.CodeGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static java.time.Instant.now;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final CodeGenerator codeGenerator;
    private final ModelMapper modelMapper;

    public VoucherDto create(@Valid VoucherCreateDto createDto) {
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

    public VoucherDto getById(@NotBlank String id) {
        var voucher = getEntity(id);
        return modelMapper.map(voucher, VoucherDto.class);
    }

    private VoucherEntity getEntity(String id) {
        return voucherRepository.findById(id)
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

    public ValidateResult validate(@NotBlank String code, String routeId) {
        var voucher = voucherRepository.findByCode(code);
        if (voucher.isEmpty()) {
            return ValidateResult.builder().status(ValidateResult.Status.NOT_VALID).build();
        }
        if (!voucher.get().getStatus().equals(VoucherStatus.ACTIVE)) {
            return ValidateResult.builder().status(ValidateResult.Status.NOT_VALID).build();
        }
        if (voucher.get().getExpiredAt() != null && voucher.get().getExpiredAt().isBefore(now())) {
            return ValidateResult.builder().status(ValidateResult.Status.EXPIRED).build();
        }
        if (voucher.get().getRouteId() != null && !voucher.get().getRouteId().equals(routeId)) {
            return ValidateResult.builder().status(ValidateResult.Status.NOT_VALID).build();
        }

        return ValidateResult.builder()
                .status(ValidateResult.Status.VALID)
                .type(voucher.get().getType())
                .id(voucher.get().getId())
                .discountPercent(voucher.get().getDiscountPercent())
                .discountAbsolute(voucher.get().getDiscountAbsolute())
                .build();
    }


    public ValidateResult apply(@NotBlank String code, String routeId) {
        var result = validate(code, routeId);
        if (result.getStatus().equals(ValidateResult.Status.VALID)) {
            var voucher = getEntity(result.getId());

            voucher.setCount(voucher.getCount() + 1);
            if (voucher.getType().equals(VoucherType.PROMO_CODE)) {
                voucher.setStatus(VoucherStatus.APPLIED);
            }

            voucherRepository.save(voucher);
        }
        return result;
    }
}

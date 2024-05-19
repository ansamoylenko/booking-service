package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.WalkDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WalkService {
    public WalkDto createWalk(WalkDto walk) {
        return null;
    }

    public Page<WalkDto> getPageOfWalks(Pageable pageable) {
        return null;
    }

    public WalkDto getWalk(String id) {
        return null;
    }

    public WalkDto updateWalk(String id, WalkDto walk) {
        return null;
    }

    public void deleteWalk(String id) {

    }

    public Page<WalkDto> getOrdersByWalk(String id, PageRequest pageRequest) {
        return null;
    }
}

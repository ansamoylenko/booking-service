package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.RouteDto;
import com.samoylenko.bookingservice.model.dto.WalkDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    public RouteDto createRoute(RouteDto route) {
        return null;
    }

    public RouteDto getRouteById(String id) {
        return null;
    }

    public RouteDto updateRoute(String id, RouteDto route) {
        return null;
    }

    public void deleteRoute(String id) {
    }

    public List<RouteDto> getAllRoutes() {
        return null;
    }

    public Page<WalkDto> getWalksByRoute(String id, PageRequest pageRequest) {
        return null;
    }
}

package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.RouteEntity;

import java.util.List;

public interface RouteRepository extends BaseEntityRepository<RouteEntity> {
    List<RouteEntity> findAll();
}

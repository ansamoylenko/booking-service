package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.RouteEntity;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RouteRepository extends BaseEntityRepository<RouteEntity> {
    List<RouteEntity> findAll();

    List<RouteEntity> findAll(Sort sort);
}

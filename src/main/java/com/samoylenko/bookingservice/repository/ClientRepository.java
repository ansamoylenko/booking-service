package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.ClientEntity;

import java.util.Optional;

public interface ClientRepository extends BaseEntityRepository<ClientEntity> {
    Optional<ClientEntity> findByPhone(String phone);
}

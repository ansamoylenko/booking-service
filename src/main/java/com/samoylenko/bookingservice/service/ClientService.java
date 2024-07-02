package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import com.samoylenko.bookingservice.model.entity.ClientEntity;
import com.samoylenko.bookingservice.repository.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ClientEntity createIfNotExist(ClientCreateDto clientDto) {
        return clientRepository.findByPhone(clientDto.getPhone())
                .map(existingClient -> updateClient(existingClient, clientDto))
                .orElseGet(() -> createNewClient(clientDto));
    }

    private ClientEntity updateClient(ClientEntity clientEntity, ClientCreateDto clientDto) {
        log.info("Updating client {} to {}", clientEntity, clientDto);
        clientEntity.setFirstName(clientDto.getFirstName());
        clientEntity.setLastName(clientDto.getLastName());
        clientEntity.setEmail(clientDto.getEmail());
        clientEntity.setDateOfBirth(clientDto.getDateOfBirth());
        return clientRepository.save(clientEntity);
    }

    private ClientEntity createNewClient(ClientCreateDto clientDto) {
        log.info("Creating new client {}", clientDto);
        return clientRepository.save(ClientEntity.builder()
                .firstName(clientDto.getFirstName())
                .lastName(clientDto.getLastName())
                .email(clientDto.getEmail())
                .phone(clientDto.getPhone())
                .dateOfBirth(clientDto.getDateOfBirth())
                .build());
    }
}

package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.entity.ClientEntity;
import com.samoylenko.bookingservice.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ClientDto create(ClientCreateDto clientDto) {
        var client = clientRepository.save(ClientEntity.builder()
                .firstName(clientDto.getFirstName())
                .lastName(clientDto.getLastName())
                .email(clientDto.getEmail())
                .phone(clientDto.getPhone())
                .build());
        return modelMapper.map(client, ClientDto.class);
    }
}

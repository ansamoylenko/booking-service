package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.DefaultClientEntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ClientRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void save_shouldSaveContact() {
        var contact = DefaultClientEntityBuilder.of().build();

        var saved = repository.save(contact);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
    }

    @Test
    public void findById_shouldReturnContact() {
        var contact = DefaultClientEntityBuilder.of().build();
        var saved = repository.save(contact);

        var found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    public void deleteById_shouldDeleteContact() {
        var contact = DefaultClientEntityBuilder.of().build();
        var saved = repository.save(contact);

        repository.deleteById(saved.getId());

        var found = repository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}

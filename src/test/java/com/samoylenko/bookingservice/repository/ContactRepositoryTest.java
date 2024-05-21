package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.DefaultContactEntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContactRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ContactRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void save_shouldSaveContact() {
        var contact = DefaultContactEntityBuilder.of().build();

        var saved = repository.save(contact);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
    }

    @Test
    public void saveSave_withIncorrectPhone_shouldThrowException() {
        var contact = DefaultContactEntityBuilder.of().withPhone("123").build();

        assertThatThrownBy(
                () -> repository.save(contact)
        ).isInstanceOf(TransactionSystemException.class);
    }

    @Test
    public void findById_shouldReturnContact() {
        var contact = DefaultContactEntityBuilder.of().build();
        var saved = repository.save(contact);

        var found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    public void deleteById_shouldDeleteContact() {
        var contact = DefaultContactEntityBuilder.of().build();
        var saved = repository.save(contact);

        repository.deleteById(saved.getId());

        var found = repository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}

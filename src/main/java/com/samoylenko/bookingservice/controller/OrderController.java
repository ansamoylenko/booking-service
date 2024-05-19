package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.OrderDto;
import com.samoylenko.bookingservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Записи")
@AllArgsConstructor
public class OrderController {
    private final OrderService OrderDtoService;

    @Operation(summary = "Добавить новую запись")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createOrderDto(OrderDto OrderDto, UriComponentsBuilder uriBuilder) {
        var crated = OrderDtoService.createOrder(OrderDto);
        var location = uriBuilder.path("/api/v1/OrderDtos/{id}")
                .buildAndExpand(crated.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Получить страницу записей")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderDto> getOrders(PageRequest pageRequest) {
        return OrderDtoService.getOrders(pageRequest);
    }

    @Operation(summary = "Получить запись по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getOrderDto(@PathVariable String id) {
        return OrderDtoService.getOrderById(id);
    }

    @Operation(summary = "Обновить запись")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrderDto(@PathVariable String id, @RequestBody OrderDto OrderDto) {
        return OrderDtoService.updateOrder(id, OrderDto);
    }

    @Operation(summary = "Отметить запись как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderDto(@PathVariable String id) {
        OrderDtoService.deleteOrder(id);
    }
}

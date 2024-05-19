package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.WalkDto;
import com.samoylenko.bookingservice.service.WalkService;
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
@RequestMapping("/api/v1/walks")
@Tag(name = "Прогулки")
@AllArgsConstructor
public class WalkController {
    private final WalkService walkService;

    @Operation(summary = "Добавить новую прогулку")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addWalk(@RequestBody WalkDto walk, UriComponentsBuilder uriBuilder) {
        var created = walkService.createWalk(walk);
        var location = uriBuilder.path("/api/v1/walks/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Получить страницу с прогулками")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getPageOfWalks(PageRequest pageRequest) {
        return walkService.getPageOfWalks(pageRequest);
    }

    @Operation(summary = "Получить прогулку по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalkDto getWalk(@PathVariable String id) {
        return walkService.getWalk(id);
    }

    @Operation(summary = "Обновить параметры прогулки")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalkDto updateWalk(@PathVariable String id, @RequestBody WalkDto walk) {
        return walkService.updateWalk(id, walk);
    }

    @Operation(summary = "Пометить прогулку как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWalk(@PathVariable String id) {
        walkService.deleteWalk(id);
    }

    @Operation(summary = "Получить записи по прогулке")
    @GetMapping(value = "/{id}/OrderDtos", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkDto> getWalkOrderDtos(@PathVariable String id, PageRequest pageRequest) {
        return walkService.getOrdersByWalk(id, pageRequest);
    }
}

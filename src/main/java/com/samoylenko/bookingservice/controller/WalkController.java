package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.WalkAdminDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUserDto;
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
    public ResponseEntity<WalkAdminDto> addWalk(@RequestBody WalkCreateDto walk, UriComponentsBuilder uriBuilder) {
        var created = walkService.createWalk(walk);
        var location = uriBuilder.path("/api/v1/walks/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Получить страницу с прогулками")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkAdminDto> getPageOfWalks(WalkRequest pageRequest) {
        return walkService.getWalksForAdmin(pageRequest);
    }

    @Operation(summary = "Получить прогулку по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalkUserDto getWalk(@PathVariable String id) {
        return walkService.getWalkForUser(id);
    }

    @Operation(summary = "Обновить параметры прогулки")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WalkAdminDto updateWalk(@PathVariable String id, @RequestBody WalkUpdateDto walk) {
        return walkService.updateWalk(id, walk);
    }

    @Operation(summary = "Пометить прогулку как удаленную")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWalk(@PathVariable String id) {
        walkService.markDeleted(id);
    }

    @Operation(summary = "Получить записи по прогулке")
    @GetMapping(value = "/{id}/OrderDtos", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<WalkUserDto> getWalkOrderDtos(@PathVariable String id, PageRequest pageRequest) {
        return walkService.getOrdersByWalk(id, pageRequest);
    }
}

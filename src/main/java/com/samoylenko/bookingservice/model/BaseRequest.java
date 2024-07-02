package com.samoylenko.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseRequest {
    protected Integer pageNumber;
    protected Integer pageSize;

    public PageRequest getPageRequest() {
        return PageRequest.of(
                pageNumber == null ? 0 : pageNumber,
                pageSize == null ? 10 : pageSize,
                Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
    }
}

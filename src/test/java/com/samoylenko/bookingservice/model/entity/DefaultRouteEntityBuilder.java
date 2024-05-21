package com.samoylenko.bookingservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultRouteEntityBuilder implements DefaultEntityBuilder<RouteEntity> {
    private String name = "testRoute";
    private String description = "testDescription";
    private Integer priceForOne = 100;

    @Override
    public RouteEntity build() {
        return RouteEntity.builder()
                .name(name)
                .description(description)
                .priceForOne(priceForOne)
                .build();
    }
}

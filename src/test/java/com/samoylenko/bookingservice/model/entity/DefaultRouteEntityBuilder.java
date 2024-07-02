package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.route.RouteEntity;
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
    private String serviceName = "testServiceName";

    @Override
    public RouteEntity build() {
        return RouteEntity.builder()
                .name(name)
                .description(description)
                .priceForOne(priceForOne)
                .serviceName(serviceName)
                .build();
    }
}

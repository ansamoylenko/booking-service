package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultWalkEntityBuilder;
import com.samoylenko.bookingservice.model.entity.WalkEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private WalkRepository walkRepository;

    @BeforeEach
    public void setUp() {
        routeRepository.deleteAll();
        walkRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveRoute() {
        var route = DefaultRouteEntityBuilder.of().build();

        var saved = routeRepository.save(route);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPriceForOne()).isPositive();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
    }

    @Test
    public void getById_shouldReturnRoute() {
        var route = DefaultRouteEntityBuilder.of().build();
        var saved = routeRepository.save(route);

        var found = routeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(saved).isEqualTo(found.get());
    }

    @Test
    public void getAll_shouldReturnAllRoutes() {
        var route1 = DefaultRouteEntityBuilder.of().build();
        var route2 = DefaultRouteEntityBuilder.of().build();
        var saved1 = routeRepository.save(route1);
        var saved2 = routeRepository.save(route2);

        var found = routeRepository.findAll();

        assertThat(found).isNotNull();
        assertThat(found).hasSize(2);
        assertThat(found).contains(saved1, saved2);
    }

    @Test
    public void save_shouldChangeVersion() {
        var route = DefaultRouteEntityBuilder.of().build();
        var saved = routeRepository.save(route);

        saved.setDescription("changed");
        var secondSaved = routeRepository.save(saved);

        assertThat(saved).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(0);
        assertThat(secondSaved).isNotNull();
        assertThat(secondSaved.getVersion()).isEqualTo(1);
    }

    @Test
    public void save_shouldChangeLastModifiedDate() {
        var route = DefaultRouteEntityBuilder.of().build();
        var saved = routeRepository.save(route);

        saved.setDescription("changed");
        var secondSaved = routeRepository.save(saved);

        assertThat(saved).isNotNull();
        assertThat(secondSaved).isNotNull();
        assertThat(saved.getLastModifiedDate()).isBefore(secondSaved.getLastModifiedDate());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete_shouldExistWalk_ifRemoveWalkFromList() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var walks = new ArrayList<WalkEntity>();
        walks.add(savedWalk1);
        walks.add(savedWalk2);
        savedRoute.setWalks(walks);
        routeRepository.save(savedRoute);

        var foundRoute = routeRepository.findById(savedRoute.getId());
        assertThat(foundRoute).isPresent();
        foundRoute.get().getWalks().remove(savedWalk1);

        var foundWalks = walkRepository.findAll();
        assertThat(foundWalks).hasSize(2);
    }

    @Test
    void remove_shouldRemoveRouteAndWalks() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());

        var walksBefore = walkRepository.findAll();
        routeRepository.deleteById(savedRoute.getId());
        var walksAfter = walkRepository.findAll();

        assertThat(walksBefore).hasSize(2);
        assertThat(walksAfter).isEmpty();
    }
}


package gatling;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static gatling.AuthBuilderUtils.auth;
import static gatling.RouteBuilderUtils.*;
import static gatling.WalkBuilderUtils.createWalk;
import static gatling.WalkBuilderUtils.getWalksByAdmin;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RouteManagementSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8081/api/v1")
//    HttpProtocolBuilder httpProtocol = http.baseUrl("http://178.154.205.74:8080/api/v1")

            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    FeederBuilder<String> feeder = csv("routes.csv").circular();
    FeederBuilder<String> walksFeeder = csv("walks.csv").shuffle().circular();


    ScenarioBuilder routeCreating = scenario("Создание маршрутов, добавление прогулок")
            .exec(auth())
            .feed(feeder)
            .exec(
                    createRoute(),
                    pause(1),
                    getRoute(),
                    repeat(10).on(
                            feed(walksFeeder),
                            exec(createWalk())
//                            repeat(5).on(
//                                    exec(createBooking())
//                            )
                    ),
                    getWalksByAdmin(),
                    pause(1),
                    updateRoute(),
                    pause(1),
                    getAllRoutes(),
                    pause(1)
            );

    ScenarioBuilder walksAdding = scenario("Добавление прогулок");

    ScenarioBuilder deleteAllRoutes = scenario("Удаление всех маршрутов")
            .exec(auth())
            .exec(
                    http("Удаление всех маршрутов")
                            .delete("/dev/routes")
                            .check(status().is(200))
            );

    ScenarioBuilder checkDeletedRoutes = scenario("Проверка что все маршруты удалены")
            .exec(
                    http("Получение всех маршрутов")
                            .get("/routes")
                            .check(
                                    status().is(200),
                                    jsonPath("$[*]").count().is(0)
                            )
            );

    {
        setUp(
                deleteAllRoutes.injectOpen(atOnceUsers(1)),
                routeCreating.injectOpen(
                        rampUsers(5).during(10)
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile4().lt(1500),
                        global().successfulRequests().percent().gt(90d)
                );
    }


}

package gatling;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RouteBuilderUtils {
    public static HttpRequestActionBuilder createRoute() {
        return http("Создание маршрута")
                .post("/admin/routes")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(StringBody("{ \"name\": \"#{name}\", \"description\": \"#{description}\", \"priceForOne\": #{price}, \"serviceName\": \"Катание на сапах\"}"))
                .check(
                        status().is(201),
                        jsonPath("$.id").exists().saveAs("routeId"),
                        jsonPath("$.name").exists()
                );
    }

    public static HttpRequestActionBuilder updateRoute() {
        return http("Обновление маршрута")
                .patch("/admin/routes/#{routeId}")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(StringBody("{ \"name\": \"#{updated_name}\", \"description\": \"#{updated_description}\", \"priceForOne\": #{updated_price} }"))
                .check(
                        status().is(200),
                        jsonPath("$.id").isEL("#{routeId}"),
                        jsonPath("$.name").isEL("#{updated_name}"),
                        jsonPath("$.description").isEL("#{updated_description}"),
                        jsonPath("$.priceForOne").isEL("#{updated_price}")
                );
    }

    public static HttpRequestActionBuilder getRoute() {
        return http("Получение маршрута")
                .get("/routes/#{routeId}")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .check(
                        status().is(200),
                        jsonPath("$.id").isEL("#{routeId}"),
                        jsonPath("$.name").isEL("#{name}"),
                        jsonPath("$.description").isEL("#{description}"),
                        jsonPath("$.priceForOne").isEL("#{price}")
                );
    }

    public static HttpRequestActionBuilder getAllRoutes() {
        return http("Получение всех маршрутов")
                .get("/routes")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .check(
                        status().is(200),
                        jsonPath("$[*]").count().gt(0)
                );
    }

    public static HttpRequestActionBuilder deleteRoute() {
        return http("Удаление маршрута")
                .delete("/routes/#{routeId}")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .check(
                        status().is(204)
                );
    }
}

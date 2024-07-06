package gatling;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class WalkBuilderUtils {
    public static HttpRequestActionBuilder createWalk() {
        return http("Добавление прогулки")
                .post("/admin/walks")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(StringBody("""
                        {
                          "routeId": "#{routeId}",
                          "maxPlaces": #{maxPlaces},
                          "priceForOne": #{priceForOne},
                          "startTime": "#{startTime}",
                          "durationInMinutes": 120
                        }
                        """))
                .check(
                        status().is(201),
                        jsonPath("$.id").exists().saveAs("walkId")
                );

    }

    public static HttpRequestActionBuilder getWalksByAdmin() {
        return http("Получение прогулки по фильтру")
                .get("/admin/walks")
                .queryParam("routeId", "#{routeId}")
                //.queryParam("status", "Запись активна")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .check(
                        status().is(200),
                        jsonPath("$.numberOfElements").ofInt().is(10)
                );
    }
}

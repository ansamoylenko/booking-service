package gatling;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AuthBuilderUtils {
    public static HttpRequestActionBuilder auth() {
        return http("Авторизация")
                .post("/admin/auth/login")
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("username", "owner")
                .formParam("password", "owner")
                .check(
                        status().is(200),
                        jsonPath("$.authenticated").exists()
                );
    }
}

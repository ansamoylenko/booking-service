package gatling;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BookingBuilderUtils {
    public static HttpRequestActionBuilder createBooking() {
        return http("Бронирование")
                .post("/bookings")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(StringBody("""
                        {
                          "walkId": "#{walkId}",
                          "numberOfPeople": 1,
                          "client": {
                            "firstName": "Иван",
                            "lastName": "Иванов",
                            "email": "ivan.ivanov@gmail.com",
                            "phone": "79999999999"
                          },
                          "bookingInfo": {
                            "comment": " какой-то комментарий",
                            "hasChildren": false,
                            "agreementConfirmed": true
                          }
                        }
                        """))
                .check(
                        status().is(201),
                        jsonPath("$.id").exists().saveAs("bookingId")
                );
    }
}

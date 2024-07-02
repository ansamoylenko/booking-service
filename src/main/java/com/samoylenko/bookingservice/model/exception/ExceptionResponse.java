package com.samoylenko.bookingservice.model.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private LocalDateTime timestamp;
    private String path;
    private String method;
    private Integer status;
    private String message;
    private String cause;
    private String rootCause;

    @Override
    public String toString() {
        return "Exception {\n" +
               "   timestamp: " + timestamp + '\n' +
               "   path: " + path + '\n' +
               "   method: " + method + '\n' +
               "   status: " + status + "\n" +
               "   message: " + message + '\n' +
               "   cause: " + cause + '\n' +
               "   rootCause:  " + rootCause + '\n' +
               '}';
    }
}

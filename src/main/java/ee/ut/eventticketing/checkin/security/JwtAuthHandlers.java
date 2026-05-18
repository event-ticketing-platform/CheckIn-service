package ee.ut.eventticketing.checkin.security;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class JwtAuthHandlers {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtAuthHandlers() {
    }

    public static AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, exception) ->
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "Missing or invalid JWT token");
    }

    public static AccessDeniedHandler forbiddenHandler() {
        return (request, response, exception) ->
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                        "You do not have permission to access this resource");
    }

    private static void writeError(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);

        MAPPER.writeValue(response.getOutputStream(), body);
    }
}

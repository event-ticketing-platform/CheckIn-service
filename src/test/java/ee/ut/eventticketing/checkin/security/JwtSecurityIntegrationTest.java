package ee.ut.eventticketing.checkin.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JwtSecurityIntegrationTest {

    private static final String EVENT_ID = "aaaa1111-1111-1111-1111-111111111111";
    private static final String CHECK_IN_ID = "00000000-0000-0000-0000-000000000099";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/checkin/checkins/" + CHECK_IN_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void publicAttendanceWithoutTokenReturns200() throws Exception {
        mockMvc.perform(get("/api/checkin/events/" + EVENT_ID + "/attendance"))
                .andExpect(status().isOk());
    }

    @Test
    void adminOnlyReverseWithUserTokenReturns403() throws Exception {
        mockMvc.perform(patch("/api/checkin/checkins/" + CHECK_IN_ID + "/reverse")
                        .header("Authorization", "Bearer " + JwtTestTokens.create("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void userCanAccessProtectedReadEndpoint() throws Exception {
        mockMvc.perform(get("/api/checkin/checkins/" + CHECK_IN_ID)
                        .header("Authorization", "Bearer " + JwtTestTokens.create("USER")))
                .andExpect(status().isNotFound());
    }
}

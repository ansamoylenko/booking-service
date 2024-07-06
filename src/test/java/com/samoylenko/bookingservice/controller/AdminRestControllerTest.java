package com.samoylenko.bookingservice.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminRestControllerTest {
    MockMvc mockMvc;

    @BeforeEach
    public void setUp(final ApplicationContext context) {
        this.mockMvc = context.getBean(MockMvc.class);
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.authorities").value("Администратор"));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/admin/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/admin/auth/login?logout=true"));
    }

    @Test
    public void getAdminWalks_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/walks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAdminWalks_withAdminAuthorization_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/admin/walks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void addWalk_shouldReturnForbidden() throws Exception {
        var body = """
                {
                  "routeId": "69472cd9-f395-4064-ba87-c7d5192dfe7f",
                  "maxPlaces": 10,
                  "priceForOne": 1000,
                  "startTime": "2024-07-05T21:39:14.494Z",
                  "durationInMinutes": 150
                }
                """;
        mockMvc.perform(post("/api/v1/admin/walks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserWalks_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/walks"))
                .andExpect(status().isOk());
    }

}

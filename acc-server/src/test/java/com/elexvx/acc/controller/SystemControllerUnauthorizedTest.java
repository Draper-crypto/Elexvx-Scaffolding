package com.elexvx.acc.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SystemControllerUnauthorizedTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void menus_should_return_401_when_not_logged_in() throws Exception {
        mockMvc.perform(get("/api/system/menus"))
                .andExpect(status().isUnauthorized());
    }
}


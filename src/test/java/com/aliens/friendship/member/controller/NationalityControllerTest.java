package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.config.jwt.JwtAuthenticationFilter;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.service.NationalityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NationalityController.class)
class NationalityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NationalityService nationalityService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("국적 목록 요청 성공")
    void GetNationalities_Success() throws Exception {
        // given
        Nationality nationality1 = new Nationality(1, "USA");
        Nationality nationality2 = new Nationality(2, "Korea");
        List<Nationality> nationalities = Arrays.asList(nationality1, nationality2);
        Map<String, Object> nationalitiesMap = new HashMap<>();
        nationalitiesMap.put("nationalities", nationalities);
        when(nationalityService.getNationalities()).thenReturn(nationalitiesMap);

        // when & then
        mockMvc.perform(get("/api/v1/member/nationalities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.nationalities.*", hasSize(nationalities.size())));
        verify(nationalityService, times(1)).getNationalities();
    }
}
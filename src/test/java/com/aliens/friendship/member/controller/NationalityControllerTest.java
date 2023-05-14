package com.aliens.friendship.member.controller;

import com.aliens.friendship.domain.member.controller.NationalityController;
import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.service.NationalityService;
import com.aliens.friendship.global.config.jwt.JwtAuthenticationFilter;
import com.aliens.friendship.global.response.ResponseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @MockBean
    private ResponseService responseService;

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
                .andExpect(status().isOk());
        verify(nationalityService, times(1)).getNationalities();
    }
}
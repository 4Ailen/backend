package com.aliens.friendship.member.service;

import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.NationalityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NationalityServiceTest {
    @InjectMocks
    private NationalityService nationalityService;

    @Mock
    private NationalityRepository nationalityRepository;

    @Test
    @DisplayName("국적 목록 조회 성공")
    void GetNationalities_Success_WhenValidDataExist() {
        //given: 국적 목록 존재
        Nationality nationality1 = new Nationality(1, "USA");
        Nationality nationality2 = new Nationality(2, "Korea");
        List<Nationality> expectedNationalities = Arrays.asList(nationality1, nationality2);
        when(nationalityRepository.findAll()).thenReturn(expectedNationalities);

        //when: 국적 목록 조회
        Map<String, Object> result = nationalityService.getNationalities();

        //then: 국적 목록 조회 성공
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsKey("nationalities");
        assertThat(result.get("nationalities")).isEqualTo(expectedNationalities);
    }

    @Test
    @DisplayName("국적 목록 조회 예외: DB에 국적 목록이 없는 경우")
    void GetNationalities_ThrowException_WhenValidDataNotExist() {
        //given: 국적 목록 존재하지 않음
        when(nationalityRepository.findAll()).thenReturn(null);

        //when: 국적 목록 조회
        Exception exception = assertThrows(Exception.class, () -> {
            nationalityService.getNationalities();
        });

        //then: 국적 목록 조회 실패
        assertEquals("국적 목록이 데이터베이스에 없습니다.", exception.getMessage());
    }
}
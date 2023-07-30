package com.aliens.friendship.member.business;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
import com.aliens.friendship.domain.member.business.MemberBusiness;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoByAdminResponseDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoResponseDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.converter.MemberConverter;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.member.service.ProfileImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberBusinessTest {

    @Mock
    private MemberService memberService;

    @Mock
    private MemberConverter memberConverter;

    @Mock
    private EmailAuthenticationService emailAuthenticationService;

    @Mock
    private ProfileImageService profileImageService;

    @InjectMocks
    private MemberBusiness memberBusiness;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    public void testJoin() throws Exception {
        // given
        JoinRequestDto joinRequestDto = JoinRequestDto.builder()
                .birthday("1998-11-25")
                .email("test@example.com")
                .gender("male")
                .mbti(MemberEntity.Mbti.ENFJ)
                .nationality("KOREA")
                .password("1234").build();
        MultipartFile profileImage = new MockMultipartFile("file", "profile_image.jpg", "image/jpeg", new byte[]{});
        joinRequestDto.setProfileImage(profileImage);

        // when
        when(memberService.checkDuplicatedAndWithdrawnInAWeekEmail(anyString())).thenReturn(false);
        when(memberService.createTemporaryPassword()).thenReturn("temp123");
        when(memberService.register(any(MemberEntity.class))).thenReturn(2L);
        doNothing().when(emailAuthenticationService).checkEmailAuthentication(anyString());
        when(profileImageService.uploadProfileImage(any(MultipartFile.class))).thenReturn("/files/profile_image.jpg");
        when(memberConverter.toMemberEntityWithUser(joinRequestDto)).thenReturn(MemberEntity.builder().build());

        //then
        memberBusiness.join(joinRequestDto);

        verify(memberService, times(1)).checkDuplicatedAndWithdrawnInAWeekEmail(anyString());
        verify(emailAuthenticationService, times(1)).checkEmailAuthentication(anyString());
        verify(profileImageService, times(1)).uploadProfileImage(any(MultipartFile.class));
        verify(memberService, times(1)).register(any(MemberEntity.class));
    }

    @Test
    @DisplayName("회원정보 가져오기 - 성공")
    public void testGetMemberInfo() throws Exception {
        // Set up mock behaviors for the MemberService
        when(memberService.getCurrentMemberEntity()).thenReturn( MemberEntity.builder().build());

        // Set up mock behaviors for the MemberConverter
        when(memberConverter.toMemberInfoDto(any(MemberEntity.class))).thenReturn(new MemberInfoResponseDto());

        // Call the method to be tested
        MemberInfoResponseDto memberInfoResponseDto = memberBusiness.getMemberInfo();

        // Verify the interactions and result
        verify(memberService, times(1)).getCurrentMemberEntity();
        assertNotNull(memberInfoResponseDto);
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    public void testWithdraw() throws Exception {
        // Set up mock behaviors for the MemberService
        MemberEntity memberEntity =MemberEntity.builder().password("password123").build();
        when(memberService.getCurrentMemberEntity()).thenReturn(memberEntity);
        doNothing().when(memberService).unregister(any(MemberEntity.class), anyString());

        // Call the method to be tested
        memberBusiness.withdraw("password123");

        // Verify the interactions with the MemberService
        verify(memberService, times(1)).getCurrentMemberEntity();
        verify(memberService, times(1)).unregister(any(MemberEntity.class), anyString());
    }

    @Test
    @DisplayName("임시비밀번호 발급 - 성공")
    public void testIssueTemporaryPassword() throws Exception {
        // Set up mock behaviors for the MemberService
        MemberEntity memberEntity = MemberEntity.builder().email("test@example.com").name("Test User").build();
        when(memberService.findByEmailAndName(anyString(), anyString())).thenReturn(memberEntity);
        when(memberService.createTemporaryPassword()).thenReturn("temp123");
        doNothing().when(memberService).changePassword(any(MemberEntity.class), anyString());
        doNothing().when(memberService).sendTemporaryPassword(any(MemberEntity.class), anyString());

        // Call the method to be tested
        memberBusiness.issueTemporaryPassword("test@example.com", "Test User");

        // Verify the interactions with the MemberService
        verify(memberService, times(1)).findByEmailAndName(anyString(), anyString());
        verify(memberService, times(1)).createTemporaryPassword();
        verify(memberService, times(1)).changePassword(any(MemberEntity.class), anyString());
        verify(memberService, times(1)).sendTemporaryPassword(any(MemberEntity.class), anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    public void testChangePassword() throws Exception {
        // Set up mock behaviors for the MemberService
        MemberEntity memberEntity = MemberEntity.builder().password("oldPassword123").build();
        when(memberService.getCurrentMemberEntity()).thenReturn(memberEntity);
        doNothing().when(memberService).changePassword(any(MemberEntity.class), anyString());

        // Call the method to be tested
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setCurrentPassword("oldPassword123");
        passwordUpdateRequestDto.setNewPassword("newPassword456");
        memberBusiness.changePassword(passwordUpdateRequestDto);

        // Verify the interactions with the MemberService
        verify(memberService, times(1)).getCurrentMemberEntity();
        verify(memberService, times(1)).changePassword(any(MemberEntity.class), anyString());
    }

    @Test
    @DisplayName("MBTI 변경 - 성공")
    public void testChangeMbti() throws Exception {
        // Set up mock behaviors for the MemberService
        MemberEntity memberEntity = MemberEntity.builder().build();
        when(memberService.getCurrentMemberEntity()).thenReturn(memberEntity);
        doNothing().when(memberService).changeMbti(any(MemberEntity.class), any());

        // Call the method to be tested
        memberBusiness.changeMbti(MemberEntity.Mbti.INTJ);

        // Verify the interactions with the MemberService
        verify(memberService, times(1)).getCurrentMemberEntity();
        verify(memberService, times(1)).changeMbti(any(MemberEntity.class), any());
    }

    @Test
    @DisplayName("프로필이미지 변경 - 성공")
    public void testChangeProfileImage() throws Exception {
        // given
        MemberEntity loginMemberEntityMock = mock(MemberEntity.class);
        MultipartFile newProfileImage = new MockMultipartFile("file", "new_profile_image.jpg", "image/jpeg", new byte[]{});

        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntityMock);
        when(loginMemberEntityMock.getProfileImageUrl()).thenReturn("/files/old_profile_image.jpg");

        // then
        memberBusiness.changeProfileImage(newProfileImage);

        verify(profileImageService, times(1)).deleteProfileImage("/files/old_profile_image.jpg");
        verify(memberService, times(1)).changeProfileImage(eq(loginMemberEntityMock), eq(newProfileImage));
    }

    @Test
    @DisplayName("자기소개 변경 - 성공")
    public void testChangeSelfIntroduction() throws Exception {
        // Set up mock behaviors for the MemberService
        MemberEntity memberEntity = MemberEntity.builder().build();
        when(memberService.getCurrentMemberEntity()).thenReturn(memberEntity);
        doNothing().when(memberService).changeSelfIntroduction(any(MemberEntity.class), anyString());

        // Call the method to be tested
        memberBusiness.changeSelfIntroduction("Hello, I'm a new member.");

        // Verify the interactions with the MemberService
        verify(memberService, times(1)).getCurrentMemberEntity();
        verify(memberService, times(1)).changeSelfIntroduction(any(MemberEntity.class), anyString());
    }

    @Test
    @DisplayName("관리자의 회원 조회 - 성공")
    public void testGetMemberInfoByAdmin() throws Exception {
        // Set up mock behaviors for the MemberService and MemberConverter
        MemberEntity memberEntity = MemberEntity.builder().build();
        when(memberService.findByEmail(anyString())).thenReturn(memberEntity);
        when(memberConverter.toMemberInfoByAdminDto(any(MemberEntity.class)))
                .thenReturn(new MemberInfoByAdminResponseDto());

        // Call the method to be tested
        MemberInfoByAdminResponseDto memberInfoByAdminResponseDto = memberBusiness.getMemberInfoByAdmin("test@example.com");

        // Verify the interactions and result
        assertNotNull(memberInfoByAdminResponseDto);
        verify(memberService, times(1)).findByEmail(anyString());
        verify(memberConverter, times(1)).toMemberInfoByAdminDto(any(MemberEntity.class));
    }

}

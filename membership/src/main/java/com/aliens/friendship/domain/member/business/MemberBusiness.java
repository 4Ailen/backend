package com.aliens.friendship.domain.member.business;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoByAdminResponseDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoResponseDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.converter.MemberConverter;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.member.service.ProfileImageService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Business
@Slf4j
public class MemberBusiness {

    private final MemberService memberService;
    private final MemberConverter memberConverter;
    private final EmailAuthenticationService emailAuthenticationService;
    private final ProfileImageService profileImageService;

    /**
     * 회원가입
     */
    public void join(JoinRequestDto joinRequestDto) throws Exception {

        // 중복체크
        memberService.checkDuplicatedAndWithdrawnInAWeekEmail(joinRequestDto.getEmail());

        // 이메일 검증 체크
        emailAuthenticationService.checkEmailAuthentication(joinRequestDto.getEmail());

        // 프로필이미지 체크
        String imageURL = profileImageService.uploadProfileImage(joinRequestDto.getProfileImage());
        joinRequestDto.setImageUrl(imageURL);

        // 엔티티 변환
        MemberEntity memberEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);

        //엔티티 저장
        memberService.register(memberEntity);
    }

    /**
     * 회원정보 가져오기
     */
    public MemberInfoResponseDto getMemberInfo() throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // DTO 변환
        MemberInfoResponseDto memberInfoResponseDto = memberConverter.toMemberInfoDto(loginMemberEntity);

        return memberInfoResponseDto;
    }

    /**
     * 회원 탈퇴
     */
    public void withdraw(String password) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //회원 탈퇴
        memberService.unregister(loginMemberEntity,password);

    }


    /**
     * 임시비밀번호 발급
     */
    public void issueTemporaryPassword(String email, String name) throws Exception {

        //회원 찾기
        MemberEntity memberEntity = memberService.findByEmailAndName(email,name);

        //임시 번호 생성
        String temporaryPassword = memberService.createTemporaryPassword();

        //회원 임시 번호로 비밀번호 변경
        memberService.changePassword(memberEntity,temporaryPassword);

        //변경된 비밀번호 메일전송
        memberService.sendTemporaryPassword(memberEntity,temporaryPassword);

    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(PasswordUpdateRequestDto passwordUpdateRequestDto) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //새로운 비밀번호와 현재 등록된 비밀번호가 동일한지 체크
        memberService.checkCurrentPassword(passwordUpdateRequestDto.getCurrentPassword(), loginMemberEntity);

        //새로운 비밀번호와 DTO의 현재 비밀번호가 동일한지 체크
        memberService.checkSameNewPasswordAndCurrentPassword(passwordUpdateRequestDto);

        //새 비밀번호로 변경
        memberService.changePassword(loginMemberEntity, passwordUpdateRequestDto.getNewPassword());
    }


    /**
     * MBTI 변경
     */
    public void changeMbti(MemberEntity.Mbti mbti) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //MBTI 변경
        memberService.changeMbti(loginMemberEntity,mbti);
    }


    /**
     * 프로필이미지 변경
     */
    public void changeProfileImage(MultipartFile profileImage) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //회원의 프로필 이미지가 존재한다면 삭제
        if (!loginMemberEntity.getProfileImageUrl().equals("/files/default_profile_image.png")) {
            profileImageService.deleteProfileImage(loginMemberEntity.getProfileImageUrl());
        }

        // 회원 프로필 사진 변경
        memberService.changeProfileImage(loginMemberEntity,profileImage);

    }


    /**
     * 자기소개 변경
     */
    public void changeSelfIntroduction(String selfIntroductionChangeRequest) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //자기소개 변경
        memberService.changeSelfIntroduction(loginMemberEntity,selfIntroductionChangeRequest);

    }

    /**
     * 관리자의 회원 조회
     */
    public MemberInfoByAdminResponseDto getMemberInfoByAdmin(String email) throws Exception {

        // 찾고자하는 회원 엔티티
        MemberEntity memberEntity  = memberService.findByEmail(email);

        // DTO 변환
        MemberInfoByAdminResponseDto memberInfoByAdminResponseDto = memberConverter.toMemberInfoByAdminDto(memberEntity);

        return memberInfoByAdminResponseDto;
    }

}

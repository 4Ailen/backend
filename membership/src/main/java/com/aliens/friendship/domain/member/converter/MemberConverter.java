package com.aliens.friendship.domain.member.converter;

import com.aliens.db.auth.entity.AuthorityEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoByAdminResponseDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoResponseDto;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Converter
public class MemberConverter {
    @Value("${file-server.domain}")
    private String domainUrl;

    public MemberEntity toMemberEntityWithUser(JoinRequestDto joinRequestDto){
        MemberEntity memberEntity = MemberEntity.builder()
                .email(joinRequestDto.getEmail())
                .password(joinRequestDto.getPassword())
                .mbti(joinRequestDto.getMbti())
                .gender(joinRequestDto.getGender())
                .birthday(joinRequestDto.getBirthday())
                .name(joinRequestDto.getName())
                .nationality(joinRequestDto.getNationality())
                .profileImageUrl(joinRequestDto.getImageUrl())
                .selfIntroduction(joinRequestDto.getSelfIntroduction())
                .build();
        memberEntity.addAuthority(AuthorityEntity.ofUser(memberEntity));
        return memberEntity;
    }

    public MemberInfoResponseDto toMemberInfoDto(MemberEntity memberEntity) throws Exception {
        MemberInfoResponseDto memberInfoResponseDto =  MemberInfoResponseDto.builder()
                .memberId(memberEntity.getId())
                .email(memberEntity.getEmail())
                .mbti(memberEntity.getMbti())
                .gender(memberEntity.getGender())
                .nationality(memberEntity.getNationality())
                .birthday(memberEntity.getBirthday())
                .age(memberEntity.getAge())
                .name(memberEntity.getName())
                .selfIntroduction(memberEntity.getSelfIntroduction())
                .profileImage(domainUrl + memberEntity.getProfileImageUrl())
                .build();

        return memberInfoResponseDto;
    }

    public MemberInfoByAdminResponseDto toMemberInfoByAdminDto(MemberEntity memberEntity)throws Exception{
        MemberInfoByAdminResponseDto memberInfoByAdminResponseDto =  MemberInfoByAdminResponseDto.builder()
                .email(memberEntity.getEmail())
                .mbti(memberEntity.getMbti())
                .gender(memberEntity.getGender())
                .nationality(memberEntity.getNationality())
                .status(memberEntity.getStatus())
                .name(memberEntity.getName())
                .withdrawalAt(memberEntity.getWithdrawalAt() != null
                        ? memberEntity.getWithdrawalAt().toString()
                        : null)
                .birthday(memberEntity.getBirthday())
                .age(memberEntity.getAge())
                .build();
        return memberInfoByAdminResponseDto;
    }

}
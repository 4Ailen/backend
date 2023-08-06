package com.aliens.friendship.domain.member.service;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.chatting.repository.ChattingRoomRepository;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.matching.repository.MatchRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.emailauthentication.exception.EmailAlreadyRegisteredException;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;
    private final JavaMailSender javaMailSender;
    private final ApplicantService applicantService;
    private final MatchRepository matchRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    @Value("${file-server.domain}")
    private String domainUrl;

    @Transactional
    public boolean changeProfileImage(MemberEntity memberEntity, MultipartFile profileImage) throws Exception {
        memberEntity.updateImageUrl(profileImageService.uploadProfileImage(profileImage));
        memberRepository.save(memberEntity);
        return true;
    }

    @Transactional
    public Long register(MemberEntity memberEntity) {
        memberEntity.updatePassword(passwordEncoder.encode(memberEntity.getPassword()));
        MemberEntity saved = memberRepository.save(memberEntity);
        return saved.getId();
    }

    @Transactional
    public void unregister(MemberEntity memberEntity,String password) {
        if(isAppliedAndMatched(memberEntity, applicantService.findByMemberEntity(memberEntity))){
            closeChattingRoomWithMember(memberEntity);
        }

        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
        memberEntity.updateStatus(MemberEntity.Status.WITHDRAWN);
        memberEntity.updateWithdrawalDate(Instant.now());
    }

    @Transactional
    public void deleteMemberInfoByAdmin(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void changePassword(MemberEntity memberEntity, String password) throws Exception {
        memberEntity.updatePassword(passwordEncoder.encode(password));
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void changeSelfIntroduction(MemberEntity memberEntity, String selfIntroductionChangeRequest) throws Exception {
        memberEntity.updateSelfIntroduction(selfIntroductionChangeRequest);
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void changeMbti(MemberEntity memberEntity, MemberEntity.Mbti mbti) {
        memberEntity.updateMbti(mbti);
        memberRepository.save(memberEntity);
    }

    public MemberEntity findByEmailAndName(String email, String name) throws Exception {
        return memberRepository.findByEmailAndName(email,name).orElseThrow(MemberNotFoundException::new);
    }

    public boolean isJoinedEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public boolean checkDuplicatedAndWithdrawnInAWeekEmail(String email) throws Exception {
        if (memberRepository.findByEmail(email).isPresent()) {
            if (memberRepository.findByEmail(email).get().getStatus() == MemberEntity.Status.WITHDRAWN) {
                throw new WithdrawnMemberWithinAWeekException();
            } else {
                throw new DuplicateMemberEmailException();
            }
        }
        return true;
    }

    public String createTemporaryPassword() {
        Random random = new Random();
        int length = 8 + random.nextInt(5);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    public void sendTemporaryPassword(MemberEntity memberEntity, String temporaryPassword) throws  Exception{
        SimpleMailMessage authenticationMail = createAuthenticationMail(memberEntity.getEmail(), memberEntity.getName(), temporaryPassword);
        javaMailSender.send(authenticationMail);
    }

    public void checkCurrentPassword(String currentPassword, MemberEntity memberEntity) throws Exception {
        if (!passwordEncoder.matches(currentPassword, memberEntity.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
    }

    public void checkSameNewPasswordAndCurrentPassword(PasswordUpdateRequestDto passwordUpdateRequestDto) throws Exception {
        if (passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getCurrentPassword())) {
            throw new PasswordChangeFailedException();
        }
    }

    public MemberEntity findByEmail(String email) throws Exception {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public MemberEntity findById(Long memberId) throws Exception {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public boolean checkJoinedEmail(String email) throws Exception {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }
        return true;
    }

    public SimpleMailMessage createAuthenticationMail(String email, String name, String temporaryPassword) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(email);
        authenticationEmail.setSubject("[FriendShip] Temporary Password Information  임시 비밀번호 발급");
        String content = getContentWithNameAndTemporaryPassword(name,temporaryPassword);
        authenticationEmail.setText(content);
        return authenticationEmail;
    }


    // TODO: 탈퇴 한달 후 삭제
    public void deleteWithdrawnMember(MemberEntity memberEntity) {
        memberRepository.delete(memberEntity);
    }

    /**
     * 현재 로그인 회원 엔티티 조회
     */
    public MemberEntity getCurrentMemberEntity() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String MemberEmail = userDetails.getUsername();
        MemberEntity memberEntity = findByEmail(MemberEmail);
        return memberEntity;
    }

    public List<MemberEntity> findAllAppliedMember() {
        return memberRepository.findAllByStatus(MemberEntity.Status.APPLIED);
    }

    @Transactional
    public void changeApplied(MemberEntity loginMemberEntity) {
        loginMemberEntity.updateStatus(MemberEntity.Status.APPLIED);
    }

    private boolean isAppliedAndMatched(MemberEntity memberEntity, ApplicantEntity applicantEntity){
        return memberEntity.getStatus() == MemberEntity.Status.APPLIED && applicantEntity.getIsMatched() == ApplicantEntity.Status.MATCHED;
    }

    private void closeChattingRoomWithMember(MemberEntity memberEntity){
        List<MatchingEntity> matchingEntities = matchRepository.findAllByMatchingMember(memberEntity);
        for(MatchingEntity matchingEntity : matchingEntities){
            matchingEntity.getChattingRoomEntity().updateStatus(ChattingRoomEntity.RoomStatus.CLOSE);
        }
    }
    private String getContentWithNameAndTemporaryPassword(String name,String temporaryPassword){
        String content = "Hello "+name+",\n\n"+
                "Please use this temporary password to log in, \n"+
                "and make sure to change your password for security purposes once you're logged in.\n\n"+
                "Temporary Password: "+temporaryPassword+
                "\n\nAfter logging in, please follow these steps to change your password:\n"+
                "1. Log in to FriendShip App.\n" +
                "2. Navigate to \" Profile Settings.\"\n"+
                "3. Choose the \"Change Password\" option.\n"+
                "4. Enter the temporary password and your new password.\n"+
                "5. Save the changes.\n"+
                "If you have any questions," +
                "please contact us at this email.\n\n"+
                "Thank you,\n"+
                "The 4aliens Team\n\n\n\n"+
                "안녕하세요, "+name+"님,\n\n"+
                "비밀번호 변경 요청에 따라 임시 비밀번호를 안내해 드립니다. 로그인 후에는 보안을 위해 비밀번호를 변경하셔야 합니다.\n\n"+
                "임시 비밀번호: "+temporaryPassword+
                "\n\n로그인하신 후에는 다음 단계를 따라 비밀번호를 변경해 주세요:\n" +
                "1. FriendShip App에 로그인하세요.\n" +
                "2. \"계정 설정\" 메뉴로 이동하세요.\n"+
                "3. \"비밀번호 변경\" 옵션을 선택하세요.\n"+
                "4. 임시 비밀번호와 새로운 비밀번호를 입력해 주세요.\n" +
                "5. 변경 사항을 저장하세요.\n" +
                "만약 궁금한 사항이 있으시면 해당 이메일로 문의해 주세요.\n\n"+
                "감사합니다,\n"+
                "4aliens 팀 올림\n";
        return content;
    }
}
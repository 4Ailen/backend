package com.aliens.db.auth.entity;

import com.aliens.db.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Entity @Getter @Builder
@Table(name = "authority", schema = "aliendb")
public class AuthorityEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    private String role;

    public static AuthorityEntity ofUser(MemberEntity memberEntity) {
        return AuthorityEntity.builder()
                .role("ROLE_USER")
                .memberEntity(memberEntity)
                .build();
    }

    public static AuthorityEntity ofAdmin(MemberEntity memberEntity) {
        return AuthorityEntity.builder()
                .role("ROLE_ADMIN")
                .memberEntity(memberEntity)
                .build();
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
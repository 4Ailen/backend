package com.aliens.db.auth.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Entity @Getter @SuperBuilder
@Table(name = "authority", schema = "aliendb")
public class AuthorityEntity extends BaseEntity implements GrantedAuthority {

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

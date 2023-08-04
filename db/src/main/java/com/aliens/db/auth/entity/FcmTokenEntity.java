package com.aliens.db.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "fcm_token")
public class FcmTokenEntity {

    @Id
    private String id;

    private Long memberId;

    private String value;

    private FcmTokenEntity(
            Long memberId,
            String value
    ) {
        this.memberId = memberId;
        this.value = value;
    }

    public static FcmTokenEntity of(
            Long memberPersonalId,
            String value
    ) {
        return new FcmTokenEntity(memberPersonalId, value);
    }

    public void changeMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
package com.aliens.db.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "refresh_token")
public class RefreshTokenEntity {

    @Id
    private String id;

    private String email;

    private String value;

    private RefreshTokenEntity(
            String email,
            String value
    ) {
        this.email = email;
        this.value = value;
    }

    public static RefreshTokenEntity of(
            String memberPersonalId,
            String value
    ) {
        return new RefreshTokenEntity(memberPersonalId, value);
    }

    /**
     * Token Value 변경
     */
    public void changeTokenValue(String tokenValue) {
        this.value = tokenValue;
    }
}
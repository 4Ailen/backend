package com.aliens.friendship.domain.auth.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collation = "refresh_token")
public class RefreshToken {

    @Id
    private String id;

    private String email;

    private String value;

    private RefreshToken(
            String email,
            String value
    ) {
        this.email = email;
        this.value = value;
    }

    public static RefreshToken of(
            String memberPersonalId,
            String value
    ) {
        return new RefreshToken(memberPersonalId, value);
    }

    /**
     * Token Value 변경
     */
    public void changeTokenValue(String tokenValue) {
        this.value = tokenValue;
    }
}
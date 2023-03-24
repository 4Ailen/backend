package com.aliens.friendship.member.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Nationality.TABLE_NAME, schema = "aliendb")
public class Nationality {
    public static final String TABLE_NAME = "nationality";
    public static final String COLUMN_ID_NAME = "nationality_id";
    public static final String COLUMN_NATINALITYTEXT_NAME = "natinality_text";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_NATINALITYTEXT_NAME, nullable = false, length = 45)
    private String natinalityText;

    public String getCountryImageUrl() {
        return "도메인" + natinalityText + ".png";
    }
}
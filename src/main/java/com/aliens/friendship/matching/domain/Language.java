package com.aliens.friendship.matching.domain;

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
@Table(name = Language.TABLE_NAME, schema = "aliendb")
public class Language {
    public static final String TABLE_NAME = "language";
    public static final String COLUMN_ID_NAME = "language_id";
    public static final String COLUMN_LANGUAGETEXT_NAME = "language_text";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_LANGUAGETEXT_NAME, nullable = false, length = 45)
    private String languageText;

}
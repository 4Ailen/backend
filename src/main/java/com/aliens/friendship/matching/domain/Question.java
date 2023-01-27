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
@Setter
@ToString
@Entity
@Table(name = Question.TABLE_NAME, schema = "aliendb")
public class Question {
    public static final String TABLE_NAME = "question";
    public static final String COLUMN_ID_NAME = "question_id";
    public static final String COLUMN_QUESTIONTEXT_NAME = "question_text";
    public static final String COLUMN_ISSELECTED_NAME = "is_selected";


    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_QUESTIONTEXT_NAME, nullable = false, length = 45)
    private String questionText;

    @Column(name = COLUMN_ISSELECTED_NAME, nullable = false)
    private Byte isSelected;

}
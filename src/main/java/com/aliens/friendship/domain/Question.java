package com.aliens.friendship.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "question", schema = "aliendb")
public class Question {
    @Id
    @Column(name = "question_id", nullable = false)
    private Integer id;

    @Column(name = "sentence", nullable = false, length = 45)
    private String sentence;

    @Column(name = "is_selected", nullable = false)
    private Byte isSelected;

}
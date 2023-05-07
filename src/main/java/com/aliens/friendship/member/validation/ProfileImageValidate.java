package com.aliens.friendship.member.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = ProfileImageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProfileImageValidate {

    String message() default "유효하지 않은 프로필 이미지입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
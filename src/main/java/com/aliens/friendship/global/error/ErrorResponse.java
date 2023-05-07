package com.aliens.friendship.global.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private Integer status;
    private String code;
    private String message;
    private List<FieldError> errors;
    private LocalDateTime timestamp;

    private ErrorResponse(
            final ExceptionCode exceptionCode
    ) {
        this.message = exceptionCode.getMessage();
        this.status = exceptionCode.getHttpStatus().value();
        this.code = exceptionCode.getCode();
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(
            final ExceptionCode exceptionCode,
            final String message
    ) {
        this.message = message;
        this.status = exceptionCode.getHttpStatus().value();
        this.code = exceptionCode.getCode();
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(
            final ExceptionCode exceptionCode,
            final List<FieldError> errors
    ) {
        this.message = exceptionCode.getMessage();
        this.status = exceptionCode.getHttpStatus().value();
        this.code = exceptionCode.getCode();
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public static ErrorResponse of(
            final ExceptionCode exceptionCode
    ) {
        return new ErrorResponse(exceptionCode);
    }

    public static ErrorResponse of(
            final ExceptionCode exceptionCode,
            final String message
    ) {
        return new ErrorResponse(exceptionCode, message);
    }

    public static ErrorResponse of(
            final ExceptionCode code,
            final BindingResult bindingResult
    ) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(
            final ExceptionCode exceptionCode,
            final List<FieldError> errors
    ) {
        return new ErrorResponse(exceptionCode, errors);
    }

    /**
     * Object -> Json (필터에서 사용)
     */
//    public String convertJson() {
//        return new GsonUtil().toJson(this);
//    }

    /**
     * 입력 값 검증 에러를 상세하게 표현하는 Inner Class
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(
                String field,
                String value,
                String reason
        ) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(
                final String field,
                final String value,
                final String reason
        ) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));

            return fieldErrors;
        }

        private static List<FieldError> of(
                final BindingResult bindingResult
        ) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();

            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
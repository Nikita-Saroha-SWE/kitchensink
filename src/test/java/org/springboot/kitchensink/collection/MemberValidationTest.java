package org.springboot.kitchensink.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springboot.kitchensink.collections.Member;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class MemberValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validMember_shouldHaveNoViolations() {
        Member member = new Member(
                "Alice",
                "alice@example.com",
                "1234567890",
                "ADMIN",
                "user123"
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertThat(violations).isEmpty();
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        Member member = new Member(
                "Alice",
                "invalid-email",
                "1234567890",
                "ADMIN"
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shortPhoneNumber_shouldFailValidation() {
        Member member = new Member(
                "Alice",
                "alice@example.com",
                "12345", // too short
                "ADMIN"
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
    }

    @Test
    void nameContainingNumber_shouldFailValidation() {
        Member member = new Member(
                "Bob123",
                "bob@example.com",
                "1234567890",
                "USER"
        );

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}

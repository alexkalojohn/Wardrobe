package com.example.wardrobeplanner.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for EmailValidator.
 */
public class EmailValidatorTest {

    @Test
    public void validate_validSimpleEmail_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@example.com");
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    public void validate_validEmailWithDots_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("first.last@example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithPlus_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user+tag@example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithUnderscore_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user_name@example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithPercent_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user%name@example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithHyphen_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user-name@example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithNumbers_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user123@example456.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_validEmailWithSubdomain_returnsValid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@mail.example.com");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_nullEmail_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate(null);
        assertFalse(result.isValid());
        assertEquals("Το email είναι υποχρεωτικό", result.getErrorMessage());
    }

    @Test
    public void validate_emptyEmail_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("");
        assertFalse(result.isValid());
        assertEquals("Το email είναι υποχρεωτικό", result.getErrorMessage());
    }

    @Test
    public void validate_blankEmail_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("   ");
        assertFalse(result.isValid());
        assertEquals("Το email είναι υποχρεωτικό", result.getErrorMessage());
    }

    @Test
    public void validate_emailWithSpaces_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user @example.com");
        assertFalse(result.isValid());
        assertEquals("Το email δεν μπορεί να περιέχει κενά", result.getErrorMessage());
    }

    @Test
    public void validate_emailMissingAtSymbol_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("userexample.com");
        assertFalse(result.isValid());
        assertEquals("Μη έγκυρη μορφή email (π.χ. user@example.com)", result.getErrorMessage());
    }

    @Test
    public void validate_emailMissingDomain_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@");
        assertFalse(result.isValid());
    }

    @Test
    public void validate_emailMissingLocalPart_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("@example.com");
        assertFalse(result.isValid());
    }

    @Test
    public void validate_emailMissingTld_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@example");
        assertFalse(result.isValid());
    }

    @Test
    public void validate_emailWithMultipleAtSymbols_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@@example.com");
        assertFalse(result.isValid());
    }

    @Test
    public void validate_emailWithSpecialCharsInDomain_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@exa!mple.com");
        assertFalse(result.isValid());
    }

    @Test
    public void isValid_validEmail_returnsTrue() {
        assertTrue(EmailValidator.isValid("test@domain.com"));
    }

    @Test
    public void isValid_invalidEmail_returnsFalse() {
        assertFalse(EmailValidator.isValid("invalid-email"));
    }

    @Test
    public void validate_trimsLeadingAndTrailingSpaces() {
        EmailValidator.ValidationResult result = EmailValidator.validate("  user@example.com  ");
        assertTrue(result.isValid());
    }

    @Test
    public void validate_singleCharTld_returnsInvalid() {
        EmailValidator.ValidationResult result = EmailValidator.validate("user@example.c");
        assertFalse(result.isValid());
    }
}

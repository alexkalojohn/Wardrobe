package com.example.wardrobeplanner.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Utility class for email validation.
 * Provides both frontend (Android Patterns) and backend-compatible validation.
 */
public class EmailValidator {

    // RFC 5322 compliant regex that supports common special characters
    // Allows: letters, digits, +, _, %, -, . in local part
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private EmailValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates an email address.
     * Checks for:
     * - Non-null and non-empty value
     * - No leading/trailing whitespace (after trim)
     * - Valid format: local@domain.tld
     * - Common special characters supported in local part
     *
     * @param email the email to validate
     * @return ValidationResult containing isValid flag and error message
     */
    public static ValidationResult validate(String email) {
        if (email == null) {
            return new ValidationResult(false, "Το email είναι υποχρεωτικό");
        }

        String trimmed = email.trim();

        if (trimmed.isEmpty()) {
            return new ValidationResult(false, "Το email είναι υποχρεωτικό");
        }

        // Check for spaces within the email
        if (trimmed.contains(" ")) {
            return new ValidationResult(false, "Το email δεν μπορεί να περιέχει κενά");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            return new ValidationResult(false, "Μη έγκυρη μορφή email (π.χ. user@example.com)");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Quick check if email format is valid.
     *
     * @param email the email to check
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String email) {
        return validate(email).isValid();
    }

    /**
     * Validates email using Android's built-in pattern (for frontend use).
     *
     * @param email the email to validate
     * @return ValidationResult containing isValid flag and error message
     */
    public static ValidationResult validateWithAndroidPattern(String email) {
        if (email == null) {
            return new ValidationResult(false, "Το email είναι υποχρεωτικό");
        }

        String trimmed = email.trim();

        if (trimmed.isEmpty()) {
            return new ValidationResult(false, "Το email είναι υποχρεωτικό");
        }

        if (trimmed.contains(" ")) {
            return new ValidationResult(false, "Το email δεν μπορεί να περιέχει κενά");
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            return new ValidationResult(false, "Μη έγκυρη μορφή email (π.χ. user@example.com)");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Result class for validation operations.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

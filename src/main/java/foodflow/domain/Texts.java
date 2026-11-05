package foodflow.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;

/** Small package-private helpers shared by the domain classes (validation and text matching). */
final class Texts {

    private Texts() {
    }

    static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be empty.");
        }
        return value.trim();
    }

    /** Scales an amount to exactly two decimals. */
    static BigDecimal money(BigDecimal value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Normalizes text for forgiving comparisons: case-insensitive and ignoring Turkish
     * diacritics, so "kadikoy", "KADIKOY" and "Kadıköy" are all equal.
     */
    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String s = value.trim().replace('İ', 'i').replace('ı', 'i').toLowerCase(Locale.ROOT);
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}


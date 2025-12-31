package com.inqwise.opinion.common;

import java.security.SecureRandom;
import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * Generates short, human-friendly UID prefixes.
 *
 * <p>The generated strings are designed to be:
 * <ul>
 *   <li>Readable and pronounceable (alternating consonants and vowels)</li>
 *   <li>Resistant to accidental profanity</li>
 *   <li>Unambiguous when read aloud (digits exclude 0,1)</li>
 * </ul>
 *
 * <p>Typical output example: {@code kapev7moba} or with prefix: {@code ac_tobira3}
 *
 * <p>This class is thread-safe.
 */
public final class UidPrefixGenerator {

    private static final char[] CONSONANTS = "bcdfghjkmnpqrstvwxyz".toCharArray();
    private static final char[] VOWELS = "aeiou".toCharArray();
    private static final char[] DIGITS = "23456789".toCharArray();
    private static final int DIGIT_PROBABILITY = 5; // ~20%
    private static final char RESERVED_CHAR = '-';

    private static final SecureRandom RND = new SecureRandom();

    private final int size;
    private final boolean preventFirstCharDigit;
    private final String startsWith;

    private UidPrefixGenerator(Builder builder) {
        this.size = builder.size;
        this.preventFirstCharDigit = builder.preventFirstCharDigit;
        this.startsWith = builder.startsWith;
    }

    /**
     * Generates a new UID prefix.
     *
     * @return generated prefix string
     */
    public String generate() {
        StringBuilder sb = new StringBuilder(size);
        
        // Add the prefix if specified
        if (startsWith != null && !startsWith.isEmpty()) {
            sb.append(startsWith);
        }
        
        int startIndex = sb.length();
        int remainingSize = size - startIndex;

        for (int i = 0; i < remainingSize; i++) {
            int absolutePosition = startIndex + i;
            
            if (absolutePosition % 2 == 0) {
                // Even positions: consonants or digits
                boolean shouldUseDigit = RND.nextInt(DIGIT_PROBABILITY) == 0;
                
                // Prevent first character from being a digit if configured
                if (preventFirstCharDigit && absolutePosition == 0 && shouldUseDigit) {
                    shouldUseDigit = false;
                }
                
                if (shouldUseDigit) {
                    sb.append(randomChar(DIGITS));
                } else {
                    sb.append(randomChar(CONSONANTS));
                }
            } else {
                // Odd positions: vowels
                sb.append(randomChar(VOWELS));
            }
        }
        return sb.toString();
    }

    private static char randomChar(char[] source) {
        return source[RND.nextInt(source.length)];
    }

    /**
     * Creates a new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Convenience instance for the common case:
     * size = 10, ~20% digits.
     */
    public static final UidPrefixGenerator SIZE_10 =
            builder().size(10).build();

    /**
     * Builder for {@link UidPrefixGenerator}.
     */
    public static final class Builder {

        private int size = 10;
        private boolean preventFirstCharDigit = false;
        private String startsWith = null;

        private Builder() {
        }

        /**
         * Sets the total length of the generated prefix (including any startsWith prefix).
         *
         * @param size prefix length, must be greater than 2
         */
        public Builder size(int size) {
            Preconditions.checkArgument(size > 2, "size must be > 2");
            this.size = size;
            return this;
        }

        /**
         * Prevents the first character of the generated portion from being a digit.
         * This is useful when you want to ensure IDs always start with a letter.
         *
         * @param preventFirstCharDigit true to prevent first char from being a digit
         */
        public Builder preventFirstCharDigit(boolean preventFirstCharDigit) {
            this.preventFirstCharDigit = preventFirstCharDigit;
            return this;
        }

        /**
         * Sets a prefix string that will be prepended to every generated ID.
         * The prefix is included in the total size calculation.
         * The reserved character '-' cannot be used in the prefix.
         *
         * <p>Example: {@code startsWith("ac_")} will generate IDs like "ac_tobira3"
         *
         * @param startsWith prefix string (cannot contain '-')
         */
        public Builder startsWith(String startsWith) {
            if (startsWith != null) {
                Preconditions.checkArgument(
                    startsWith.indexOf(RESERVED_CHAR) == -1,
                    "startsWith cannot contain reserved character: %s", RESERVED_CHAR
                );
            }
            this.startsWith = startsWith;
            return this;
        }

        /**
         * Builds an immutable {@link UidPrefixGenerator}.
         */
        public UidPrefixGenerator build() {
            // Validate that size is sufficient for startsWith prefix
            if (startsWith != null && !startsWith.isEmpty()) {
                Preconditions.checkArgument(
                    size > startsWith.length(),
                    "size must be greater than startsWith prefix length (%s)",
                    startsWith.length()
                );
            }
            return new UidPrefixGenerator(this);
        }
    }
}

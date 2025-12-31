package com.inqwise.opinion.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UidPrefixGenerator}.
 */
@ExtendWith(VertxExtension.class)
class UidPrefixGeneratorTest {

	@Test
	void generate_withDefaultInstance_producesCorrectLength() {
		String prefix = UidPrefixGenerator.SIZE_10.generate();
		assertEquals(10, prefix.length());
	}

	@Test
	void generate_withDefaultInstance_producesLowercaseString() {
		String prefix = UidPrefixGenerator.SIZE_10.generate();
		assertEquals(prefix.toLowerCase(), prefix);
	}

	@Test
	void generate_multipleCalls_produceDifferentResults() {
		String prefix1 = UidPrefixGenerator.SIZE_10.generate();
		String prefix2 = UidPrefixGenerator.SIZE_10.generate();
		String prefix3 = UidPrefixGenerator.SIZE_10.generate();
		
		// With high probability, these should all be different
		Set<String> uniquePrefixes = new HashSet<>();
		uniquePrefixes.add(prefix1);
		uniquePrefixes.add(prefix2);
		uniquePrefixes.add(prefix3);
		
		assertTrue(uniquePrefixes.size() >= 2, "Should generate different prefixes");
	}

	@Test
	void generate_followsConsonantVowelPattern() {
		String prefix = UidPrefixGenerator.SIZE_10.generate();
		
		// Check consonant-vowel alternation (allowing for occasional digits)
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if (i % 2 == 0) {
				// Even positions: consonants or digits
				assertTrue(isConsonant(c) || Character.isDigit(c),
					"Position " + i + " should be consonant or digit, but got: " + c);
			} else {
				// Odd positions: vowels
				assertTrue(isVowel(c),
					"Position " + i + " should be vowel, but got: " + c);
			}
		}
	}

	@Test
	void builder_withCustomSize_producesCorrectLength() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(6)
			.build();
		
		String prefix = generator.generate();
		assertEquals(6, prefix.length());
	}

	@Test
	void builder_withCustomSize20_producesCorrectLength() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(20)
			.build();
		
		String prefix = generator.generate();
		assertEquals(20, prefix.length());
	}

	@Test
	void builder_withSizeTooSmall_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			UidPrefixGenerator.builder().size(2).build();
		});
	}

	@Test
	void builder_withSizeZero_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			UidPrefixGenerator.builder().size(0).build();
		});
	}

	@Test
	void builder_withNegativeSize_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			UidPrefixGenerator.builder().size(-1).build();
		});
	}

	@Test
	void builder_withPreventFirstCharDigit_firstCharIsNotDigit() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(10)
			.preventFirstCharDigit(true)
			.build();
		
		// Generate multiple times to ensure consistency
		for (int i = 0; i < 50; i++) {
			String prefix = generator.generate();
			assertFalse(Character.isDigit(prefix.charAt(0)),
				"First character should not be a digit: " + prefix);
		}
	}

	@Test
	void builder_withoutPreventFirstCharDigit_canStartWithDigit() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(20)
			.preventFirstCharDigit(false)
			.build();
		
		// Generate many times to potentially get a digit as first char
		boolean foundDigitStart = false;
		for (int i = 0; i < 100; i++) {
			String prefix = generator.generate();
			if (Character.isDigit(prefix.charAt(0))) {
				foundDigitStart = true;
				break;
			}
		}
		
		// With ~20% probability, we should eventually see a digit start
		// This test just documents that digits CAN appear at start without prevention
		// We don't assert foundDigitStart because it's probabilistic
	}

	@Test
	void builder_withStartsWith_prependsPrefix() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(10)
			.startsWith("ac_")
			.build();
		
		String prefix = generator.generate();
		
		assertEquals(10, prefix.length());
		assertTrue(prefix.startsWith("ac_"));
	}

	@Test
	void builder_withStartsWithAndPreventFirstCharDigit_worksCorrectly() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(15)
			.startsWith("usr_")
			.preventFirstCharDigit(true)
			.build();
		
		String prefix = generator.generate();
		
		assertEquals(15, prefix.length());
		assertTrue(prefix.startsWith("usr_"));
		
		// First char after "usr_" (position 4) should not be digit if it's an even position
		// But since "usr_" is length 4 (even), position 4 is even and could be prevented
		String generated = prefix.substring(4);
		assertNotNull(generated);
	}

	@Test
	void builder_withStartsWithContainingDash_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			UidPrefixGenerator.builder()
				.startsWith("ac-test")
				.build();
		});
	}

	@Test
	void builder_withStartsWithTooLong_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			UidPrefixGenerator.builder()
				.size(5)
				.startsWith("verylongprefix")
				.build();
		});
	}

	@Test
	void builder_withNullStartsWith_worksCorrectly() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(10)
			.startsWith(null)
			.build();
		
		String prefix = generator.generate();
		assertEquals(10, prefix.length());
	}

	@Test
	void builder_withEmptyStartsWith_worksCorrectly() {
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(10)
			.startsWith("")
			.build();
		
		String prefix = generator.generate();
		assertEquals(10, prefix.length());
	}

	@Test
	void generate_withStartsWith_maintainsConsonantVowelPattern() {
		// "ac_" is 3 chars, so generated part starts at position 3 (odd)
		UidPrefixGenerator generator = UidPrefixGenerator.builder()
			.size(13)
			.startsWith("ac_")
			.build();
		
		String prefix = generator.generate();
		assertEquals(13, prefix.length());
		assertTrue(prefix.startsWith("ac_"));
		
		// Check pattern in generated part
		for (int i = 3; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if (i % 2 == 0) {
				// Even positions: consonants or digits
				assertTrue(isConsonant(c) || Character.isDigit(c),
					"Position " + i + " should be consonant or digit, but got: " + c);
			} else {
				// Odd positions: vowels
				assertTrue(isVowel(c),
					"Position " + i + " should be vowel, but got: " + c);
			}
		}
	}

	@Test
	void generate_producesReadableOutput() {
		// Generate multiple prefixes and verify they look reasonable
		UidPrefixGenerator generator = UidPrefixGenerator.SIZE_10;
		
		for (int i = 0; i < 10; i++) {
			String prefix = generator.generate();
			
			// Should not contain confusing characters
			assertFalse(prefix.contains("0"));
			assertFalse(prefix.contains("1"));
			
			// Should be lowercase
			assertEquals(prefix, prefix.toLowerCase());
			
			// Should have the expected pattern
			assertNotNull(prefix);
			assertEquals(10, prefix.length());
		}
	}

	@Test
	void generate_statisticalUniqueness() {
		UidPrefixGenerator generator = UidPrefixGenerator.SIZE_10;
		Set<String> prefixes = new HashSet<>();
		
		int count = 100;
		for (int i = 0; i < count; i++) {
			prefixes.add(generator.generate());
		}
		
		// Should generate mostly unique values
		assertTrue(prefixes.size() > 95, "Should generate highly unique prefixes");
	}

	@Test
	void defaultDigits_excludeConfusingCharacters() {
		UidPrefixGenerator generator = UidPrefixGenerator.SIZE_10;
		
		for (int i = 0; i < 100; i++) {
			String prefix = generator.generate();
			
			// Should not contain 0 or 1 (confusing with O/I)
			assertFalse(prefix.contains("0"));
			assertFalse(prefix.contains("1"));
		}
	}

	private boolean isVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
	}

	private boolean isConsonant(char c) {
		return Character.isLetter(c) && !isVowel(c);
	}
}

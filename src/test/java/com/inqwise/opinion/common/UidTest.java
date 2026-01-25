package com.inqwise.opinion.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Uid}.
 */
@ExtendWith(VertxExtension.class)
class UidTest {

	@Test
	void builder_withIdAndPrefix_createsUid() {
		Uid uid = Uid.builder()
			.withId(12345L)
			.withPrefix("test")
			.build();
		
		assertNotNull(uid);
		assertEquals(12345L, uid.getId());
		assertEquals("test", uid.getPrefix());
	}

	@Test
	void builder_withNullValues_allowsNulls() {
		Uid uid = Uid.builder()
			.build();
		
		assertNotNull(uid);
		assertNull(uid.getId());
		assertNull(uid.getPrefix());
	}

	@Test
	void toUidToken_withValidValues_generatesToken() {
		Uid uid = Uid.builder()
			.withId(123456789L)
			.withPrefix("account")
			.build();
		
		String token = uid.toUidToken();
		
		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertTrue(token.contains("-"));
		assertTrue(token.startsWith("account-"));
	}

	@Test
	void toUidToken_withNullId_throwsException() {
		Uid uid = Uid.builder()
			.withPrefix("test")
			.build();
		
		assertThrows(NullPointerException.class, () -> uid.toUidToken());
	}

	@Test
	void toUidToken_withNullPrefix_throwsException() {
		Uid uid = Uid.builder()
			.withId(123L)
			.build();
		
		assertThrows(NullPointerException.class, () -> uid.toUidToken());
	}

	@Test
	void toUidToken_usesBase52Encoding() {
		Uid uid = Uid.builder()
			.withId(999L)
			.withPrefix("test")
			.build();
		
		String token = uid.toUidToken();
		String[] parts = token.split("-");
		
		assertEquals(2, parts.length);
		assertEquals("test", parts[0]);
		
		// Base52 should only contain letters
		String encodedPart = parts[1];
		for (char c : encodedPart.toCharArray()) {
			assertTrue(Character.isLetter(c), "Base52 should only contain letters, found: " + c);
		}
	}

	@Test
	void parse_withValidToken_reconstructsUid() {
		String prefix = "account";
		Long id = 123456789L;
		
		Uid original = Uid.builder()
			.withId(id)
			.withPrefix(prefix)
			.build();
		
		String token = original.toUidToken();
		Uid parsed = Uid.parse(token);
		
		assertNotNull(parsed);
		assertEquals(id, parsed.getId());
		assertEquals(prefix, parsed.getPrefix());
	}

	@Test
	void parse_withNullToken_throwsException() {
		assertThrows(NullPointerException.class, () -> Uid.parse(null));
	}

	@Test
	void parse_withInvalidToken_throwsException() {
		assertThrows(Exception.class, () -> Uid.parse("invalid"));
	}

	@Test
	void parse_withNoSeparator_throwsException() {
		assertThrows(Exception.class, () -> Uid.parse("noseparator"));
	}

	@Test
	void toUidTokenAndParse_roundTrip_preservesValues() {
		Uid original = Uid.builder()
			.withId(987654321L)
			.withPrefix("user")
			.build();
		
		String token = original.toUidToken();
		Uid parsed = Uid.parse(token);
		
		assertEquals(original.getId(), parsed.getId());
		assertEquals(original.getPrefix(), parsed.getPrefix());
	}

	@Test
	void toUidTokenAndParse_roundTrip_withDifferentIds() {
		for (long id : new long[]{1L, 100L, 10000L, 1000000L, Long.MAX_VALUE / 2}) {
			Uid original = Uid.builder()
				.withId(id)
				.withPrefix("test")
				.build();
			
			String token = original.toUidToken();
			Uid parsed = Uid.parse(token);
			
			assertEquals(id, parsed.getId(), "Failed for id: " + id);
		}
	}

	@Test
	void toUidTokenAndParse_roundTrip_withDifferentPrefixes() {
		String[] prefixes = {"a", "account", "user", "org", "product"};
		
		for (String prefix : prefixes) {
			Uid original = Uid.builder()
				.withId(12345L)
				.withPrefix(prefix)
				.build();
			
			String token = original.toUidToken();
			Uid parsed = Uid.parse(token);
			
			assertEquals(prefix, parsed.getPrefix(), "Failed for prefix: " + prefix);
		}
	}

	@Test
	void builderFrom_copiesExistingUid() {
		Uid original = Uid.builder()
			.withId(555L)
			.withPrefix("copy")
			.build();
		
		Uid copy = Uid.builderFrom(original).build();
		
		assertEquals(original.getId(), copy.getId());
		assertEquals(original.getPrefix(), copy.getPrefix());
	}

	@Test
	void builderFrom_allowsModification() {
		Uid original = Uid.builder()
			.withId(111L)
			.withPrefix("original")
			.build();
		
		Uid modified = Uid.builderFrom(original)
			.withId(222L)
			.build();
		
		assertEquals(222L, modified.getId());
		assertEquals("original", modified.getPrefix());
		
		// Original should be unchanged
		assertEquals(111L, original.getId());
	}

	@Test
	void builderFrom_canChangePrefix() {
		Uid original = Uid.builder()
			.withId(333L)
			.withPrefix("old")
			.build();
		
		Uid modified = Uid.builderFrom(original)
			.withPrefix("new")
			.build();
		
		assertEquals(333L, modified.getId());
		assertEquals("new", modified.getPrefix());
		
		// Original should be unchanged
		assertEquals("old", original.getPrefix());
	}

	@Test
	void toUidToken_multipleCallsOnSameInstance_returnsSameToken() {
		Uid uid = Uid.builder()
			.withId(777L)
			.withPrefix("test")
			.build();
		
		String token1 = uid.toUidToken();
		String token2 = uid.toUidToken();
		
		assertEquals(token1, token2);
	}

	@Test
	void differentUids_produceDifferentTokens() {
		Uid uid1 = Uid.builder()
			.withId(100L)
			.withPrefix("test")
			.build();
		
		Uid uid2 = Uid.builder()
			.withId(200L)
			.withPrefix("test")
			.build();
		
		String token1 = uid1.toUidToken();
		String token2 = uid2.toUidToken();
		
		assertNotEquals(token1, token2);
	}

	@Test
	void differentPrefixes_produceDifferentTokens() {
		Uid uid1 = Uid.builder()
			.withId(100L)
			.withPrefix("prefix1")
			.build();
		
		Uid uid2 = Uid.builder()
			.withId(100L)
			.withPrefix("prefix2")
			.build();
		
		String token1 = uid1.toUidToken();
		String token2 = uid2.toUidToken();
		
		assertNotEquals(token1, token2);
	}

	@Test
	void toUidToken_formatContainsSeparator() {
		Uid uid = Uid.builder()
			.withId(123L)
			.withPrefix("format")
			.build();
		
		String token = uid.toUidToken();
		
		assertTrue(token.contains("-"), "Token should contain separator");
		assertEquals(1, token.chars().filter(ch -> ch == '-').count(), 
			"Token should contain exactly one separator");
	}

	@Test
	void getId_returnsCorrectValue() {
		Uid uid = Uid.builder()
			.withId(999888777L)
			.withPrefix("test")
			.build();
		
		assertEquals(999888777L, uid.getId());
	}

	@Test
	void getPrefix_returnsCorrectValue() {
		Uid uid = Uid.builder()
			.withId(123L)
			.withPrefix("myprefix")
			.build();
		
		assertEquals("myprefix", uid.getPrefix());
	}

	@Test
	void builder_chainedCalls_worksCorrectly() {
		Uid uid = Uid.builder()
			.withId(1L)
			.withPrefix("a")
			.withId(2L)
			.withPrefix("b")
			.build();
		
		// Last values should win
		assertEquals(2L, uid.getId());
		assertEquals("b", uid.getPrefix());
	}
}

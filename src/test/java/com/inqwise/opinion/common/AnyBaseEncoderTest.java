package com.inqwise.opinion.common;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AnyBaseEncoder}.
 */
@ExtendWith(VertxExtension.class)
class AnyBaseEncoderTest {

	@Test
	void encodeDecode_roundTrip_base52() {
		AnyBaseEncoder encoder = AnyBaseEncoder.BASE_52;

		assertEquals(BigInteger.ZERO, encoder.decode(encoder.encode(BigInteger.ZERO)));
		assertEquals(BigInteger.ONE, encoder.decode(encoder.encode(BigInteger.ONE)));
		assertEquals(BigInteger.valueOf(52), encoder.decode(encoder.encode(BigInteger.valueOf(52))));
		assertEquals(BigInteger.valueOf(123456789L), encoder.decode(encoder.encode(BigInteger.valueOf(123456789L))));
	}

	@Test
	void encode_withNegativeValue_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
			AnyBaseEncoder.BASE_36.encode(BigInteger.valueOf(-1))
		);
	}

	@Test
	void decode_withInvalidCharacter_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
			AnyBaseEncoder.BASE_10.decode("12a3")
		);
	}

	@Test
	void builder_withDuplicateCharacters_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
			AnyBaseEncoder.builder().alphabet("aabc").build()
		);
	}
}

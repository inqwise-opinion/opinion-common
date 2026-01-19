package com.inqwise.opinion.common;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * Base-N encoder/decoder for non-negative numbers.
 */
public final class AnyBaseEncoder {

	public static final AnyBaseEncoder BASE_10 =
		builder().alphabet("0123456789").build();
	public static final AnyBaseEncoder BASE_16 =
		builder().alphabet("0123456789abcdef").build();
	public static final AnyBaseEncoder BASE_36 =
		builder().alphabet("0123456789abcdefghijklmnopqrstuvwxyz").build();
	public static final AnyBaseEncoder BASE_52 =
		builder().alphabet("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").build();
	public static final AnyBaseEncoder BASE_62 =
		builder().alphabet("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").build();

	private final char[] alphabet;
	private final int base;
	private final int[] indexByChar;

	private AnyBaseEncoder(Builder builder) {
		this.alphabet = builder.alphabet.toCharArray();
		this.base = alphabet.length;
		this.indexByChar = buildIndexByChar(alphabet);
	}

	public String encode(long value) {
		return encode(BigInteger.valueOf(value));
	}

	public String encode(BigInteger value) {
		Objects.requireNonNull(value, "value");
		Preconditions.checkArgument(value.signum() >= 0, "value must be non-negative");

		if (BigInteger.ZERO.equals(value)) {
			return String.valueOf(alphabet[0]);
		}

		StringBuilder sb = new StringBuilder();
		BigInteger current = value;
		BigInteger baseBig = BigInteger.valueOf(base);

		while (current.signum() > 0) {
			BigInteger[] divRem = current.divideAndRemainder(baseBig);
			sb.append(alphabet[divRem[1].intValue()]);
			current = divRem[0];
		}

		return sb.reverse().toString();
	}

	public BigInteger decode(String value) {
		Objects.requireNonNull(value, "value");
		Preconditions.checkArgument(!value.isEmpty(), "value must not be empty");

		BigInteger result = BigInteger.ZERO;
		BigInteger baseBig = BigInteger.valueOf(base);

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			int index = indexOf(c);
			Preconditions.checkArgument(index >= 0, "invalid character at %s: %s", i, c);
			result = result.multiply(baseBig).add(BigInteger.valueOf(index));
		}

		return result;
	}

	public long decodeToLong(String value) {
		return decode(value).longValueExact();
	}

	public String getAlphabet() {
		return new String(alphabet);
	}

	public int getBase() {
		return base;
	}

	public static Builder builder() {
		return new Builder();
	}

	private int indexOf(char c) {
		if (c >= indexByChar.length) {
			return -1;
		}
		return indexByChar[c];
	}

	private static int[] buildIndexByChar(char[] alphabet) {
		int maxChar = 0;
		for (char c : alphabet) {
			if (c > maxChar) {
				maxChar = c;
			}
		}
		int[] index = new int[maxChar + 1];
		Arrays.fill(index, -1);
		for (int i = 0; i < alphabet.length; i++) {
			index[alphabet[i]] = i;
		}
		return index;
	}

	public static final class Builder {
		private String alphabet;

		private Builder() {
		}

		public Builder alphabet(String alphabet) {
			Objects.requireNonNull(alphabet, "alphabet");
			Preconditions.checkArgument(alphabet.length() >= 2, "alphabet must have at least 2 characters");
			validateUniqueAscii(alphabet);
			this.alphabet = alphabet;
			return this;
		}

		public AnyBaseEncoder build() {
			Objects.requireNonNull(alphabet, "alphabet");
			return new AnyBaseEncoder(this);
		}

		private static void validateUniqueAscii(String alphabet) {
			boolean[] seen = new boolean[128];
			for (int i = 0; i < alphabet.length(); i++) {
				char c = alphabet.charAt(i);
				Preconditions.checkArgument(
					c < 128,
					"alphabet must be ASCII, invalid char at %s: %s",
					i,
					c
				);
				Preconditions.checkArgument(!seen[c], "alphabet contains duplicate char: %s", c);
				seen[c] = true;
			}
		}
	}
}

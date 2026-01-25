package com.inqwise.opinion.common;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class Uid {
	private Long id;
	private String prefix;
	private final static char SEPARATOR = '-';

	private Uid(Builder builder) {
		this.id = builder.id;
		this.prefix = builder.prefix;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String toUidToken() {
		
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(prefix, "prefix");
		var builder = new StringBuilder();
		builder.append(prefix).append(SEPARATOR).append(AnyBaseEncoder.BASE_52.encode(id.longValue()));
		return builder.toString();
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static Builder builderFrom(Uid uid) {
		return new Builder(uid);
	}

	public static final class Builder {
		private Long id;
		private String prefix;

		private Builder() {
		}

		private Builder(Uid uid) {
			this.id = uid.id;
			this.prefix = uid.prefix;
		}

		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		public Builder withPrefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public Uid build() {
			return new Uid(this);
		}
	}
	
	public static final Uid parse(String uidToken) {
		Objects.requireNonNull(uidToken);
		int separatorIndex = uidToken.indexOf(SEPARATOR);
		Preconditions.checkElementIndex(separatorIndex, uidToken.length(), "invalid uidToken, no separator found");
		String prefix = uidToken.substring(0, separatorIndex);
		String base52id = uidToken.substring(separatorIndex + 1, uidToken.length());
		long id = AnyBaseEncoder.BASE_52.decodeToLong(base52id);
		return builder().withId(id).withPrefix(prefix).build();
	}
}

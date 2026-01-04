package com.inqwise.opinion.common;

public enum OpinionEntityStatus implements OpinionStatus {
	NotImplemented(0),
	NonActive(1),
	Active(2),
	Deleted(9);
	
	private int value;
	private OpinionEntityStatus(int value) {
		this.value = value;
	}

	@Override
	public int value() {
		return value;
	}
	
	public static OpinionEntityStatus valueOf(int value) {
		var result = optValueOf(value);
		if(null == result) {
			throw new IllegalArgumentException(String.format("OpinionEntityStatus #%s not exist", value));
		}
		return result;
	}
	
	public static OpinionEntityStatus optValueOf(int value) {
		for (var status : OpinionEntityStatus.values()) {
			if(status.value() == value) return status;
		}
		return null;
	}
}

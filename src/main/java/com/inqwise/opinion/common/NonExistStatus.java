package com.inqwise.opinion.common;

public final class NonExistStatus implements OpinionStatus {

	private int value;
	
	public NonExistStatus(int value) {
		this.value = value;
	}
	
	@Override
	public int value() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("Status%s", value);
	}
}

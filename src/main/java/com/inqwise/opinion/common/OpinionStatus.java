package com.inqwise.opinion.common;

public interface OpinionStatus {
	static OpinionStatus parse(int value) {
		var result = OpinionEntityStatus.optValueOf(value);
		if(null == result) return new NonExistStatus(value);
		
		return result;
	}
	
	int value();
	
}

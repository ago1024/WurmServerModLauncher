package org.gotti.wurmunlimited.modloader.interfaces;

import java.util.function.BinaryOperator;

public enum MessagePolicy {
	PASS,
	
	DISCARD;
	
	public static final BinaryOperator<MessagePolicy> ANY_DISCARDED = (a, b) -> a == MessagePolicy.DISCARD || b == MessagePolicy.DISCARD ? MessagePolicy.DISCARD : MessagePolicy.PASS; 
	
	public static final BinaryOperator<MessagePolicy> ALL_DISCARDED = (a, b) -> a == MessagePolicy.DISCARD && b == MessagePolicy.DISCARD ? MessagePolicy.DISCARD : MessagePolicy.PASS; 
	
}

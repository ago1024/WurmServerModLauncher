package org.gotti.wurmunlimited.modsupport;

public interface IIdType {

	/**
	 * Start value for generated ids For server ids the start value should be the highest possible value for the id.
	 * The IdFactory will count down towards the real server ids. For non-server ids the value should be 0 and {@link #isCountingDown()} should return false
	 */
	int startValue();

	/**
	 * Indicate if generated ids will increase or decrease.
	 */
	boolean isCountingDown();
	
	/**
	 * Get the property name.
	 */
	String typeName();
}

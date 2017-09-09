package org.gotti.wurmunlimited.modloader.interfaces;

/**
 * Called before the server shutdown is initiated
 */
public interface ServerShutdownListener {

	/**
	 * Called before the server shutdown is initiated
	 */
	void onServerShutdown();

}

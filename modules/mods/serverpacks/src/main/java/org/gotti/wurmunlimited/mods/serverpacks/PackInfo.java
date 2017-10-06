package org.gotti.wurmunlimited.mods.serverpacks;

import java.nio.file.Path;

class PackInfo {
	
	/**
	 * Path to the server pack.
	 */
	Path path;
	
	/**
	 * Prepend the pack to the pack list on the client.
	 */
	boolean prepend;
	
	/**
	 * Create a pack info
	 * @param path Path to the pack
	 * @param prepend Prepend the pack to the pack list on the client
	 */
	public PackInfo(Path path, boolean prepend) {
		this.path = path;
		this.prepend = prepend;
	}
}

package org.gotti.wurmunlimited.mods.serverpacks;

import java.nio.file.Path;

import org.gotti.wurmunlimited.mods.serverpacks.api.ServerPacks;

class PackInfo {

	/**
	 * Path to the server pack.
	 */
	final Path path;

	/**
	 * in-memory pack data
	 */
	final byte[] data;

	/**
	 * Prepend the pack to the pack list on the client.
	 */
	final boolean prepend;

	/**
	 * Force the download
	 */
	final boolean force;

	/**
	 * Create a pack info
	 * @param path Path to the pack
	 * @param options Options
	 */
	public PackInfo(Path path, ServerPacks.ServerPackOptions... options) {
		this.path = path;
		this.data = null;
		this.prepend = ServerPacks.ServerPackOptions.PREPEND.isIn(options);
		this.force = ServerPacks.ServerPackOptions.FORCE.isIn(options);
	}

	/**
	 * Create a pack info for in-memory data
	 * @param data in-memory data
	 * @param options Options
	 */
	public PackInfo(byte[] data, ServerPacks.ServerPackOptions... options) {
		this.path = null;
		this.data = data;
		this.prepend = ServerPacks.ServerPackOptions.PREPEND.isIn(options);
		this.force = ServerPacks.ServerPackOptions.FORCE.isIn(options);
	}
}

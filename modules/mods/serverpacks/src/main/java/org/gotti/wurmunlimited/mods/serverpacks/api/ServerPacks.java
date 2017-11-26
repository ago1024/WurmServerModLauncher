package org.gotti.wurmunlimited.mods.serverpacks.api;

import java.nio.file.Path;

import org.gotti.wurmunlimited.mods.serverpacks.ServerPackMod;

public interface ServerPacks {

	public enum ServerPackOptions {
		/**
		 * Prepend the pack to the list of packs
		 */
		PREPEND,
		/**
		 * Instruct the client to force the download
		 */
		FORCE,

		;

		public boolean isIn(ServerPackOptions...options) {
			if (options != null) {
				for (ServerPackOptions option : options) {
					if (option == this) {
						return true;
					}
				}
			}
			return false;
		}
	}

	void addServerPack(Path path, ServerPackOptions... options);

	void addServerPack(byte[] data, ServerPackOptions... options);

	void addServerPack(String name, byte[] data, ServerPackOptions... options);

	static ServerPacks getInstance() {
		return ServerPackMod.getInstance();
	}
}

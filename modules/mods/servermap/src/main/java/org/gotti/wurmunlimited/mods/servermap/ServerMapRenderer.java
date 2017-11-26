package org.gotti.wurmunlimited.mods.servermap;

import java.awt.image.BufferedImage;

/**
 * Interface for a map renderer.
 * @author ago
 */
public interface ServerMapRenderer {

	/**
	 * Render the server map.
	 * @return image of the map
	 */
	BufferedImage renderServerMap();

}

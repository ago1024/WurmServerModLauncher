package org.gotti.wurmunlimited.mods.servermap.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import org.gotti.wurmunlimited.mods.servermap.ServerMapRenderer;

import com.wurmonline.mesh.MeshIO;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;

/**
 * InGame map renderer (WU style). Renders from the surface mesh
 * Copied from the server code
 */
public class InternalServerMapRenderer implements ServerMapRenderer {

	@Override
	public BufferedImage renderServerMap() {
		return createMapDump(Server.surfaceMesh);
	}
	
	private static class ColorBuffer {
		private final int[] pixels;
		private final int stride;
		
		public ColorBuffer(BufferedImage img) {
			WritableRaster raster = img.getRaster();
			pixels = ((DataBufferInt) raster.getDataBuffer()).getData();
			stride = raster.getWidth();
		}
		
		public void set(int x, int y, Color color) {
			final int index = y * stride + x;
			pixels[index] = color.getRGB();
		}
	}
	
	public static BufferedImage createMapDump(final MeshIO mesh) {
		final int size = mesh.getSize();
		final BufferedImage bmi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final ColorBuffer buffer = new ColorBuffer(bmi);
		
		int maxH = 0;
		int minH = 0;
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				final int tileId = mesh.getTile(x, y);
				final int height = Tiles.decodeHeight(tileId);
				if (height > maxH) {
					maxH = height;
				}
				if (height < minH) {
					minH = height;
				}
			}
		}
		maxH /= (int) 10.0f;
		minH /= (int) 10.0f;
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				final int tileId = mesh.getTile(x, y);
				final byte type = Tiles.decodeType(tileId);
				final Tiles.Tile tile = Tiles.getTile(type);
				final int height2 = Tiles.decodeHeight(tileId);
				if (height2 > 0) {
					if (type == 4) {
						final float tenth = height2 / 10.0f;
						final float percent = tenth / maxH;
						final int step = (int) (150.0f * percent);
						buffer.set(x, y, new Color(Math.min(200, 10 + step), Math.min(200, 10 + step), Math.min(200, 10 + step)));
					} else if (type == 12) {
						buffer.set(x, y, Color.MAGENTA);
					} else {
						final float tenth = height2 / 10.0f;
						final float percent = tenth / maxH;
						final int step = (int) (190.0f * percent);
						final Color c = tile.getColor();
						buffer.set(x, y, new Color(c.getRed(), Math.min(255, c.getGreen() + step), c.getBlue()));
					}
				} else {
					final float tenth = height2 / 10.0f;
					final float percent = tenth / minH;
					final int step = (int) (255.0f * percent);
					buffer.set(x, y, new Color(0, 0, Math.max(20, 255 - Math.abs(step))));
				}
			}
		}
		return bmi;
	}
}

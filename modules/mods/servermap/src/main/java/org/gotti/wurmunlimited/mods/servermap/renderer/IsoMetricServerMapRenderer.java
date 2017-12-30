package org.gotti.wurmunlimited.mods.servermap.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.gotti.wurmunlimited.mods.servermap.ServerMapRenderer;

import com.wurmonline.mesh.MeshIO;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;

public class IsoMetricServerMapRenderer implements ServerMapRenderer {
	
	public final static int SCALE_FACTOR = 40;
	
	public BufferedImage render(final MeshIO mesh) {
		final int size = mesh.getSize();
		
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		for (int x = 0; x < size - 1; x++) {
			for (int y = 0; y < size - 1; y++) {
				final int tileId = mesh.getTile(x, y);
				final byte type = Tiles.decodeType(tileId);
				final Tiles.Tile tile = Tiles.getTile(type);

				float h1 = Tiles.decodeHeight(tileId);
				float h2 = Tiles.decodeHeight(mesh.getTile(x, y + 1));
				float h3 = Tiles.decodeHeight(mesh.getTile(x + 1, y + 1));

				int colour = shade(tile.getColor().getRGB(), h3 - h1, h1 <= 0);

				int start = (int) (y - (h1 / SCALE_FACTOR));
				int end = (int) (start + (Math.abs(h2 - h1) / SCALE_FACTOR) + 2);
				if (end >= size) end = size - 1;
				if (start < 0) start = 0;

				for (int i = start; i <= end; i++) {
					pixels[i * size + x] = colour;
				}
			}
		}

		return img;
	}
	
	public int shade(int colour, float delta, boolean water) {
		int r = (colour >> 16) & 0xFF;
		int g = (colour >> 8) & 0xFF;
		int b = colour & 0xFF;

		float mult = (float) (1 + (Math.tanh(delta / 128) * 0.66));

		r *= mult;
		g *= mult;
		b *= mult;

		if (water) {
			r = (r / 5) + 41;
			g = (r / 5) + 51;
			b = (r / 5) + 102;
		}

		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;

		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	@Override
	public BufferedImage renderServerMap() {
		return render(Server.surfaceMesh);
	}
}

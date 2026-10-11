package com.zpvp;

import net.minecraft.client.gui.DrawContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws the ZPVP logo (assets/zpvp/logo.png) with plain rectangle fills, so it needs no texture API
 * and works the same in every Minecraft version.
 */
public final class Logo {
	private record Run(int x, int y, int w, int argb) {}

	private static List<Run> runs;
	private static int imgW = 200, imgH = 55;
	private static boolean failed = false;

	private Logo() {}

	private static void load() {
		if (runs != null || failed) return;
		try (InputStream in = Logo.class.getResourceAsStream("/assets/zpvp/logo.png")) {
			if (in == null) { failed = true; return; }
			BufferedImage img = ImageIO.read(in);
			imgW = img.getWidth();
			imgH = img.getHeight();
			List<Run> out = new ArrayList<>();
			for (int y = 0; y < imgH; y++) {
				int x = 0;
				while (x < imgW) {
					int c = img.getRGB(x, y);
					if ((c >>> 24) < 24) { x++; continue; }
					int start = x;
					while (x < imgW && img.getRGB(x, y) == c) x++;
					out.add(new Run(start, y, x - start, c));
				}
			}
			runs = out;
		} catch (Exception e) {
			failed = true;
			ZPVP.LOG.warn("Could not load the ZPVP logo", e);
		}
	}

	/** Height of the logo when drawn targetWidth wide. */
	public static int heightFor(int targetWidth) {
		load();
		return Math.max(1, Math.round(imgH * (float) targetWidth / imgW));
	}

	/** Draws the logo centered on cx, with its top edge at top. */
	public static void draw(DrawContext ctx, int cx, int top, int targetWidth) {
		load();
		if (runs == null) return;
		float s = (float) targetWidth / imgW;
		int left = cx - targetWidth / 2;
		for (Run r : runs) {
			int x0 = left + Math.round(r.x * s);
			int x1 = left + Math.round((r.x + r.w) * s);
			int y0 = top + Math.round(r.y * s);
			int y1 = top + Math.max(Math.round((r.y + 1) * s), Math.round(r.y * s) + 1);
			if (x1 <= x0) x1 = x0 + 1;
			ctx.fill(x0, y0, x1, y1, r.argb);
		}
	}
}

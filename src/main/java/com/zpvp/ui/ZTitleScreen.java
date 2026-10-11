package com.zpvp.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

/**
 * Black & white main menu in a pixel-art style: sunset landscape, centered logo,
 * a column of dark icon buttons, a top-right toolbar and Quit at the bottom.
 */
public class ZTitleScreen extends ZScreen {
	private static final int PX = 4;

	public ZTitleScreen() {
		super(Text.literal("ZPVP"), null);
	}

	private static int rnd(int n) {
		n = n * 1103515245 + 12345;
		n ^= n >>> 15;
		n *= 0x45D9F3B;
		n ^= n >>> 13;
		return n & 0x7FFFFFFF;
	}

	/** Dark red-tinted scenery: keeps the black / red palette of the menu. */
	private static int gray(int v) {
		v = Math.max(0, Math.min(255, v));
		int g = v * 28 / 100, b = v * 32 / 100;
		return 0xFF000000 | (v << 16) | (g << 8) | b;
	}

	private static int groundTop(int x, int horizon) {
		int block = x / (PX * 4);
		return horizon + PX * 3 + (rnd(block + 7) % 3) * PX;
	}

	/** The dark red pixel sunset; also used behind Singleplayer / Multiplayer / Options. */
	public static void drawBackdrop(DrawContext ctx, int w, int h) {
		int horizon = (int) (h * 0.66f);

		// sky: banded gradient, brightest at the horizon
		for (int y = 0; y < horizon; y += PX) {
			float t = (float) y / horizon;
			ctx.fill(0, y, w, y + PX, gray((int) (14 + t * t * 150)));
		}

		// big diamond sun on the left
		int sx = (int) (w * 0.27f), sy = horizon - 36;
		for (int r = 44; r > 0; r -= 8) {
			int c = gray(200 - r * 2);
			for (int dy = -r; dy <= r; dy += 2) {
				int half = r - Math.abs(dy);
				ctx.fill(sx - half, sy + dy, sx + half, sy + dy + 2, c);
			}
		}

		// dark cloud bars
		for (int i = 0; i < 9; i++) {
			int cy = (int) (h * (0.18f + 0.045f * i));
			int cw = PX * (10 + rnd(i * 31 + 5) % 22);
			int cx = rnd(i * 17 + 3) % Math.max(1, w);
			ctx.fill(cx, cy, cx + cw, cy + PX, gray(10 + i * 3));
		}

		// trees on the horizon
		for (int i = 0; i < 16; i++) {
			int tx = (rnd(i * 53 + 9) % Math.max(1, w / PX)) * PX;
			int base = groundTop(tx, horizon);
			int th = PX * (5 + rnd(i * 11) % 5);
			ctx.fill(tx, base - th, tx + PX * 2, base, gray(38));
			int cw = PX * (8 + rnd(i * 19) % 6);
			ctx.fill(tx + PX - cw / 2, base - th - PX * 3, tx + PX + cw / 2, base - th, gray(34));
			ctx.fill(tx + PX - cw / 4, base - th - PX * 5, tx + PX + cw / 4, base - th - PX * 3, gray(34));
			ctx.fill(tx + PX - cw / 2, base - th - PX * 3, tx + PX + cw / 2, base - th - PX * 3 + 2, gray(80));
		}

		// ground blocks with a light top edge
		for (int x = 0; x < w; x += PX * 4) {
			int top = groundTop(x, horizon);
			ctx.fill(x, top, x + PX * 4, h, gray(12));
			ctx.fill(x, top + PX * 4, x + PX * 4, h, gray(6));
			ctx.fill(x, top, x + PX * 4, top + 2, gray(170));
			ctx.fill(x, top, x + 1, top + PX * 4, gray(24));
		}

		// floating block platforms at the sides
		for (int side = 0; side < 2; side++) {
			int px = side == 0 ? PX * 6 : w - PX * 20;
			int py = horizon - PX * (14 + side * 3);
			for (int b = 0; b < 4; b++) {
				int bx = px + b * PX * 4;
				ctx.fill(bx, py, bx + PX * 4, py + PX * 4, gray(16));
				ctx.fill(bx, py, bx + PX * 4, py + 2, gray(150));
				ctx.fill(bx, py, bx + 1, py + PX * 4, gray(60));
			}
		}

		// drifting ash
		long t = System.currentTimeMillis();
		for (int i = 0; i < 30; i++) {
			int px = (int) (((long) i * 137 + t / 50 * (1 + i % 3)) % Math.max(1, w));
			int py = (int) (((long) i * 251 + t / 90) % Math.max(1, h));
			ctx.fill(px, py, px + 1, py + 1, 0x55FFFFFF);
		}
	}

	private boolean menuButton(DrawContext ctx, String label, String[] icon, int x, int y, int w, int h, int mx, int my, boolean accent) {
		boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
		UI.rbox(ctx, x, y, w, h, accent ? UI.RED_DARK : hover ? 0xFF161618 : UI.CARD, accent ? UI.RED : hover ? UI.HOVER_LINE : UI.CARD_LINE);
		int color = UI.WHITE;
		String text = label.toUpperCase();
		int tw = textRenderer.getWidth(text);
		int tx = x + (w - tw) / 2 + (icon != null ? 6 : 0);
		if (icon != null) UI.icon(ctx, tx - 14, y + (h - 7) / 2, icon, color, 1);
		ctx.drawText(textRenderer, text, tx, y + (h - 8) / 2, color, false);
		return pressed && hover;
	}

	@Override
	protected void draw(DrawContext ctx, int mx, int my) {
		drawBackdrop(ctx, width, height);

		int cx = width / 2;
		int top = Math.max(6, height / 2 - 100);

		// logo
		int logoW = Math.min(190, width - 40);
		com.zpvp.Logo.draw(ctx, cx, top, logoW);
		int logoH = com.zpvp.Logo.heightFor(logoW);
		UI.centeredText(ctx, textRenderer, "C L I E N T", cx, top + logoH + 6, UI.GRAY);

		// button column
		int bw = Math.min(160, width - 40), bh = 20, gap = 4;
		int bx = cx - bw / 2;
		int by = top + 68;
		if (menuButton(ctx, "Singleplayer", UI.ICON_PERSON, bx, by, bw, bh, mx, my, false)) { client.setScreen(new SelectWorldScreen(this)); return; }
		by += bh + gap;
		if (menuButton(ctx, "Multiplayer", UI.ICON_PEOPLE, bx, by, bw, bh, mx, my, false)) { client.setScreen(new MultiplayerScreen(this)); return; }
		by += bh + gap;
		if (menuButton(ctx, "Mods", UI.ICON_STAR, bx, by, bw, bh, mx, my, true)) { client.setScreen(new ClickGuiScreen(this)); return; }
		by += bh + gap;
		if (menuButton(ctx, "HUD Editor", UI.ICON_MOVE, bx, by, bw, bh, mx, my, false)) { client.setScreen(new HudEditorScreen(this)); return; }
		by += bh + gap;
		if (menuButton(ctx, "Options", UI.ICON_GEAR, bx, by, bw, bh, mx, my, false)) { client.setScreen(new OptionsScreen(this, client.options)); return; }

		int quitY = Math.max(by + bh + 10, height - 30);
		if (menuButton(ctx, "Quit Game", null, bx, quitY, bw, bh, mx, my, false)) { client.scheduleStop(); return; }

		// top-right toolbar
		int tb = 26, tg = 4;
		String[][] icons = {UI.ICON_STAR, UI.ICON_MOVE, UI.ICON_PEOPLE, UI.ICON_GEAR};
		String[] tips = {"Mods", "HUD Editor", "Multiplayer", "Options"};
		int tx = width - 4 - (tb + tg) * icons.length + tg;
		String hovered = null;
		int hoveredX = 0;
		for (int i = 0; i < icons.length; i++) {
			int x = tx + i * (tb + tg);
			boolean hover = mx >= x && mx < x + tb && my >= 4 && my < 4 + tb;
			UI.rbox(ctx, x, 4, tb, tb, hover ? 0xFF161618 : UI.CARD, hover ? UI.RED : UI.CARD_LINE);
			UI.icon(ctx, x + (tb - 14) / 2, 4 + (tb - 14) / 2, icons[i], UI.WHITE, 2);
			if (hover) { hovered = tips[i]; hoveredX = x + tb / 2; }
			if (pressed && hover) {
				switch (i) {
					case 0 -> client.setScreen(new ClickGuiScreen(this));
					case 1 -> client.setScreen(new HudEditorScreen(this));
					case 2 -> client.setScreen(new MultiplayerScreen(this));
					default -> client.setScreen(new OptionsScreen(this, client.options));
				}
				return;
			}
		}
		// username chip
		String name = client.getSession().getUsername();
		int cw = textRenderer.getWidth(name) + 34;
		int chipX = tx - 6 - cw;
		UI.rbox(ctx, chipX, 4, cw, tb, UI.CARD, UI.CARD_LINE);
		ctx.fill(chipX + 6, 9, chipX + 24, 27, UI.RED);
		ctx.fill(chipX + 10, 15, chipX + 13, 18, UI.WHITE);
		ctx.fill(chipX + 17, 15, chipX + 20, 18, UI.WHITE);
		ctx.fill(chipX + 11, 21, chipX + 19, 23, UI.WHITE);
		ctx.drawText(textRenderer, name, chipX + 30, 4 + (tb - 8) / 2, UI.WHITE, false);
		if (hovered != null) UI.centeredText(ctx, textRenderer, hovered, hoveredX, 4 + tb + 4, UI.WHITE);

		ctx.drawText(textRenderer, "ZPVP 1.0.0  -  Minecraft 1.21.11", 6, height - 12, UI.GRAY, false);
	}

	@Override
	protected void onShift() {
		client.setScreen(new ClickGuiScreen(this));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}

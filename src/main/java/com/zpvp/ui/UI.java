package com.zpvp.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Tiny black & white drawing helpers. */
public final class UI {
	public static final int WHITE = 0xFFFFFFFF;
	public static final int BLACK = 0xFF000000;
	public static final int GRAY = 0xFF8C8C8C;
	public static final int DARK = 0xFF1A1A1A;
	public static final int LINE = 0xFF2A2A2A;
	public static final int RED = 0xFFFF2D2D;
	public static final int RED_DARK = 0xFF1A0D0E;
	public static final int PANEL = 0xFF0D0D0E;
	public static final int CARD = 0xFF0A0A0A;
	public static final int CARD_LINE = 0xFF1E1E1E;
	public static final int HOVER_LINE = 0xFF4A4A4A;
	public static final int OFF = 0xFF1C1C1E;
	public static final int KNOB_OFF = 0xFF6A6A6A;

	// 7x7 pixel icons
	public static final String[] ICON_PERSON = {"..###..", ".#####.", "..###..", "...#...", ".#####.", "#######", "#.....#"};
	public static final String[] ICON_PEOPLE = {".##.##.", ".##.##.", "..#.#..", "#######", "#######", "#.#.#.#", "#.#.#.#"};
	public static final String[] ICON_STAR = {"...#...", "...#...", "#######", ".#####.", "..###..", ".##.##.", "#.....#"};
	public static final String[] ICON_GEAR = {"..#.#..", ".#####.", "###.###", ".#...#.", "###.###", ".#####.", "..#.#.."};
	public static final String[] ICON_MOVE = {"...#...", "...#...", "..###..", "#######", "..###..", "...#...", "...#..."};
	public static final String[] ICON_GRID = {"###.###", "###.###", "###.###", ".......", "###.###", "###.###", "###.###"};

	public static final String[] ICON_BOLT = {"......###", ".....###.", "....###..", "...#####.", ".....###.", "....###..", "...###...", "..###....", "..##....."};
	public static final String[] ICON_SUN = {"....#....", ".#..#..#.", "...###...", "..#...#..", "##.....##", "..#...#..", "...###...", ".#..#..#.", "....#...."};
	public static final String[] ICON_MAGNIFIER = {".#####...", "#.....#..", "#.....#..", "#.....#..", "#.....#..", ".#####...", "......##.", ".......##", "........#"};
	public static final String[] ICON_EYE = {".........", "..#####..", ".#.....#.", "#...#...#", "#..###..#", "#...#...#", ".#.....#.", "..#####..", "........."};
	public static final String[] ICON_CUBE = {"..#######", ".#.....##", "#######.#", "#.....#.#", "#.....#.#", "#.....#.#", "#.....###", "#.....##.", "#######.."};
	public static final String[] ICON_TEXT = {"#########", ".........", "#######..", ".........", "#########", ".........", "#####....", ".........", "#########"};
	public static final String[] ICON_KEYS = {"###.###", "###.###", "###.###", ".......", "###.###", "###.###", "###.###"};
	public static final String[] ICON_CLOCK = {".#####.", "#..#..#", "#..#..#", "#..##.#", "#.....#", "#.....#", ".#####."};

	private UI() {}

	/** Rounded (1px corner cut) filled rectangle. */
	public static void rrect(DrawContext ctx, int x, int y, int w, int h, int color) {
		ctx.fill(x + 1, y, x + w - 1, y + h, color);
		ctx.fill(x, y + 1, x + w, y + h - 1, color);
	}

	/** Rounded rectangle with a 1px border. */
	public static void rbox(DrawContext ctx, int x, int y, int w, int h, int fill, int border) {
		rrect(ctx, x, y, w, h, border);
		rrect(ctx, x + 1, y + 1, w - 2, h - 2, fill);
	}

	public static void iconCentered(DrawContext ctx, int cx, int cy, String[] bmp, int color, int scale) {
		icon(ctx, cx - bmp[0].length() * scale / 2, cy - bmp.length * scale / 2, bmp, color, scale);
	}


	public static void icon(DrawContext ctx, int x, int y, String[] bmp, int color, int scale) {
		for (int r = 0; r < bmp.length; r++) {
			for (int c = 0; c < bmp[r].length(); c++) {
				if (bmp[r].charAt(c) == '#') ctx.fill(x + c * scale, y + r * scale, x + (c + 1) * scale, y + (r + 1) * scale, color);
			}
		}
	}

	public static void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
		ctx.fill(x, y, x + w, y + 1, color);
		ctx.fill(x, y + h - 1, x + w, y + h, color);
		ctx.fill(x, y, x + 1, y + h, color);
		ctx.fill(x + w - 1, y, x + w, y + h, color);
	}

	public static void centeredText(DrawContext ctx, TextRenderer tr, String text, int cx, int y, int color) {
		ctx.drawText(tr, text, cx - tr.getWidth(text) / 2, y, color, false);
	}

	public static void scaledText(DrawContext ctx, TextRenderer tr, String text, float x, float y, float scale, int color) {
		var m = ctx.getMatrices();
		m.pushMatrix();
		m.translate(x, y);
		m.scale(scale, scale);
		ctx.drawText(tr, text, 0, 0, color, false);
		m.popMatrix();
	}

	public static void scaledCentered(DrawContext ctx, TextRenderer tr, String text, int cx, float y, float scale, int color) {
		scaledText(ctx, tr, text, cx - tr.getWidth(text) * scale / 2f, y, scale, color);
	}
}

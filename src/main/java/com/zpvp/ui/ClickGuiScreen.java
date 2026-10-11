package com.zpvp.ui;

import com.zpvp.module.HudModule;
import com.zpvp.module.Module;
import com.zpvp.module.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Locale;

/**
 * The Right Shift menu: a dark rounded panel with ALL / HUD / MOD tabs, a search box and a grid of
 * mod cards (name, big icon, settings gear, on/off switch).
 * It is drawn with its own scale so it never fills the whole screen, whatever your GUI scale is.
 */
public class ClickGuiScreen extends ZScreen {
	private static final int PANEL_LINE = 0xFF2A2A2A;
	private static final int GAP = 10, CH = 88;

	/** Effective GUI scale of this menu. Lower = smaller menu. */
	private static final double MENU_SCALE = 1.5;

	private static final int ACCENT = UI.RED;

	private int tab = 0; // 0 = all, 1 = hud, 2 = other mods
	private int scrollRow = 0, maxRow = 0;
	private Module settingsFor = null;
	private TextFieldWidget search;

	// layout in "menu units" (screen units divided by k)
	private double k = 1.0;
	private int vw, vh, px, py, pw, ph, rowsVis, cols;
	private int searchX, searchY, searchW;

	public ClickGuiScreen(Screen parent) {
		super(Text.literal("ZPVP"), parent);
	}

	private void layout() {
		double gs = client.getWindow().getScaleFactor();
		k = Math.min(1.0, MENU_SCALE / gs);
		vw = (int) (width / k);
		vh = (int) (height / k);
		pw = Math.min(600, vw - 24);
		cols = pw >= 520 ? 3 : pw >= 340 ? 2 : 1;
		int avail = Math.max(1, (vh - 24 - 62 - 14 + GAP) / (CH + GAP));
		rowsVis = Math.min(3, avail);
		ph = 62 + rowsVis * (CH + GAP) - GAP + 14;
		px = (vw - pw) / 2;
		py = (vh - ph) / 2;
		searchW = Math.min(200, pw / 3);
		searchX = px + pw - 18 - searchW;
		searchY = py + 18;
	}

	private void placeSearchReal() {
		search.setX((int) ((searchX + 10) * k));
		search.setY((int) ((searchY + 9) * k));
		search.setWidth(Math.max(10, (int) ((searchW - 20) * k)));
		search.setHeight(Math.max(6, (int) (10 * k)));
	}

	@Override
	protected void init() {
		super.init();
		layout();
		search = new TextFieldWidget(textRenderer, 0, 0, 10, 10, Text.literal("Search"));
		search.setDrawsBackground(false);
		search.setPlaceholder(Text.literal("SEARCH..."));
		search.setChangedListener(s -> scrollRow = 0);
		addDrawableChild(search);
		placeSearchReal();
	}

	@Override
	protected void onShift() {
		if (search != null && search.isFocused()) return; // typing a capital letter must not close the menu
		close();
	}

	private boolean click(int mx, int my, int x, int y, int w, int h) {
		return pressed && mx >= x && mx < x + w && my >= y && my < y + h;
	}

	private static String[] iconFor(Module m) {
		return switch (m.id) {
			case "fps", "fpsboost", "ping", "togglesprint" -> UI.ICON_BOLT;
			case "zoom" -> UI.ICON_MAGNIFIER;
			case "keystrokes", "cps" -> UI.ICON_KEYS;
			case "armor" -> UI.ICON_CUBE;
			case "coords", "direction", "rotation", "speed" -> UI.ICON_MOVE;
			case "players", "target", "server" -> UI.ICON_PERSON;
			case "biome", "light", "day", "fullbright" -> UI.ICON_SUN;
			case "clock", "session" -> UI.ICON_CLOCK;
			case "custommenu", "freelook" -> UI.ICON_EYE;
			case "health", "hunger", "saturation" -> UI.ICON_PERSON;
			case "itemcount", "helditem" -> UI.ICON_CUBE;
			default -> UI.ICON_TEXT;
		};
	}

	private boolean matches(Module m) {
		if (tab == 1 && !m.isHud()) return false;
		if (tab == 2 && m.isHud()) return false;
		String q = search == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
		return q.isEmpty() || m.name.toLowerCase(Locale.ROOT).contains(q);
	}

	@Override
	protected void draw(DrawContext ctx, int rawMx, int rawMy) {
		layout();
		int mx = (int) (rawMx / k), my = (int) (rawMy / k);
		var m = ctx.getMatrices();
		m.pushMatrix();
		m.scale((float) k, (float) k);
		try {
			drawMenu(ctx, mx, my);
		} finally {
			m.popMatrix();
			if (search != null) placeSearchReal(); // input uses real screen coordinates
		}
	}

	private void drawMenu(DrawContext ctx, int mx, int my) {
		TextRenderer tr = textRenderer;
		boolean popup = settingsFor != null;
		int cmx = popup ? -1000 : mx, cmy = popup ? -1000 : my; // block hover/clicks behind the popup

		int logoW = Math.min(170, pw);
		com.zpvp.Logo.draw(ctx, vw / 2, Math.max(2, py - com.zpvp.Logo.heightFor(logoW) - 8), logoW);

		UI.rbox(ctx, px, py, pw, ph, UI.PANEL, PANEL_LINE);

		// tabs
		String[] tabs = {"ALL", "HUD", "MOD"};
		int tx = px + 18, ty = py + 18;
		for (int i = 0; i < tabs.length; i++) {
			int bw = tr.getWidth(tabs[i]) + 28;
			boolean sel = tab == i;
			boolean hover = cmx >= tx && cmx < tx + bw && cmy >= ty && cmy < ty + 28;
			UI.rbox(ctx, tx, ty, bw, 28, sel ? UI.RED_DARK : 0xFF111113, sel ? ACCENT : hover ? 0xFF555555 : PANEL_LINE);
			ctx.drawText(tr, tabs[i], tx + 14, ty + 10, sel ? UI.WHITE : UI.GRAY, false);
			if (click(cmx, cmy, tx, ty, bw, 28)) { tab = i; scrollRow = 0; }
			tx += bw + 8;
		}
		int ew = tr.getWidth("EDIT HUD") + 28;
		if (tx + ew < searchX - 8) {
			boolean eh = cmx >= tx && cmx < tx + ew && cmy >= ty && cmy < ty + 28;
			UI.rbox(ctx, tx, ty, ew, 28, 0xFF111113, eh ? 0xFF888888 : PANEL_LINE);
			ctx.drawText(tr, "EDIT HUD", tx + 14, ty + 10, UI.GRAY, false);
			if (click(cmx, cmy, tx, ty, ew, 28)) { client.setScreen(new HudEditorScreen(this)); return; }
		}

		// search box (the real text field is moved to menu coordinates just for this draw)
		UI.rbox(ctx, searchX, searchY, searchW, 28, 0xFF0A0A0B, search.isFocused() ? 0xFF888888 : PANEL_LINE);
		search.setX(searchX + 10);
		search.setY(searchY + 9);
		search.setWidth(searchW - 20);
		search.setHeight(10);
		search.render(ctx, mx, my, 0f);

		// cards (whole rows only, so nothing is cut off)
		int top = py + 62, bottom = py + ph - 14;
		int cw = (pw - 36 - (cols - 1) * GAP) / cols;
		int count = 0;
		for (Module mod : ModuleManager.all()) if (matches(mod)) count++;
		int totalRows = (count + cols - 1) / cols;
		maxRow = Math.max(0, totalRows - rowsVis);
		scrollRow = Math.max(0, Math.min(scrollRow, maxRow));

		int i = 0;
		for (Module mod : ModuleManager.all()) {
			if (!matches(mod)) continue;
			int row = i / cols;
			int col = i % cols;
			i++;
			if (row < scrollRow || row >= scrollRow + rowsVis) continue;
			int x = px + 18 + col * (cw + GAP);
			int y = top + (row - scrollRow) * (CH + GAP);
			drawCard(ctx, tr, mod, x, y, cw, cmx, cmy);
		}

		if (count == 0) UI.centeredText(ctx, tr, "NO MODS FOUND", px + pw / 2, top + 30, UI.GRAY);

		if (maxRow > 0) {
			int trackH = bottom - top;
			int barH = Math.max(16, trackH * rowsVis / totalRows);
			int barY = top + (trackH - barH) * scrollRow / maxRow;
			UI.rrect(ctx, px + pw - 9, barY, 3, barH, 0xFF555555);
		}

		if (popup) drawSettings(ctx, tr, mx, my);
	}

	private void drawCard(DrawContext ctx, TextRenderer tr, Module m, int x, int y, int cw, int mx, int my) {
		boolean hover = mx >= x && mx < x + cw && my >= y && my < y + CH;
		UI.rbox(ctx, x, y, cw, CH, UI.CARD, hover ? UI.HOVER_LINE : UI.CARD_LINE);
		ctx.drawText(tr, m.name.toUpperCase(Locale.ROOT), x + 14, y + 14, UI.WHITE, false);

		String[] ic = iconFor(m);
		int sc = ic[0].length() >= 9 ? 3 : 4;
		UI.iconCentered(ctx, x + cw / 2, y + CH / 2 - 4, ic, m.isEnabled() ? UI.WHITE : 0xFF9A9A9A, sc);

		// gear (settings)
		int gx = x + 12, gy = y + CH - 28;
		boolean gh = mx >= gx - 3 && mx < gx + 21 && my >= gy - 3 && my < gy + 17;
		UI.icon(ctx, gx, gy, UI.ICON_GEAR, gh ? UI.WHITE : UI.GRAY, 2);
		if (click(mx, my, gx - 3, gy - 3, 24, 20)) {
			settingsFor = m;
			search.setFocused(false);
		}

		// on/off switch
		int sw = 46, sh = 22, sx = x + cw - 14 - sw, sy = y + CH - 14 - sh;
		boolean on = m.isEnabled();
		UI.rbox(ctx, sx, sy, sw, sh, on ? ACCENT : UI.OFF, on ? ACCENT : 0xFF3A3A3A);
		int kx = on ? sx + sw - 4 - 14 : sx + 4;
		UI.rrect(ctx, kx, sy + 4, 14, 14, on ? UI.WHITE : UI.KNOB_OFF);
		if (click(mx, my, sx, sy, sw, sh)) m.toggle();
	}

	private void drawSettings(DrawContext ctx, TextRenderer tr, int mx, int my) {
		Module m = settingsFor;
		ctx.fill(px, py, px + pw, py + ph, 0xB0000000);
		boolean styled = m instanceof HudModule sm && sm.styleNames() != null;
		int w = 240, h = m.isHud() ? (styled ? 158 : 130) : 84;
		int x = vw / 2 - w / 2, y = vh / 2 - h / 2;
		UI.rbox(ctx, x, y, w, h, 0xFF0F0F10, 0xFF555555);
		ctx.drawText(tr, m.name.toUpperCase(Locale.ROOT) + " SETTINGS", x + 14, y + 14, UI.WHITE, false);

		int ry = y + 38;
		if (m instanceof HudModule hm) {
			ctx.drawText(tr, "SIZE", x + 14, ry + 5, UI.GRAY, false);
			if (small(ctx, tr, "-", x + w - 14 - 96, ry, 24, 18, mx, my, false)) hm.scale = Math.max(0.5f, Math.round((hm.scale - 0.1f) * 10f) / 10f);
			String sc = String.format(Locale.ROOT, "%.1fx", hm.scale);
			ctx.drawText(tr, sc, x + w - 14 - 68 + (48 - tr.getWidth(sc)) / 2, ry + 5, UI.WHITE, false);
			if (small(ctx, tr, "+", x + w - 14 - 24, ry, 24, 18, mx, my, false)) hm.scale = Math.min(3.0f, Math.round((hm.scale + 0.1f) * 10f) / 10f);
			ry += 28;
			ctx.drawText(tr, "BACKGROUND", x + 14, ry + 5, UI.GRAY, false);
			if (small(ctx, tr, hm.background ? "ON" : "OFF", x + w - 14 - 50, ry, 50, 18, mx, my, hm.background)) hm.background = !hm.background;
			String[] styles = hm.styleNames();
			if (styles != null) {
				ry += 28;
				ctx.drawText(tr, "STYLE", x + 14, ry + 5, UI.GRAY, false);
				String label = styles[Math.floorMod(hm.style, styles.length)];
				if (small(ctx, tr, label, x + w - 14 - 80, ry, 80, 18, mx, my, false)) hm.style = (hm.style + 1) % styles.length;
			}
		} else {
			ctx.drawText(tr, "No extra settings for this mod.", x + 14, ry + 2, UI.GRAY, false);
		}
		if (small(ctx, tr, "DONE", x + w / 2 - 30, y + h - 28, 60, 18, mx, my, false)) settingsFor = null;
	}

	private boolean small(DrawContext ctx, TextRenderer tr, String label, int x, int y, int w, int h, int mx, int my, boolean active) {
		boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
		UI.rbox(ctx, x, y, w, h, active ? UI.RED : hover ? 0xFF222224 : 0xFF141416, active ? UI.RED : hover ? UI.WHITE : 0xFF3A3A3A);
		ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2, UI.WHITE, false);
		return pressed && hover;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (settingsFor == null && verticalAmount != 0) {
			scrollRow = Math.max(0, Math.min(maxRow, scrollRow + (verticalAmount > 0 ? -1 : 1)));
		}
		return true;
	}
}

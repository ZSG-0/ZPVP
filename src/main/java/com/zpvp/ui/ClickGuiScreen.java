package com.zpvp.ui;

import com.zpvp.module.Category;
import com.zpvp.module.HudModule;
import com.zpvp.module.Module;
import com.zpvp.module.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Locale;

/** The Right Shift menu: every mod as a card you can toggle, scale and give a background. */
public class ClickGuiScreen extends ZScreen {
	private static final String[] TAB_NAMES = {"All", "HUD", "Gameplay", "Performance", "Misc"};
	private static final Category[] TAB_CATS = {null, Category.HUD, Category.GAMEPLAY, Category.PERFORMANCE, Category.MISC};

	private static final int CW = 132, CH = 46, GAP = 6, X0 = 16, Y0 = 82;

	private Category filter = null;
	private int scroll = 0;
	private int maxScroll = 0;

	public ClickGuiScreen(Screen parent) {
		super(Text.literal("ZPVP"), parent);
	}

	@Override
	protected void draw(DrawContext ctx, int mx, int my) {
		TextRenderer tr = textRenderer;
		UI.scaledText(ctx, tr, "ZPVP", 16, 10, 3f, UI.WHITE);
		ctx.drawText(tr, "Click a mod to toggle it  |  Right Shift or Esc to close", 16, 38, UI.GRAY, false);

		int tx = 16, ty = 54;
		for (int i = 0; i < TAB_NAMES.length; i++) {
			int bw = tr.getWidth(TAB_NAMES[i]) + 18;
			if (button(ctx, TAB_NAMES[i], tx, ty, bw, 16, mx, my, filter == TAB_CATS[i])) {
				filter = TAB_CATS[i];
				scroll = 0;
			}
			tx += bw + 4;
		}
		int bx = width - 16 - 50;
		if (button(ctx, "Close", bx, ty, 50, 16, mx, my, false)) { close(); return; }
		bx -= 74;
		if (button(ctx, "Edit HUD", bx, ty, 70, 16, mx, my, false)) { client.setScreen(new HudEditorScreen(this)); return; }

		ctx.fill(16, 76, width - 16, 77, UI.LINE);

		int cols = Math.max(1, (width - 2 * X0 + GAP) / (CW + GAP));
		int count = 0;
		for (Module m : ModuleManager.all()) if (filter == null || m.category == filter) count++;
		int rows = (count + cols - 1) / cols;
		int viewTop = Y0, viewBottom = height - 6;
		maxScroll = Math.max(0, rows * (CH + GAP) - (viewBottom - viewTop));
		scroll = Math.max(0, Math.min(scroll, maxScroll));

		ctx.enableScissor(0, viewTop, width, viewBottom);
		int i = 0;
		for (Module m : ModuleManager.all()) {
			if (filter != null && m.category != filter) continue;
			int cx = X0 + (i % cols) * (CW + GAP);
			int cy = Y0 + (i / cols) * (CH + GAP) - scroll;
			i++;
			if (cy + CH < viewTop || cy > viewBottom) continue;
			drawCard(ctx, tr, m, cx, cy, mx, my, viewTop, viewBottom);
		}
		ctx.disableScissor();

		if (maxScroll > 0) {
			int trackH = viewBottom - viewTop;
			int barH = Math.max(12, trackH * trackH / (trackH + maxScroll));
			int barY = viewTop + (trackH - barH) * scroll / maxScroll;
			ctx.fill(width - 8, barY, width - 5, barY + barH, UI.GRAY);
		}
	}

	private void drawCard(DrawContext ctx, TextRenderer tr, Module m, int x, int y, int mx, int my, int viewTop, int viewBottom) {
		boolean on = m.isEnabled();
		int fg = on ? UI.BLACK : UI.WHITE;
		boolean inView = my >= viewTop && my < viewBottom;
		boolean hover = inView && mx >= x && mx < x + CW && my >= y && my < y + CH;

		ctx.fill(x, y, x + CW, y + CH, on ? UI.WHITE : hover ? 0xFF202020 : UI.BLACK);
		UI.outline(ctx, x, y, CW, CH, on || hover ? UI.WHITE : UI.LINE);
		ctx.drawText(tr, m.name, x + 6, y + 6, fg, false);
		String status = on ? "ON" : "OFF";
		ctx.drawText(tr, status, x + CW - 6 - tr.getWidth(status), y + 6, on ? UI.BLACK : UI.GRAY, false);
		ctx.drawText(tr, tr.trimToWidth(m.description, CW - 12), x + 6, y + 18, on ? 0xFF555555 : UI.GRAY, false);

		boolean handled = false;
		if (m instanceof HudModule h) {
			int ry = y + CH - 15;
			if (mini(ctx, tr, "-", x + 6, ry, 12, 11, mx, my, fg, false, inView)) {
				h.scale = Math.max(0.5f, Math.round((h.scale - 0.1f) * 10f) / 10f);
				handled = true;
			}
			String sc = String.format(Locale.ROOT, "%.1fx", h.scale);
			ctx.drawText(tr, sc, x + 20 + (28 - tr.getWidth(sc)) / 2, ry + 2, fg, false);
			if (mini(ctx, tr, "+", x + 50, ry, 12, 11, mx, my, fg, false, inView)) {
				h.scale = Math.min(3.0f, Math.round((h.scale + 0.1f) * 10f) / 10f);
				handled = true;
			}
			if (mini(ctx, tr, "BG", x + 68, ry, 20, 11, mx, my, fg, h.background, inView)) {
				h.background = !h.background;
				handled = true;
			}
		}
		if (!handled && pressed && hover) m.toggle();
	}

	/** Small outlined button inside a card. Returns true when clicked. */
	private boolean mini(DrawContext ctx, TextRenderer tr, String label, int x, int y, int w, int h, int mx, int my, int fg, boolean active, boolean inView) {
		boolean hover = inView && mx >= x && mx < x + w && my >= y && my < y + h;
		int cardOn = fg == UI.BLACK ? 1 : 0;
		if (active) ctx.fill(x, y, x + w, y + h, fg);
		UI.outline(ctx, x, y, w, h, hover ? (cardOn == 1 ? UI.BLACK : UI.WHITE) : (cardOn == 1 ? 0xFF777777 : UI.LINE));
		int bg = cardOn == 1 ? UI.WHITE : UI.BLACK;
		ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2 + 1, active ? bg : fg, false);
		return pressed && hover;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scroll = Math.max(0, Math.min(maxScroll, scroll - (int) (verticalAmount * 20)));
		return true;
	}
}

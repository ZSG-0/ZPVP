package com.zpvp.ui;

import com.zpvp.module.HudModule;
import com.zpvp.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/** Drag every enabled HUD mod where you want it. Positions are saved automatically. */
public class HudEditorScreen extends ZScreen {
	private HudModule dragging;
	private int offX, offY;

	public HudEditorScreen(Screen parent) {
		super(Text.literal("HUD Editor"), parent);
	}

	@Override
	protected void draw(DrawContext ctx, int mx, int my) {
		int w = width, h = height;
		ctx.fill(w / 2, 0, w / 2 + 1, h, 0x30FFFFFF);
		ctx.fill(0, h / 2, w, h / 2 + 1, 0x30FFFFFF);
		UI.centeredText(ctx, textRenderer, "HUD EDITOR  -  drag mods to move them", w / 2, 8, UI.WHITE);
		UI.centeredText(ctx, textRenderer, "Scale and background are in the mod menu", w / 2, 20, UI.GRAY);

		List<HudModule> list = ModuleManager.hud();
		boolean any = false;
		for (HudModule m : list) {
			if (!m.isEnabled()) continue;
			any = true;
			m.ensurePlaced(w, h, client);
			m.render(ctx, client);
			UI.outline(ctx, m.x - 1, m.y - 1, m.scaledW(client) + 2, m.scaledH(client) + 2, m == dragging ? UI.RED : 0xFF777777);
		}
		if (!any) UI.centeredText(ctx, textRenderer, "No HUD mods are enabled - turn some on in the mod menu", w / 2, h / 2, UI.GRAY);

		if (pressed && my < h - 30) {
			for (int i = list.size() - 1; i >= 0; i--) {
				HudModule m = list.get(i);
				if (m.isEnabled() && m.hit(client, mx, my)) {
					dragging = m;
					offX = mx - m.x;
					offY = my - m.y;
					break;
				}
			}
		}
		if (!down) dragging = null;
		if (dragging != null) {
			int sw = dragging.scaledW(client), sh = dragging.scaledH(client);
			int nx = Math.max(0, Math.min(w - sw, mx - offX));
			int ny = Math.max(0, Math.min(h - sh, my - offY));
			if (Math.abs(nx + sw / 2 - w / 2) <= 3) nx = w / 2 - sw / 2;
			if (Math.abs(ny + sh / 2 - h / 2) <= 3) ny = h / 2 - sh / 2;
			dragging.x = nx;
			dragging.y = ny;
		}

		if (button(ctx, "Done", w / 2 - 30, h - 24, 60, 16, mx, my, false)) close();
	}
}

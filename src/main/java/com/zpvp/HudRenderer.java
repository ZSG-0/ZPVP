package com.zpvp;

import com.zpvp.module.HudModule;
import com.zpvp.module.ModuleManager;
import com.zpvp.ui.HudEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderer {
	private HudRenderer() {}

	public static void render(DrawContext ctx) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.options.hudHidden) return;
		if (mc.currentScreen instanceof HudEditorScreen) return; // the editor draws them itself
		Clicks.poll(mc);
		for (HudModule m : ModuleManager.hud()) {
			if (!m.isEnabled()) continue;
			m.ensurePlaced(ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight(), mc);
			m.render(ctx, mc);
		}
	}
}

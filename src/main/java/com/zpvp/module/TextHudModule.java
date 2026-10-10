package com.zpvp.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public abstract class TextHudModule extends HudModule {
	protected TextHudModule(String id, String name, String description, boolean enabledByDefault, int x, int y) {
		super(id, name, description, enabledByDefault, x, y);
	}

	protected abstract List<String> lines(MinecraftClient mc);

	@Override
	public int width(MinecraftClient mc) {
		int w = 40;
		for (String s : lines(mc)) w = Math.max(w, mc.textRenderer.getWidth(s) + 8);
		return w;
	}

	@Override
	public int height(MinecraftClient mc) {
		return Math.max(1, lines(mc).size()) * 10 + 4;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc) {
		int i = 0;
		for (String s : lines(mc)) {
			ctx.drawText(mc.textRenderer, s, 4, 3 + i * 10, 0xFFFFFFFF, true);
			i++;
		}
	}
}

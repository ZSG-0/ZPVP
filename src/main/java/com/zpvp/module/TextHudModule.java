package com.zpvp.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;

public abstract class TextHudModule extends HudModule {
	protected TextHudModule(String id, String name, String description, boolean enabledByDefault, int x, int y) {
		super(id, name, description, enabledByDefault, x, y);
	}

	protected abstract List<String> lines(MinecraftClient mc);

	protected static Text styled(String s) {
		return Text.literal(s.toUpperCase(Locale.ROOT)).formatted(Formatting.BOLD);
	}

	/** Right-aligned lists (like the module list) override this. */
	protected boolean rightAlign() {
		return false;
	}

	@Override
	public int width(MinecraftClient mc) {
		int w = 40;
		for (String s : lines(mc)) w = Math.max(w, mc.textRenderer.getWidth(styled(s)) + 8);
		return w;
	}

	@Override
	public int height(MinecraftClient mc) {
		return Math.max(1, lines(mc).size()) * 11 + 4;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc) {
		int i = 0;
		int boxW = width(mc);
		for (String s : lines(mc)) {
			Text t = styled(s);
			int x = rightAlign() ? boxW - 4 - mc.textRenderer.getWidth(t) : 4;
			ctx.drawText(mc.textRenderer, t, x, 3 + i * 11, 0xFFFFFFFF, true);
			i++;
		}
	}
}

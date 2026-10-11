package com.zpvp.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class HudModule extends Module {
	public int x;
	public int y;
	public float scale = 1.0f;
	public boolean background = false;
	/** Index into styleNames(); only used by mods that offer several looks. */
	public int style = 0;

	/** Names of the available looks, or null when the mod has only one. */
	public String[] styleNames() {
		return null;
	}

	/** x or y of -1 means "place automatically on first render" (right / middle of screen). */
	protected HudModule(String id, String name, String description, boolean enabledByDefault, int x, int y) {
		super(id, name, description, Category.HUD, enabledByDefault);
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isHud() {
		return true;
	}

	public abstract int width(MinecraftClient mc);

	public abstract int height(MinecraftClient mc);

	protected abstract void draw(DrawContext ctx, MinecraftClient mc);

	public boolean hasBackground() {
		return true;
	}

	public int scaledW(MinecraftClient mc) {
		return (int) Math.ceil(width(mc) * scale);
	}

	public int scaledH(MinecraftClient mc) {
		return (int) Math.ceil(height(mc) * scale);
	}

	public void ensurePlaced(int screenW, int screenH, MinecraftClient mc) {
		if (x < 0) x = Math.max(0, screenW - scaledW(mc) - 4);
		if (y < 0) y = Math.max(0, screenH / 2 - scaledH(mc) / 2);
	}

	public boolean hit(MinecraftClient mc, int mx, int my) {
		return mx >= x && mx < x + scaledW(mc) && my >= y && my < y + scaledH(mc);
	}

	public void render(DrawContext ctx, MinecraftClient mc) {
		var m = ctx.getMatrices();
		m.pushMatrix();
		m.translate((float) x, (float) y);
		m.scale(scale, scale);
		if (background && hasBackground()) {
			ctx.fill(0, 0, width(mc), height(mc), 0x90000000);
		}
		draw(ctx, mc);
		m.popMatrix();
	}
}

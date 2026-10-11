package com.zpvp.mixin;

import com.zpvp.InventoryStyle;
import com.zpvp.module.ModuleManager;
import com.zpvp.ui.UI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Dark ZPVP look for the inventory, chests, furnaces, crafting tables... (drawn over the vanilla panel). */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {
	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int backgroundWidth;
	@Shadow protected int backgroundHeight;
	@Shadow @Final protected T handler;

	@Inject(method = "renderBackground", at = @At("TAIL"), require = 0)
	private void zpvp$panel(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		InventoryStyle.drawn = false;
		if (!ModuleManager.customMenuEnabled()) return;
		Object self = this;
		if (self instanceof CreativeInventoryScreen) return;

		int x0 = x, y0 = y, w = backgroundWidth, h = backgroundHeight;
		int panel = 0xFF111113, line = 0xFF3A3A3C;

		if (self instanceof InventoryScreen) {
			// leave the player preview (and its black box) untouched
			int px0 = x0 + 25, py0 = y0 + 7, px1 = x0 + 76, py1 = y0 + 78;
			ctx.fill(x0, y0, x0 + w, py0, panel);
			ctx.fill(x0, py0, px0, py1, panel);
			ctx.fill(px1, py0, x0 + w, py1, panel);
			ctx.fill(x0, py1, x0 + w, y0 + h, panel);
		} else {
			ctx.fill(x0, y0, x0 + w, y0 + h, panel);
		}
		UI.outline(ctx, x0, y0, w, h, line);
		ctx.fill(x0 + 1, y0, x0 + w - 1, y0 + 1, UI.RED);

		for (Slot s : handler.slots) {
			UI.rbox(ctx, x0 + s.x - 1, y0 + s.y - 1, 18, 18, 0xFF0A0A0A, 0xFF2A2A2C);
		}
		int lw = 100;
		com.zpvp.Logo.draw(ctx, x0 + w / 2, Math.max(2, y0 - com.zpvp.Logo.heightFor(lw) - 6), lw);
		InventoryStyle.drawn = true;
	}
}

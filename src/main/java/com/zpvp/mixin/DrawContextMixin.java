package com.zpvp.mixin;

import com.zpvp.InventoryStyle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Inventory titles are dark gray (0x404040) in vanilla; on the dark ZPVP panel they are drawn light instead. */
@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
	@ModifyVariable(method = "drawText", at = @At("HEAD"), argsOnly = true, ordinal = 2, require = 0)
	private int zpvp$lightTitle(int color) {
		if (InventoryStyle.drawn && MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>
			&& (color == 0x404040 || color == 0xFF404040)) {
			return 0xFFE0E0E0;
		}
		return color;
	}
}

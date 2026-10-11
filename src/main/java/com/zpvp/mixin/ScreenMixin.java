package com.zpvp.mixin;

import com.zpvp.module.ModuleManager;
import com.zpvp.ui.ZTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replaces the dirt / panorama / blur background of every screen with the ZPVP one. */
@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void zpvp$background(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!ModuleManager.customMenuEnabled()) return;
		Screen s = (Screen) (Object) this;
		if (MinecraftClient.getInstance().world == null) {
			ZTitleScreen.drawBackdrop(ctx, s.width, s.height);
			ctx.fill(0, 0, s.width, s.height, 0xA0000000);
		} else {
			ctx.fill(0, 0, s.width, s.height, 0xC0050506);
		}
		if (s instanceof net.minecraft.client.gui.screen.GameMenuScreen) {
			com.zpvp.Logo.draw(ctx, s.width / 2, 6, Math.min(110, s.width - 20));
		}
		ci.cancel();
	}
}

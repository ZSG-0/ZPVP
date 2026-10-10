package com.zpvp.mixin;

import com.zpvp.module.ModuleManager;
import com.zpvp.ui.ZTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	/** Swaps the vanilla title screen for the ZPVP main menu. */
	@ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
	private Screen zpvp$replaceTitle(Screen screen) {
		if (screen != null && screen.getClass() == TitleScreen.class && ModuleManager.customMenuEnabled()) {
			return new ZTitleScreen();
		}
		return screen;
	}
}

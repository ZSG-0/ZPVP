package com.zpvp.mixin;

import com.zpvp.module.ModuleManager;
import com.zpvp.ui.UI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Draws every vanilla button (Singleplayer, Multiplayer, Options, pause menu...) in the ZPVP black & red style. */
@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin {
	@Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
	private void zpvp$style(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		Object self = this;
		if (!(self instanceof ButtonWidget || self instanceof CyclingButtonWidget)) return;
		if (self instanceof net.minecraft.client.gui.widget.TexturedButtonWidget) return; // recipe book, icon buttons
		if (!ModuleManager.customMenuEnabled()) return;

		ClickableWidget w = (ClickableWidget) self;
		if (w.getWidth() < 40 || w.getMessage().getString().isEmpty()) return; // small / icon-only buttons stay vanilla
		int x = w.getX(), y = w.getY(), bw = w.getWidth(), bh = w.getHeight();
		boolean hover = w.isHovered();
		boolean on = w.active;

		UI.rbox(ctx, x, y, bw, bh,
			on && hover ? 0xFF161618 : UI.CARD,
			!on ? 0xFF161618 : hover ? UI.RED : UI.CARD_LINE);

		TextRenderer tr = MinecraftClient.getInstance().textRenderer;
		int tw = tr.getWidth(w.getMessage());
		int color = !on ? 0xFF666666 : hover ? UI.WHITE : 0xFFC8C8C8;
		ctx.drawText(tr, w.getMessage(), x + (bw - tw) / 2, y + (bh - 8) / 2, color, false);
		ci.cancel();
	}
}

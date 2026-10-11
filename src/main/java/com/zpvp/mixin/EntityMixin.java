package com.zpvp.mixin;

import com.zpvp.Freelook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freelook: while active, mouse movement turns the camera instead of the player. */
@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true, require = 0)
	private void zpvp$freelook(double dx, double dy, CallbackInfo ci) {
		if (Freelook.active && (Object) this == MinecraftClient.getInstance().player) {
			Freelook.yaw += (float) dx * 0.15f;
			Freelook.pitch = MathHelper.clamp(Freelook.pitch + (float) dy * 0.15f, -90f, 90f);
			ci.cancel();
		}
	}
}

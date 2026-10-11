package com.zpvp.mixin;

import com.zpvp.Freelook;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Freelook: the camera uses its own angles while the key is held. */
@Mixin(Camera.class)
public abstract class CameraMixin {
	@ModifyVariable(method = "setRotation", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
	private float zpvp$yaw(float yaw) {
		return Freelook.active ? Freelook.yaw : yaw;
	}

	@ModifyVariable(method = "setRotation", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
	private float zpvp$pitch(float pitch) {
		return Freelook.active ? Freelook.pitch : pitch;
	}
}

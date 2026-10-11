package com.zpvp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

/** State of the Freelook mod: the camera angles while the key is held. */
public final class Freelook {
	public static boolean active = false;
	public static float yaw, pitch;
	private static Perspective saved = Perspective.FIRST_PERSON;

	private Freelook() {}

	public static void start(MinecraftClient mc) {
		if (mc.player == null) return;
		active = true;
		yaw = mc.player.getYaw();
		pitch = mc.player.getPitch();
		saved = mc.options.getPerspective();
		mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
	}

	public static void stop(MinecraftClient mc) {
		if (!active) return;
		active = false;
		mc.options.setPerspective(saved);
	}
}

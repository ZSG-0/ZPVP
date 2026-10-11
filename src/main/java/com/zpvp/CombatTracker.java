package com.zpvp;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

/** Tracks your own hits for the Reach Display and Combo Counter mods (display only). */
public final class CombatTracker {
	public static double lastReach = -1;
	public static int combo = 0;
	private static long lastHitMs = 0;

	private CombatTracker() {}

	public static void init() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (world.isClient()) {
				double ex = hit != null ? hit.getPos().x : entity.getX();
				double ey = hit != null ? hit.getPos().y : entity.getEyeY();
				double ez = hit != null ? hit.getPos().z : entity.getZ();
				double dx = ex - player.getX(), dy = ey - player.getEyeY(), dz = ez - player.getZ();
				lastReach = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (entity instanceof LivingEntity) {
					combo++;
					lastHitMs = System.currentTimeMillis();
				}
			}
			return ActionResult.PASS;
		});
	}

	public static void tick(MinecraftClient mc) {
		if (mc.player == null) return;
		if (mc.player.hurtTime > 0) combo = 0;
		if (combo > 0 && System.currentTimeMillis() - lastHitMs > 4000) combo = 0;
	}
}

package com.zpvp.module;

import com.zpvp.CombatTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.List;
import java.util.Locale;

/** Second batch of HUD mods. */
public final class MoreHudModules {
	private MoreHudModules() {}

	private static final long START = System.currentTimeMillis();

	public static final class Reach extends TextHudModule {
		public Reach() { super("reach", "Reach Display", "Distance of your last hit", false, 130, 4); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (CombatTracker.lastReach < 0) return List.of("Reach: --");
			return List.of(String.format(Locale.ROOT, "Reach: %.2f", CombatTracker.lastReach));
		}
	}

	public static final class Combo extends TextHudModule {
		public Combo() { super("combo", "Combo Counter", "Hits in a row without being hit", false, 130, 20); }
		@Override protected List<String> lines(MinecraftClient mc) { return List.of("Combo: " + CombatTracker.combo); }
	}

	public static final class Target extends TextHudModule {
		public Target() { super("target", "Target Info", "Name and health of what you look at", false, 130, 36); }
		@Override protected List<String> lines(MinecraftClient mc) {
			Entity t = mc.targetedEntity;
			if (t instanceof LivingEntity le) {
				return List.of(le.getName().getString(),
					String.format(Locale.ROOT, "HP: %.1f / %.1f", le.getHealth(), le.getMaxHealth()));
			}
			return List.of("Target: none");
		}
	}

	public static final class Rotation extends TextHudModule {
		public Rotation() { super("rotation", "Rotation", "Yaw and pitch", false, 130, 64); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("Yaw 0.0  Pitch 0.0");
			float yaw = ((mc.player.getYaw() % 360f) + 540f) % 360f - 180f;
			return List.of(String.format(Locale.ROOT, "Yaw %.1f  Pitch %.1f", yaw, mc.player.getPitch()));
		}
	}

	public static final class Light extends TextHudModule {
		public Light() { super("light", "Light Level", "Light level where you stand", false, 130, 80); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null || mc.world == null) return List.of("Light: 15");
			return List.of("Light: " + mc.world.getLightLevel(mc.player.getBlockPos()));
		}
	}

	public static final class Day extends TextHudModule {
		public Day() { super("day", "Day Counter", "In-game day number", false, 130, 96); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.world == null) return List.of("Day: 0");
			return List.of("Day: " + (mc.world.getTimeOfDay() / 24000L));
		}
	}

	public static final class Players extends TextHudModule {
		public Players() { super("players", "Players Online", "Players in the tab list", false, 130, 112); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.getNetworkHandler() == null) return List.of("Players: 1");
			return List.of("Players: " + mc.getNetworkHandler().getPlayerList().size());
		}
	}

	public static final class Health extends TextHudModule {
		public Health() { super("health", "Health", "Your hearts as a number", false, 130, 144); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("HP: 20.0 / 20.0");
			return List.of(String.format(Locale.ROOT, "HP: %.1f / %.1f", mc.player.getHealth(), mc.player.getMaxHealth()));
		}
	}

	public static final class Hunger extends TextHudModule {
		public Hunger() { super("hunger", "Hunger", "Food level", false, 130, 160); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("Hunger: 20");
			return List.of("Hunger: " + mc.player.getHungerManager().getFoodLevel());
		}
	}

	public static final class Saturation extends TextHudModule {
		public Saturation() { super("saturation", "Saturation", "Hidden food saturation, shows how long your hunger lasts", false, 130, 176); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("Saturation: 5.0");
			return List.of(String.format(Locale.ROOT, "Saturation: %.1f", mc.player.getHungerManager().getSaturationLevel()));
		}
	}

	public static final class ItemCount extends TextHudModule {
		public ItemCount() { super("itemcount", "Item Counter", "How many of the item in your hand you carry", false, 130, 192); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null || mc.player.getMainHandStack().isEmpty()) return List.of("Items: --");
			var held = mc.player.getMainHandStack();
			int total = 0;
			var inv = mc.player.getInventory();
			for (int i = 0; i < inv.size(); i++) {
				var s = inv.getStack(i);
				if (s.isOf(held.getItem())) total += s.getCount();
			}
			return List.of("Items: " + total);
		}
	}

	public static final class HeldItem extends TextHudModule {
		public HeldItem() { super("helditem", "Held Item", "Name and durability of your held item", false, 130, 208); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null || mc.player.getMainHandStack().isEmpty()) return List.of("Hand: empty");
			var s = mc.player.getMainHandStack();
			if (s.isDamageable()) return List.of(s.getName().getString(), "Durability: " + (s.getMaxDamage() - s.getDamage()) + "/" + s.getMaxDamage());
			return List.of(s.getName().getString());
		}
	}

	public static final class Session extends TextHudModule {
		public Session() { super("session", "Session Timer", "Time since you launched the game", false, 130, 128); }
		@Override protected List<String> lines(MinecraftClient mc) {
			long s = (System.currentTimeMillis() - START) / 1000L;
			return List.of(String.format(Locale.ROOT, "Playtime: %d:%02d:%02d", s / 3600, (s / 60) % 60, s % 60));
		}
	}
}

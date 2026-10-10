package com.zpvp.module;

import com.zpvp.Clicks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** All HUD mods. Each one is a small nested class. */
public final class HudModules {
	private HudModules() {}

	public static final class Fps extends TextHudModule {
		public Fps() { super("fps", "FPS", "Frames per second", true, 4, 4); }
		@Override protected List<String> lines(MinecraftClient mc) { return List.of("FPS: " + mc.getCurrentFps()); }
	}

	public static final class Cps extends TextHudModule {
		public Cps() { super("cps", "CPS", "Left | right clicks per second", true, 4, 20); }
		@Override protected List<String> lines(MinecraftClient mc) { return List.of("CPS: " + Clicks.left() + " | " + Clicks.right()); }
	}

	public static final class Coordinates extends TextHudModule {
		public Coordinates() { super("coords", "Coordinates", "Your XYZ position", true, 4, 36); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("XYZ: 0.0 64.0 0.0");
			return List.of(String.format(Locale.ROOT, "XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
		}
	}

	public static final class Ping extends TextHudModule {
		public Ping() { super("ping", "Ping", "Latency to the server", true, 4, 52); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player != null && mc.getNetworkHandler() != null) {
				PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
				if (e != null) return List.of("Ping: " + e.getLatency() + "ms");
			}
			return List.of("Ping: --");
		}
	}

	public static final class Speed extends TextHudModule {
		private double px, pz, speed;
		private boolean hasPrev;
		public Speed() { super("speed", "Speed", "Movement speed in blocks per second", false, 4, 68); }
		@Override public void onTick(MinecraftClient mc) {
			if (mc.player == null) { hasPrev = false; return; }
			if (hasPrev) {
				double dx = mc.player.getX() - px, dz = mc.player.getZ() - pz;
				speed = Math.sqrt(dx * dx + dz * dz) * 20.0;
			}
			px = mc.player.getX();
			pz = mc.player.getZ();
			hasPrev = true;
		}
		@Override protected List<String> lines(MinecraftClient mc) { return List.of(String.format(Locale.ROOT, "Speed: %.2f b/s", speed)); }
	}

	public static final class Direction extends TextHudModule {
		public Direction() { super("direction", "Direction", "Which way you are facing", false, 4, 84); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null) return List.of("Facing: North");
			String s = mc.player.getHorizontalFacing().asString();
			float yaw = ((mc.player.getYaw() % 360f) + 540f) % 360f - 180f;
			return List.of("Facing: " + Character.toUpperCase(s.charAt(0)) + s.substring(1) + String.format(Locale.ROOT, " (%.0f)", yaw));
		}
	}

	public static final class Biome extends TextHudModule {
		public Biome() { super("biome", "Biome", "Current biome", false, 4, 100); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.player == null || mc.world == null) return List.of("Biome: plains");
			String b = mc.world.getBiome(mc.player.getBlockPos()).getKey().map(k -> k.getValue().getPath()).orElse("unknown");
			return List.of("Biome: " + b);
		}
	}

	public static final class Server extends TextHudModule {
		public Server() { super("server", "Server", "Server address", false, 4, 116); }
		@Override protected List<String> lines(MinecraftClient mc) {
			if (mc.getCurrentServerEntry() != null) return List.of("Server: " + mc.getCurrentServerEntry().address);
			return List.of(mc.world != null ? "Singleplayer" : "Server: --");
		}
	}

	public static final class Memory extends TextHudModule {
		public Memory() { super("memory", "Memory", "Java memory usage", false, 4, 132); }
		@Override protected List<String> lines(MinecraftClient mc) {
			Runtime rt = Runtime.getRuntime();
			long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
			long max = rt.maxMemory() / 1048576L;
			return List.of("Mem: " + used + "/" + max + " MB");
		}
	}

	public static final class Clock extends TextHudModule {
		private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
		public Clock() { super("clock", "Clock", "Real-world time", false, 4, 148); }
		@Override protected List<String> lines(MinecraftClient mc) { return List.of(LocalTime.now().format(FMT)); }
	}

	public static final class Potions extends TextHudModule {
		public Potions() { super("potions", "Potion Effects", "Active effects and time left", true, -1, 4); }
		@Override protected List<String> lines(MinecraftClient mc) {
			List<String> out = new ArrayList<>();
			if (mc.player != null) {
				for (StatusEffectInstance e : mc.player.getStatusEffects()) {
					String name = e.getEffectType().value().getName().getString();
					String time;
					if (e.getDuration() < 0) {
						time = "inf";
					} else {
						int s = e.getDuration() / 20;
						time = String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
					}
					out.add(name + " " + (e.getAmplifier() + 1) + "  " + time);
				}
			}
			if (out.isEmpty()) out.add("No effects");
			return out;
		}
	}

	public static final class Keystrokes extends HudModule {
		public Keystrokes() { super("keystrokes", "Keystrokes", "WASD and mouse button display", true, 4, 170); }
		@Override public boolean hasBackground() { return false; }
		@Override public int width(MinecraftClient mc) { return 66; }
		@Override public int height(MinecraftClient mc) { return 62; }

		@Override protected void draw(DrawContext ctx, MinecraftClient mc) {
			var o = mc.options;
			key(ctx, mc, "W", o.forwardKey.isPressed(), 22, 0, 20, 20);
			key(ctx, mc, "A", o.leftKey.isPressed(), 0, 22, 20, 20);
			key(ctx, mc, "S", o.backKey.isPressed(), 22, 22, 20, 20);
			key(ctx, mc, "D", o.rightKey.isPressed(), 44, 22, 20, 20);
			key(ctx, mc, "LMB " + Clicks.left(), Clicks.leftDown, 0, 44, 32, 16);
			key(ctx, mc, "RMB " + Clicks.right(), Clicks.rightDown, 34, 44, 32, 16);
		}

		private void key(DrawContext ctx, MinecraftClient mc, String label, boolean down, int x, int y, int w, int h) {
			ctx.fill(x, y, x + w, y + h, down ? 0xE0FFFFFF : 0x90000000);
			int tw = mc.textRenderer.getWidth(label);
			ctx.drawText(mc.textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, down ? 0xFF000000 : 0xFFFFFFFF, false);
		}
	}

	public static final class ArmorHud extends HudModule {
		private static final EquipmentSlot[] SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
		};
		public ArmorHud() { super("armor", "Armor HUD", "Armor and held item with durability", true, -1, -1); }
		@Override public int width(MinecraftClient mc) { return 52; }
		@Override public int height(MinecraftClient mc) { return SLOTS.length * 18 + 2; }

		@Override protected void draw(DrawContext ctx, MinecraftClient mc) {
			boolean any = false;
			for (int i = 0; i < SLOTS.length; i++) {
				if (mc.player == null) break;
				ItemStack stack = mc.player.getEquippedStack(SLOTS[i]);
				if (stack.isEmpty()) continue;
				any = true;
				ctx.drawItem(stack, 2, 2 + i * 18);
				if (stack.isDamageable()) {
					ctx.drawText(mc.textRenderer, String.valueOf(stack.getMaxDamage() - stack.getDamage()), 22, 6 + i * 18, 0xFFFFFFFF, true);
				}
			}
			if (!any) ctx.drawText(mc.textRenderer, "Armor", 8, 4, 0xFF8C8C8C, false);
		}
	}
}

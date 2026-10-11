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

	public static final class Potions extends HudModule {
		private static final int ROW = 22;

		public Potions() { super("potions", "Potion Effects", "Active effects with icons and time left", true, -1, 150); }

		private static java.util.List<StatusEffectInstance> effects(MinecraftClient mc) {
			if (mc.player == null) return java.util.List.of();
			return new ArrayList<>(mc.player.getStatusEffects());
		}

		@Override public int width(MinecraftClient mc) {
			int w = 90;
			for (StatusEffectInstance e : effects(mc)) w = Math.max(w, 26 + mc.textRenderer.getWidth(label(e)) + 6);
			return w;
		}

		@Override public int height(MinecraftClient mc) { return Math.max(1, effects(mc).size()) * ROW + 4; }

		private static String label(StatusEffectInstance e) {
			return (e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1)).toUpperCase(Locale.ROOT);
		}

		private static String time(StatusEffectInstance e) {
			if (e.getDuration() < 0) return "inf";
			int s = e.getDuration() / 20;
			return String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
		}

		@Override protected void draw(DrawContext ctx, MinecraftClient mc) {
			var list = effects(mc);
			if (list.isEmpty()) {
				ctx.drawText(mc.textRenderer, net.minecraft.text.Text.literal("NO EFFECTS").formatted(net.minecraft.util.Formatting.BOLD), 4, 7, 0xFF8C8C8C, true);
				return;
			}
			int i = 0;
			for (StatusEffectInstance e : list) {
				int y = 2 + i * ROW;
				var key = e.getEffectType().getKey();
				if (key.isPresent()) {
					net.minecraft.util.Identifier id = key.get().getValue();
					net.minecraft.util.Identifier sprite = net.minecraft.util.Identifier.of(id.getNamespace(), "mob_effect/" + id.getPath());
					ctx.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, sprite, 4, y + 2, 18, 18);
				}
				ctx.drawText(mc.textRenderer, net.minecraft.text.Text.literal(label(e)).formatted(net.minecraft.util.Formatting.BOLD), 26, y + 3, 0xFFFFFFFF, true);
				ctx.drawText(mc.textRenderer, time(e), 26, y + 12, 0xFFB0B0B0, true);
				i++;
			}
		}
	}

	/** Right-aligned list of every enabled mod, top right. */
	public static final class ModuleList extends TextHudModule {
		public ModuleList() { super("modulelist", "Module List", "Shows all enabled mods", true, -1, 4); }
		@Override protected boolean rightAlign() { return true; }
		@Override public int width(MinecraftClient mc) { return 120; }
		@Override protected List<String> lines(MinecraftClient mc) {
			List<Module> on = new ArrayList<>();
			for (Module m : ModuleManager.all()) if (m.isEnabled() && m != this) on.add(m);
			on.sort((a, b) -> Integer.compare(mc.textRenderer.getWidth(styled(b.name)), mc.textRenderer.getWidth(styled(a.name))));
			List<String> out = new ArrayList<>();
			for (Module m : on) out.add(m.name);
			if (out.isEmpty()) out.add("No mods");
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
			UI_fill(ctx, x, y, w, h, down);
			int tw = mc.textRenderer.getWidth(label);
			ctx.drawText(mc.textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, 0xFFFFFFFF, false);
		}

		private static void UI_fill(DrawContext ctx, int x, int y, int w, int h, boolean down) {
			com.zpvp.ui.UI.rbox(ctx, x, y, w, h, down ? com.zpvp.ui.UI.RED : 0xB00A0A0A, down ? com.zpvp.ui.UI.RED : 0xFF2A2A2A);
		}
	}

	public static final class ArmorHud extends HudModule {
		private static final EquipmentSlot[] SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
		};
		private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		private static final String[][] GHOST = {
			{"..#####..", ".#######.", "#########", "#.......#", "#.......#", "#.#...#.#", "#.#...#.#"},
			{"##.....##", "##.###.##", "##.###.##", "#########", "#.#####.#", "#.#####.#", "#.#####.#"},
			{"#########", "#########", "###...###", "###...###", "###...###", "###...###", "###...###"},
			{"###...###", "###...###", "###...###", "####.####", "#########", "#########"}
		};
		private static final int SLOT = 30, SLOT_GAP = 0;

		public ArmorHud() { super("armor", "Armor HUD", "Armor and held item with durability", true, -1, -1); }

		@Override public String[] styleNames() { return new String[] {"Classic", "Slots"}; }
		@Override public boolean hasBackground() { return style == 0; }

		@Override public int width(MinecraftClient mc) { return style == 1 ? 4 * SLOT + 6 : 52; }
		@Override public int height(MinecraftClient mc) { return style == 1 ? SLOT + 6 : SLOTS.length * 18 + 2; }

		@Override protected void draw(DrawContext ctx, MinecraftClient mc) {
			if (style == 1) drawSlots(ctx, mc); else drawClassic(ctx, mc);
		}

		private void drawClassic(DrawContext ctx, MinecraftClient mc) {
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

		/** Four framed slots in a row, empty ones show a faint ghost icon, durability bar under each item. */
		private void drawSlots(DrawContext ctx, MinecraftClient mc) {
			int w = 4 * SLOT + 6, h = SLOT + 6;
			ctx.fill(0, 0, w, h, 0xFF000000);
			for (int i = 0; i < 4; i++) {
				int x = 3 + i * (SLOT + SLOT_GAP), y = 3;
				// bevelled gray frame
				ctx.fill(x, y, x + SLOT, y + SLOT, 0xFF8B8B8B);
				ctx.fill(x, y, x + SLOT, y + 2, 0xFFC6C6C6);
				ctx.fill(x, y, x + 2, y + SLOT, 0xFFC6C6C6);
				ctx.fill(x, y + SLOT - 2, x + SLOT, y + SLOT, 0xFF555555);
				ctx.fill(x + SLOT - 2, y, x + SLOT, y + SLOT, 0xFF555555);
				// dark olive inside
				ctx.fill(x + 3, y + 3, x + SLOT - 3, y + SLOT - 3, 0xE0191808);

				ItemStack stack = mc.player == null ? ItemStack.EMPTY : mc.player.getEquippedStack(ARMOR[i]);
				if (stack.isEmpty()) {
					String[] g = GHOST[i];
					int gx = x + (SLOT - g[0].length() * 2) / 2, gy = y + (SLOT - g.length * 2) / 2 - 1;
					com.zpvp.ui.UI.icon(ctx, gx, gy, g, 0x40FFFFFF, 2);
					continue;
				}
				ctx.drawItem(stack, x + (SLOT - 16) / 2, y + 4);
				if (stack.isDamageable()) {
					float frac = (stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage();
					int bx = x + 5, by = y + SLOT - 8, bw = SLOT - 10;
					ctx.fill(bx, by, bx + bw, by + 3, 0xFF000000);
					int r = frac > 0.5f ? (int) ((1f - frac) * 2f * 255) : 255;
					int g = frac > 0.5f ? 255 : (int) (frac * 2f * 255);
					ctx.fill(bx, by, bx + Math.max(1, Math.round(bw * frac)), by + 3, 0xFF000000 | (r << 16) | (g << 8));
				}
			}
		}
	}
}

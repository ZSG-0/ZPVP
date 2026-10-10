package com.zpvp.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zpvp.ZPVP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

/** Non-HUD mods: gameplay helpers, performance and misc. */
public final class Features {
	private Features() {}

	public static final class ToggleSprint extends Module {
		public ToggleSprint() { super("togglesprint", "Toggle Sprint", "Keeps you sprinting while you move forward", Category.GAMEPLAY, true); }

		@Override public void onTick(MinecraftClient mc) {
			var p = mc.player;
			if (p == null) return;
			if (mc.options.forwardKey.isPressed() && !p.isSneaking() && !p.isUsingItem()
				&& !p.horizontalCollision && p.getHungerManager().getFoodLevel() > 6) {
				p.setSprinting(true);
			}
		}
	}

	public static final class Zoom extends Module {
		private boolean zooming;
		private int savedFov = 70;

		public Zoom() { super("zoom", "Zoom", "Hold C to zoom in (rebind in Controls)", Category.GAMEPLAY, true); }

		@Override public void onTick(MinecraftClient mc) {
			boolean held = ZPVP.zoomKey != null && ZPVP.zoomKey.isPressed();
			if (held && !zooming) {
				savedFov = mc.options.getFov().getValue();
				zooming = true;
			}
			if (held) {
				mc.options.getFov().setValue(30);
			} else if (zooming) {
				mc.options.getFov().setValue(savedFov);
				zooming = false;
			}
		}

		@Override public void onDisable() {
			if (zooming) {
				MinecraftClient.getInstance().options.getFov().setValue(savedFov);
				zooming = false;
			}
		}
	}

	public static final class FpsBoost extends Module {
		private int[] orig;

		public FpsBoost() { super("fpsboost", "FPS Boost", "Lowers distances, shadows, particles and uncaps FPS", Category.PERFORMANCE, false); }

		private static <E extends Enum<E>> void setEnumOrdinal(SimpleOption<E> opt, int ordinal) {
			E[] values = opt.getValue().getDeclaringClass().getEnumConstants();
			opt.setValue(values[Math.max(0, Math.min(ordinal, values.length - 1))]);
		}

		@Override public void onEnable() {
			GameOptions o = MinecraftClient.getInstance().options;
			if (orig == null) {
				orig = new int[] {
					o.getViewDistance().getValue(),
					o.getSimulationDistance().getValue(),
					o.getEntityShadows().getValue() ? 1 : 0,
					((Enum<?>) o.getParticles().getValue()).ordinal(),
					o.getBiomeBlendRadius().getValue(),
					o.getMaxFps().getValue(),
					o.getEnableVsync().getValue() ? 1 : 0
				};
			}
			o.getViewDistance().setValue(Math.min(8, orig[0]));
			o.getSimulationDistance().setValue(Math.min(5, orig[1]));
			o.getEntityShadows().setValue(false);
			setEnumOrdinal(o.getParticles(), 99); // last constant = minimal
			o.getBiomeBlendRadius().setValue(0);
			o.getMaxFps().setValue(260);
			o.getEnableVsync().setValue(false);
			o.write();
		}

		@Override public void onDisable() {
			if (orig == null) return;
			GameOptions o = MinecraftClient.getInstance().options;
			o.getViewDistance().setValue(orig[0]);
			o.getSimulationDistance().setValue(orig[1]);
			o.getEntityShadows().setValue(orig[2] == 1);
			setEnumOrdinal(o.getParticles(), orig[3]);
			o.getBiomeBlendRadius().setValue(orig[4]);
			o.getMaxFps().setValue(orig[5]);
			o.getEnableVsync().setValue(orig[6] == 1);
			o.write();
			orig = null;
		}

		@Override public void saveExtra(JsonObject out) {
			if (orig == null) return;
			JsonArray a = new JsonArray();
			for (int v : orig) a.add(v);
			out.add("orig", a);
		}

		@Override public void loadExtra(JsonObject in) {
			if (!in.has("orig")) return;
			JsonArray a = in.getAsJsonArray("orig");
			if (a.size() != 7) return;
			orig = new int[7];
			for (int i = 0; i < 7; i++) orig[i] = a.get(i).getAsInt();
		}
	}

	public static final class CustomMenu extends Module {
		public CustomMenu() { super("custommenu", "Custom Main Menu", "Replace the vanilla title screen with ZPVP's", Category.MISC, true); }
	}
}

package com.zpvp.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
	private static final List<Module> ALL = new ArrayList<>();
	private static final List<HudModule> HUD = new ArrayList<>();
	private static Module customMenu;

	private ModuleManager() {}

	public static void init() {
		if (!ALL.isEmpty()) return;
		// HUD
		register(new HudModules.Fps());
		register(new HudModules.Cps());
		register(new HudModules.Keystrokes());
		register(new HudModules.ArmorHud());
		register(new HudModules.Coordinates());
		register(new HudModules.Ping());
		register(new HudModules.Potions());
		register(new HudModules.Speed());
		register(new HudModules.Direction());
		register(new HudModules.Biome());
		register(new HudModules.Server());
		register(new HudModules.Memory());
		register(new HudModules.Clock());
		register(new MoreHudModules.Reach());
		register(new MoreHudModules.Combo());
		register(new MoreHudModules.Target());
		register(new MoreHudModules.Rotation());
		register(new MoreHudModules.Light());
		register(new MoreHudModules.Day());
		register(new MoreHudModules.Players());
		register(new MoreHudModules.Session());
		// Gameplay / performance / misc
		register(new Features.ToggleSprint());
		register(new Features.Zoom());
		register(new Features.FpsBoost());
		customMenu = register(new Features.CustomMenu());
	}

	private static <T extends Module> T register(T m) {
		ALL.add(m);
		if (m instanceof HudModule h) HUD.add(h);
		return m;
	}

	public static List<Module> all() {
		return ALL;
	}

	public static List<HudModule> hud() {
		return HUD;
	}

	public static boolean customMenuEnabled() {
		return customMenu != null && customMenu.isEnabled();
	}
}

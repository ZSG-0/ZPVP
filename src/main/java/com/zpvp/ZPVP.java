package com.zpvp;

import com.zpvp.module.Module;
import com.zpvp.module.ModuleManager;
import com.zpvp.ui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZPVP implements ClientModInitializer {
	public static final String MOD_ID = "zpvp";
	public static final Logger LOG = LoggerFactory.getLogger("ZPVP");

	public static KeyBinding menuKey;
	public static KeyBinding zoomKey;

	@Override
	public void onInitializeClient() {
		ModuleManager.init();
		Config.load();

		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
		menuKey = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.zpvp.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
		zoomKey = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.zpvp.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

		HudElementRegistry.attachElementAfter(
			VanillaHudElements.MISC_OVERLAYS,
			Identifier.of(MOD_ID, "hud"),
			(context, tickCounter) -> HudRenderer.render(context));

		CombatTracker.init();
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());
	}

	private void onTick(MinecraftClient mc) {
		while (menuKey.wasPressed()) {
			if (mc.currentScreen == null) mc.setScreen(new ClickGuiScreen(null));
		}
		if (mc.player == null) return;
		CombatTracker.tick(mc);
		for (Module m : ModuleManager.all()) {
			if (m.isEnabled()) m.onTick(mc);
		}
	}
}

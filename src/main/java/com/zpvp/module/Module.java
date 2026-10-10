package com.zpvp.module;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
	public final String id;
	public final String name;
	public final String description;
	public final Category category;
	private boolean enabled;

	protected Module(String id, String name, String description, Category category, boolean enabledByDefault) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.enabled = enabledByDefault;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		if (value == enabled) return;
		enabled = value;
		if (value) onEnable(); else onDisable();
	}

	/** Used when loading the config so enable hooks don't fire twice. */
	public void setEnabledSilently(boolean value) {
		enabled = value;
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public boolean isHud() {
		return false;
	}

	public void onEnable() {}

	public void onDisable() {}

	/** Called every client tick while enabled and in a world. */
	public void onTick(MinecraftClient mc) {}

	public void saveExtra(JsonObject o) {}

	public void loadExtra(JsonObject o) {}
}

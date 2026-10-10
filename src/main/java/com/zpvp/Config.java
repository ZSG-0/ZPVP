package com.zpvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zpvp.module.HudModule;
import com.zpvp.module.Module;
import com.zpvp.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Saves every mod's on/off state, position, scale and background to config/zpvp.json. */
public final class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Config() {}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("zpvp.json");
	}

	public static void load() {
		try {
			Path p = path();
			if (!Files.exists(p)) return;
			JsonObject root = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
			if (!root.has("modules")) return;
			JsonObject mods = root.getAsJsonObject("modules");
			for (Module m : ModuleManager.all()) {
				if (!mods.has(m.id)) continue;
				JsonObject o = mods.getAsJsonObject(m.id);
				if (o.has("enabled")) m.setEnabledSilently(o.get("enabled").getAsBoolean());
				if (m instanceof HudModule h) {
					if (o.has("x")) h.x = o.get("x").getAsInt();
					if (o.has("y")) h.y = o.get("y").getAsInt();
					if (o.has("scale")) h.scale = o.get("scale").getAsFloat();
					if (o.has("background")) h.background = o.get("background").getAsBoolean();
				}
				if (o.has("extra")) m.loadExtra(o.getAsJsonObject("extra"));
			}
		} catch (Exception e) {
			ZPVP.LOG.warn("Could not read zpvp.json, using defaults", e);
		}
	}

	public static void save() {
		try {
			JsonObject mods = new JsonObject();
			for (Module m : ModuleManager.all()) {
				JsonObject o = new JsonObject();
				o.addProperty("enabled", m.isEnabled());
				if (m instanceof HudModule h) {
					o.addProperty("x", h.x);
					o.addProperty("y", h.y);
					o.addProperty("scale", h.scale);
					o.addProperty("background", h.background);
				}
				JsonObject extra = new JsonObject();
				m.saveExtra(extra);
				if (extra.size() > 0) o.add("extra", extra);
				mods.add(m.id, o);
			}
			JsonObject root = new JsonObject();
			root.add("modules", mods);
			Files.writeString(path(), GSON.toJson(root));
		} catch (Exception e) {
			ZPVP.LOG.warn("Could not save zpvp.json", e);
		}
	}
}

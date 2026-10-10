package com.zpvp.module;

public enum Category {
	HUD("HUD"),
	GAMEPLAY("Gameplay"),
	PERFORMANCE("Performance"),
	MISC("Misc");

	public final String label;

	Category(String label) {
		this.label = label;
	}
}

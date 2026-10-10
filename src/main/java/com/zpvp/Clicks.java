package com.zpvp;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Frame-accurate click tracking for the CPS and keystroke mods. */
public final class Clicks {
	private static final Deque<Long> LEFT = new ArrayDeque<>();
	private static final Deque<Long> RIGHT = new ArrayDeque<>();
	private static boolean prevLeft, prevRight;
	public static boolean leftDown, rightDown;

	private Clicks() {}

	public static void poll(MinecraftClient mc) {
		long now = System.currentTimeMillis();
		boolean active = mc.currentScreen == null;
		long window = mc.getWindow().getHandle();
		leftDown = active && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		rightDown = active && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		if (leftDown && !prevLeft) LEFT.addLast(now);
		if (rightDown && !prevRight) RIGHT.addLast(now);
		prevLeft = leftDown;
		prevRight = rightDown;
		while (!LEFT.isEmpty() && now - LEFT.peekFirst() > 1000) LEFT.pollFirst();
		while (!RIGHT.isEmpty() && now - RIGHT.peekFirst() > 1000) RIGHT.pollFirst();
	}

	public static int left() {
		return LEFT.size();
	}

	public static int right() {
		return RIGHT.size();
	}
}

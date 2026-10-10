package com.zpvp.ui;

import com.zpvp.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Base for ZPVP screens. Mouse clicks and the Right Shift key are polled straight from GLFW
 * each frame, so the screens don't depend on the input-event signatures that change between versions.
 */
public abstract class ZScreen extends Screen {
	protected final Screen parent;
	protected boolean down, pressed, released;
	private boolean prevDown, prevShift;

	protected ZScreen(Text title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	@Override
	protected void init() {
		long w = client.getWindow().getHandle();
		prevDown = mouseDown(w); // ignore a click that was already held when the screen opened
		prevShift = shiftDown(w);
	}

	private static boolean mouseDown(long w) {
		return GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
	}

	private static boolean shiftDown(long w) {
		return GLFW.glfwGetKey(w, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		long w = client.getWindow().getHandle();
		boolean d = mouseDown(w);
		pressed = d && !prevDown;
		released = !d && prevDown;
		down = d;
		prevDown = d;
		boolean s = shiftDown(w);
		boolean shiftEdge = s && !prevShift;
		prevShift = s;

		ctx.fill(0, 0, width, height, client.world == null ? UI.BLACK : 0xC0000000);
		draw(ctx, mouseX, mouseY);
		if (shiftEdge) onShift();
	}

	protected abstract void draw(DrawContext ctx, int mx, int my);

	protected void onShift() {
		close();
	}

	/** Draws a button and returns true on the frame it was clicked. */
	protected boolean button(DrawContext ctx, String label, int x, int y, int w, int h, int mx, int my, boolean selected) {
		boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
		ctx.fill(x, y, x + w, y + h, selected ? UI.WHITE : hover ? 0xFF2A2A2A : UI.BLACK);
		UI.outline(ctx, x, y, w, h, selected || hover ? UI.WHITE : UI.LINE);
		int tw = textRenderer.getWidth(label);
		ctx.drawText(textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, selected ? UI.BLACK : UI.WHITE, false);
		return pressed && hover;
	}

	@Override
	protected void applyBlur(DrawContext context) {
		// no blur: keep the flat black & white look
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		// drawn in render()
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}

	@Override
	public void removed() {
		Config.save();
	}
}

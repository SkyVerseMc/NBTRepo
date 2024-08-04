package mc.skyverse.nbtrepo.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemCard extends PressableWidget {

	private boolean initialized = false;
	private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

	private final ItemStack stack;
	private final String title;

	int renderY;

	public ItemCard(ItemStack stack, String title) {

		super(0, 0, 0, 0, Text.literal(""));

		this.stack = stack;
		this.title = title;
	}

	public void init(int[] coordinates, int width, int height) {

		this.setX(coordinates[0]);
		this.setY(coordinates[1]);

		this.setWidth(width);
		this.setHeight(height);

		this.initialized = true;
	}

	@Override
	public void onPress() {};

	public void renderF(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {

		if (!initialized) return;

		renderY = getY() - scroll;

		context.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();

		boolean hovered = isHovered(mouseX, mouseY);

		RenderHelper.drawDirtBackgroundWithBrightness(context,  hovered ? 0.7F : 0.65F, getX(), renderY, getWidth(), getHeight());
		RenderHelper.drawDirtBackgroundWithBrightness(context, hovered ? 0.4F : 0.3F, getX() + 5, renderY + 5, getWidth() - 5 * 2, 60);

		RenderHelper.drawItemWithScale(context, stack, getX() + 34, renderY + 28, 3);

		context.drawCenteredTextWithShadow(textRenderer, getTitle(0), getX() + (int)(0.5 * getWidth()), renderY + 70, Color.WHITE.getRGB());
		context.drawCenteredTextWithShadow(textRenderer, getTitle(1), getX() + (int)(0.5 * getWidth()), renderY + 70 + textRenderer.fontHeight + 2, Color.WHITE.getRGB());
	}

	private String getTitle(int line) {

		if (textRenderer.getWidth(title) < getWidth()) return line < 1 ? title : "";

		String[] words = title.split("[^a-zA-Z0-9§]");

		if (words.length < 2) return line < 1 ? title : "";

		String[] text = new String[] {"", ""};
		int i = 0;
		String last = "";

		for (String s : words) {

			if (s.contains("§")) last = "§" + s.charAt(s.lastIndexOf('§') + 1);
			if (textRenderer.getWidth(text[i]) + textRenderer.getWidth(s + " ") > getWidth() - 10) i++;

			if (i > 1) break;
			text[i] += (i > 0 ? last : "") + s + " ";
		}

		return line > i ? "" : text[line];
	}

	@Override
	public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {

		this.drawScrollableText(context, textRenderer, 2, color);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {

		if (mouseX >= 125) this.onPress();
	}

	public boolean isHovered(int mouseX, int mouseY) {

		return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= renderY && mouseY <= renderY + getHeight();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (!initialized || !this.active || !this.visible) {
			return false;
		}
		if (KeyCodes.isToggle(keyCode)) {

			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			this.onPress();
			return true;
		}
		return false;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder var1) {}
}
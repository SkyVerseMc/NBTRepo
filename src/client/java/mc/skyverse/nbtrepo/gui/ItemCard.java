package mc.skyverse.nbtrepo.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import mc.skyverse.nbtrepo.NBTRepoModClient;
import mc.skyverse.nbtrepo.elements.ItemInfo;
import mc.skyverse.nbtrepo.gui.screen.RepoScreen;
import mc.skyverse.nbtrepo.util.resource.ItemUtil;
import mc.skyverse.nbtrepo.util.resource.StringUtil;
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
	private RepoScreen screen;

	private final ItemStack stack;
	private final ItemInfo itemInfo;
	
	private final String[] titleLines;
	private final String author;
	
	private int renderY;

	public ItemCard(RepoScreen screen, ItemInfo info) {

		super(0, 0, 0, 0, Text.literal(""));

		this.itemInfo = info;
		this.stack = new ItemStack(ItemUtil.getById(info.getItem()));
		this.screen = screen;
		
		titleLines = StringUtil.splitInLines(itemInfo.getName(), textRenderer, getWidth(), 2);
		author = StringUtil.fit("§7by §r" + itemInfo.getAuthor(), textRenderer, getWidth());
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
		
		context.drawCenteredTextWithShadow(textRenderer, titleLines[0], getX() + (int)(0.5 * getWidth()), renderY + 70, Color.WHITE.getRGB());
		context.drawCenteredTextWithShadow(textRenderer, titleLines[1], getX() + (int)(0.5 * getWidth()), renderY + 70 + 12, Color.WHITE.getRGB());
		
		context.drawTextWithShadow(textRenderer, (itemInfo.getVersion().isInRange(NBTRepoModClient.MC_VERSION) ? "§r" : "§c") + itemInfo.getVersion().get(), getX() + (int)(0.5 * getWidth()), renderY + 70 + 21, Color.WHITE.getRGB());
		
		context.drawTextWithShadow(textRenderer, "§7§l" + itemInfo.getDownloads(), getX() + 5, renderY + 75 + 31, Color.WHITE.getRGB());
		
		context.drawTextWithShadow(textRenderer, author, getX() + 5, renderY + 80 + 42, Color.WHITE.getRGB());
		
		context.drawCenteredTextWithShadow(textRenderer, "§7" + itemInfo.getDateString(), getX() + (int)(0.5 * getWidth()), renderY + 80 + 53, Color.WHITE.getRGB());
	}

	@Override
	public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {

		this.drawScrollableText(context, textRenderer, 2, color);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {}

	public boolean isHovered(int mouseX, int mouseY) {

		return screen.focusFree() && mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= renderY && mouseY <= renderY + getHeight();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (!initialized || !this.active || !this.visible) return false;

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
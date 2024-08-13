package mc.skyverse.nbtrepo.gui.screen;

import java.util.Random;

import mc.skyverse.nbtrepo.gui.ItemCard;
import mc.skyverse.nbtrepo.gui.ItemCardContainer;
import mc.skyverse.nbtrepo.gui.RenderHelper;
import mc.skyverse.nbtrepo.gui.components.ScrollPane;
import mc.skyverse.nbtrepo.gui.components.SearchField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RepoScreen extends Screen {

	private MinecraftClient mc;
	private Screen parent;
	protected TextFieldWidget serverIP;
	protected SearchField search;
	private ItemCardContainer container;
	private ScrollPane scrollPane;

	protected String searchQuery = "";


	public RepoScreen(MinecraftClient mc) {

		super(Text.literal("NBT Repo"));
		this.mc = mc;
		parent = mc.currentScreen;
	}

	@Override
	protected void init() {

		serverIP = new TextFieldWidget(textRenderer, width / 2 - 125, 30, 250, 20, serverIP, Text.literal(""));
		serverIP.setMaxLength(256);
		serverIP.setPlaceholder(Text.literal("Server URL or IP"));
		addSelectableChild(serverIP);

		search = new SearchField(textRenderer, width / 2 - 100, 60, 200, 20, search, Text.literal(""));
		search.setMaxLength(23);
		search.setPlaceholder(Text.literal("§oSearch for NBT..."));

		search.setSearchParams("Name", "Version", "Item", "ID", "Author");

		addSelectableChild(search);

		int h = 95;
		container = new ItemCardContainer(width, h);

		/* Random items for now */
		ItemStack stack = mc.player.getMainHandStack();
		container.addItem(new ItemCard(this, stack, stack.getName().getString()));

		for (int i = 0; i < 39; i++) {

			ItemStack st = new ItemStack(getRandom());
			container.addItem(new ItemCard(this, st, st.getName().getString()));
		}

		scrollPane = new ScrollPane(width, height - h, h, container.getHeight());
	}

	public Item getRandom() {

		Random r = new Random();
		int i = r.nextInt(Registries.ITEM.size() + 1);

		return Registries.ITEM.get(i);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {

		super.render(context, mouseX, mouseY, delta);
		super.renderBackgroundTexture(context);

		container.renderAll((int)scrollPane.getScrollAmount(), context, mouseX, mouseY, delta);

		context.getMatrices().translate(0, 0, 69);

		RenderHelper.drawDirtBackgroundWithBrightness(context,  0.25F, 0, 0, width, 95);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 8, 16777215);

		serverIP.render(context, mouseX, mouseY, delta);
		search.render(context, mouseX, mouseY, delta);;

		scrollPane.render(context, mouseX, mouseY, delta);

		context.drawBorder(width / 2 - 100, 7, 100, 100, 16777215);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		setFocused(null);

		if (serverIP.isHovered()) {

			setFocused(serverIP);

			if (serverIP.getText() == "") serverIP.setText("http://");

		} else {

			if (serverIP.getText().equals("http://")) serverIP.setText("");
		}

		if (search.isHovered()) {

			setFocused(search);
			search.onClick(mouseX, mouseY);
			
		} else {

			search.unfocus();
		}
		
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

		this.scrollPane.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (keyCode == 256) {

			if (focusFree()) {
				
				close();
				
			} else if (search.isFocused()) {
				
				search.keyPressed(keyCode, scanCode, modifiers);	
			}
			return true;
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {

		this.client.setScreen(parent);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {

		return false;
	}

	public boolean focusFree() {

		return !serverIP.isFocused() && !search.isFocused();
	}
}

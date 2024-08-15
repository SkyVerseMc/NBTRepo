package mc.skyverse.nbtrepo.gui.screen;

import java.util.Random;

import mc.skyverse.nbtrepo.gui.ItemCard;
import mc.skyverse.nbtrepo.gui.ItemCardContainer;
import mc.skyverse.nbtrepo.gui.RenderHelper;
import mc.skyverse.nbtrepo.gui.components.Dropdown;
import mc.skyverse.nbtrepo.gui.components.Listener;
import mc.skyverse.nbtrepo.gui.components.ScrollPane;
import mc.skyverse.nbtrepo.gui.components.SearchField;
import mc.skyverse.nbtrepo.gui.components.TextField;
import mc.skyverse.nbtrepo.web.Request;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RepoScreen extends Screen {

	private MinecraftClient mc;
	private Screen parent;

	private ItemCardContainer container;

	private TextField serverIP;
	private SearchField search;
	private ScrollPane scrollPane;
	private Dropdown sorter;

	protected String searchQuery = "";


	public RepoScreen(MinecraftClient mc) {

		super(Text.literal("NBT Repo"));
		this.mc = mc;
		parent = mc.currentScreen;
	}

	@Override
	protected void init() {

		serverIP = new TextField(textRenderer, width / 2 - 125, 30, 250, 20, serverIP, Text.literal(""));
		serverIP.setMaxLength(256);
		serverIP.setPlaceholder(Text.literal("Server URL or IP"));

		serverIP.setListener(new Listener() {

			@Override
			public void onUpdate(ClickableWidget widget) {

				String text = ((TextField)widget).getText();

				if (!Request.validIP(text)) return;

				// Fetch
			}
		});
		addSelectableChild(serverIP);

		search = new SearchField(textRenderer, width / 2 - 100, 60, 200, 20, search, Text.literal(""));
		search.setMaxLength(23);
		search.setPlaceholder(Text.literal("§oSearch for NBT..."));
		search.setSearchParams("Name", "Version", "Item", "ID", "Author");

		addSelectableChild(search);

		sorter = new Dropdown(textRenderer, width - 145, 60, 120, 20, Text.literal(""));
		sorter.setOptions("A-Z", "Z-A", "Most downloaded", "Most recent");
		sorter.setListener(new Listener() {

			@Override
			public void onUpdate(ClickableWidget widget) {

				String order = ((Dropdown)widget).getSelectedOption();

				// Sort
			}
		});

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
		search.render(context, mouseX, mouseY, delta);
		sorter.render(context, mouseX, mouseY, delta);

		scrollPane.render(context, mouseX, mouseY, delta);

		context.drawBorder(width / 2 - 100, 7, 100, 100, 16777215);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		setFocused(null);

		if (serverIP.isHovered()) {

			setFocused(serverIP);
			serverIP.onClick(mouseX, mouseY);

			if (serverIP.getText() == "") serverIP.setText("http://");

		} else {

			serverIP.unfocus();
		}

		if (search.isHovered()) {

			setFocused(search);
			search.onClick(mouseX, mouseY);

		} else {

			search.unfocus();
		}

		if (sorter.isHovered()) {

			setFocused(sorter);
			sorter.onClick(mouseX, mouseY);
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
				return true;
			}
			search.keyPressed(keyCode, scanCode, modifiers);	
			sorter.keyPressed(keyCode, scanCode, modifiers);

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

		return !serverIP.isFocused() && !search.isFocused() && !sorter.isFocused();
	}
}

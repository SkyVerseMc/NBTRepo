package mc.skyverse.nbtrepo.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mc.skyverse.nbtrepo.gui.ItemCard;
import mc.skyverse.nbtrepo.gui.RenderHelper;
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

	private Screen parent;

	public RepoScreen(Screen parent) {

		super(Text.literal("NBT Repo"));
		this.parent = parent;
	}

	/* later
	public ButtonWidget button1;
	public ButtonWidget button2;
	 */

	protected TextFieldWidget serverIP;
	protected TextFieldWidget search;

	protected String searchQuery = "";

	private ItemCardContainer container;
	private ScrollPane scrollPane;

	@Override
	protected void init() {

		/* Not working idk why */
		serverIP = new TextFieldWidget(textRenderer, width / 2 - 125, 30, 250, 20, serverIP, Text.literal(""));
		serverIP.setMaxLength(256);
		serverIP.setPlaceholder(Text.literal("Server URL or IP"));
		addSelectableChild(serverIP);

		search = new TextFieldWidget(textRenderer, width / 2 - 100, 55, 200, 20, search, Text.literal(""));
		search.setMaxLength(100);
		search.setPlaceholder(Text.literal("§oSearch for NBT..."));
		addSelectableChild(search);

		container = new ItemCardContainer();

		/* Random items for now */
		ItemStack stack = MinecraftClient.getInstance().player.getMainHandStack();
		container.addItem(new ItemCard(stack, stack.getName().getString()));

		for (int i = 0; i < 40; i++) {

			ItemStack st = new ItemStack(getRandom());
			container.addItem(new ItemCard(st, st.getName().getString()));
		}

		scrollPane = new ScrollPane(width, height - 120, 120, container.getHeight() + 5);
	}

	public Item getRandom() {

		Random r = new Random();
		int i = r.nextInt(Registries.ITEM.size() + 1);

		return Registries.ITEM.get(i);
	}

	public void close() {

		this.client.setScreen(this.parent);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		//serverIP.setFocused(serverIP.isHovered());
		//search.setFocused(search.isHovered());

		System.out.println(mouseX + "," + mouseY);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

		this.scrollPane.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		return true;
	}


	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {

		super.render(context, mouseX, mouseY, delta);
		super.renderBackgroundTexture(context);

		container.renderAll((int)scrollPane.getScrollAmount(), context, mouseX, mouseY, delta);

		context.getMatrices().translate(0, 0, 69);

		RenderHelper.drawDirtBackgroundWithBrightness(context,  0.25F, 0, 0, width, 125);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);

		this.serverIP.render(context, mouseX, mouseY, delta);
		this.search.render(context, mouseX, mouseY, delta);

		scrollPane.renderWidget(context, mouseX, mouseY, delta);

		context.drawBorder(width / 2 - 100, 7, 100, 100, 16777215);
	}

	public class ItemCardContainer {

		int originX = -55;
		int originY = 125;

		int padding = 25;
		int cardWidth = 85;
		int cardHeight = 135;

		int w = cardWidth + padding;
		int h = cardHeight + padding;

		int qw = width / w;

		List<ItemCard> cards = new ArrayList<ItemCard>();

		public void addItem(ItemCard itemcard) {

			itemcard.init(getCoordinates(), cardWidth, cardHeight);
			cards.add(itemcard);
		}

		public int getHeight() {

			return h * (cards.size() / qw);
		}

		private int[] getCoordinates() {

			int x = originX + w * ((cards.size() % qw) + 1);
			int y = originY + h * (cards.size() / qw);

			return new int[] {x, y};
		}

		private void renderAll(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {

			for (ItemCard card : cards) {

				card.renderF(scroll, context, mouseX, mouseY, delta);
			}
		}
	}
}

package mc.skyverse.nbtrepo.gui.screen;

import java.util.ArrayList;
import java.util.List;

import mc.skyverse.nbtrepo.gui.ItemCard;
import mc.skyverse.nbtrepo.gui.RepoListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RepoScreen extends Screen {

	private Screen parent;

	public RepoScreen(Screen parent) {

		super(Text.literal("NBT Repo"));
		this.parent = parent;
	}

	public ButtonWidget button1;
	public ButtonWidget button2;
	
	protected TextFieldWidget search;
	protected TextFieldWidget serverUrl;
	
	protected String searchFor = "";
	
	private ItemCardContainer container;
	public RepoListWidget scroll;

	@Override
	protected void init() {
		
		serverUrl = new TextFieldWidget(textRenderer, width / 2 - 125, 30, 250, 20, Text.literal(""));
		serverUrl.setMaxLength(256);
		serverUrl.setPlaceholder(Text.literal("Server URL or IP"));

		//scroll = new RepoListWidget(this, MinecraftClient.getInstance(), 600, 100, 80, 30, null);
		serverUrl.setChangedListener(s -> scroll.setServer(s));
		
		search = new TextFieldWidget(textRenderer, width / 2 - 100, 55, 200, 20, search, Text.literal(""));
		//search.setChangedListener(s -> scroll.setSearch(s));
		search.setMaxLength(100);
		search.setPlaceholder(Text.literal("§oSearch for NBT..."));


		container = new ItemCardContainer();

//		item = new ItemCard(width / 2 - 100, 7, 175, 125, Text.literal("Item"));
		
		container.addItem(new ItemCard(Text.literal("Item")));
		container.addItem(new ItemCard(Text.literal("Item 2")));
		
		addSelectableChild(serverUrl);
		addSelectableChild(search);
		//addSelectableChild(scroll);
		setInitialFocus((Element)search);
	}

	public void close() {
		
		this.client.setScreen(this.parent);
	}
	
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        
    	System.out.println(mouseX + "," + mouseY);
    	return false;
    }

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		
		super.renderBackgroundTexture(context);
		
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		
		this.serverUrl.render(context, mouseX, mouseY, delta);
		this.search.render(context, mouseX, mouseY, delta);
		//this.scroll.render(context, mouseX, mouseY, delta);
		
		container.renderAll(context, mouseX, mouseY, delta);
		
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		
		context.drawBorder(width / 2 - 100, 7, 100, 100, 16777215);
		//context.drawHorizontalLine(width / 2 - 100, 7, width / 2, 16777215);
		//context.fill(width / 2 - 100, 7, width / 2, 107, 16777215);
	}
	
	public class ItemCardContainer {
		
		int originalX = 0;//dunno;
		int originalY = 125;//dunno;
				
		int padding = 25;
		int cardWidth = 85;
		int cardHeight = 115;
		
		List<ItemCard> cards = new ArrayList<ItemCard>();
		
		public void addItem(ItemCard itemcard) {
			
			cards.add(itemcard);
			itemcard.init(getCoordinates(), cardWidth, cardHeight);
			
			System.out.println(width + "x" + height);
		}
		
		private int[] getCoordinates() {
			
			int x = originalX + padding + (cards.size() < 2 ? 0 : (width % ((cardWidth + padding) * (cards.size() - 1))));
			int y = originalY + (cards.size() < 2 ? 0 : (cardHeight + padding) * (int)(cards.size() * (cardWidth + padding) / width));
				
			System.out.println((int)(cards.size() * (cardWidth + padding) / width));
			
			System.out.println(x + ";" + y);
			return new int[] {x, y};
		}
		
		private void renderAll(DrawContext context, int mouseX, int mouseY, float delta) {
			
			for (ItemCard card : cards) {
				
				card.renderF(context, mouseX, mouseY, delta);
			}
		}
	}
}

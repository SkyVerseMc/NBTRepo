package mc.skyverse.nbtrepo.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;

public class ItemCardContainer {

	int originX = -55;
	int originY;

	int padding = 25;
	int width;
	int cardWidth = 85;
	int cardHeight = 135;

	int w = cardWidth + padding;
	int h = cardHeight + padding;

	int qw;

	List<ItemCard> cards = new ArrayList<ItemCard>();

	public ItemCardContainer(int width, int y) {

		this.width = width;
		qw = width / w;
		originY = y + 10;
	}

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

	public void renderAll(int scroll, DrawContext context, int mouseX, int mouseY, float delta) {

		for (ItemCard card : cards) {

			card.renderF(scroll, context, mouseX, mouseY, delta);
		}
	}
}
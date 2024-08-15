package mc.skyverse.nbtrepo.gui.components;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SearchField extends TextFieldWidget {

	private TextRenderer textRenderer;
	private LinkedHashMap<String, String> queryParams = new LinkedHashMap<String, String>(); 

	private int selectedRow = 0;

	private boolean hovered = false;
	private boolean hideFilters = true;
	private boolean navKey = false;


	public SearchField(TextRenderer textRenderer, int width, int height, Text text) {

		this(textRenderer, 0, 0, width, height, text);
	}

	public SearchField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {

		this(textRenderer, x, y, width, height, null, text);
	}

	public SearchField(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable SearchField copyFrom, Text text) {

		super(textRenderer, x, y, width, height, text);

		this.textRenderer = textRenderer;

		if (copyFrom != null) setText(copyFrom.getText());
	}

	public void setSearchParams(String... params) {

		for (String param : params) {

			queryParams.put(param, "");
		}
	}

	public String getQueryString() throws UnsupportedEncodingException {

		String query = "";

		for (Map.Entry<String, String> param : queryParams.entrySet()) {

			if (param.getValue() == "") continue;
			query += param.getKey() + "=" + URLEncoder.encode(param.getValue(), "utf-8");
		}

		return query;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

		super.renderWidget(context, mouseX, mouseY, delta);

		hovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight() * (isFocused() ? queryParams.size() + 1 : 1);

		if (!isFocused() || filtersHidden()) return;

		context.fill(RenderLayer.getGuiOverlay(), getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight() * (queryParams.size() + 1), Color.BLACK.getRGB());

		int i = 0;
		for (Map.Entry<String, String> param : queryParams.entrySet()) {

			context.drawTextWithShadow(textRenderer, param.equals(getSelectedEntry()) ? "§r§l" + param.getKey() + ": §r§7" + (getText().length() > param.getValue().length() ? getText() : param.getValue()) : "§r§7" + param.getKey() + ": " + param.getValue(), getX() + 4, getY() + getHeight() + 6 + (textRenderer.fontHeight + 1) * 2 * i, 0xE0E0E0);
			i++;
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {

		super.onClick(mouseX, mouseY);

		for (int i = 0; i < queryParams.size() + 1; i++) {

			if (getY() + getHeight() * (i + 1) > mouseY) {

				selectedRow = i;
				break;
			}
		}
	}

	public void unfocus() {

		selectedRow = 0;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (!isNarratable() || !isFocused()) return false;

		int l = queryParams.keySet().size();

		switch (keyCode) {

		case 235:
		case 257:
			saveParam();
			break;

		case 258:
		case 264:
			if (!filtersHidden() && !navKey) saveParam();
			hideFilters(false);
			selectedRow = Math.max(1, (selectedRow + 1) % (l + 1));
			setText(getSelectedEntry().getValue());
			break;

		case 265:
			if (!filtersHidden() && !navKey) saveParam();
			hideFilters(false);
			selectedRow = Math.max(1, (selectedRow + l) % (l + 1));
			setText(getSelectedEntry().getValue());
			break;

		case 256:
			unfocus();
			if (filtersHidden()) setFocused(false);
			hideFilters(true);
			break;

		default:
			navKey = false;
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		navKey = true;
		return true;
	}

	public void saveParam() {

		if (selectedRow == 0) return;

		queryParams.put(getSelectedEntry().getKey(), getText());
		setText("");
	}

	public Map.Entry<String, String> getSelectedEntry() {

		int i = 1;
		for (Map.Entry<String, String> param : queryParams.entrySet()) {

			if (selectedRow == i) return param;
			i++;
		}
		return null;
	}

	@Override
	public boolean isHovered() {

		return hovered;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {

		if (!isActive() || isInvalid(chr)) return false;

		return super.charTyped(chr, modifiers);
	}

	private void hideFilters(boolean flag) {

		hideFilters = flag;
	}

	private boolean filtersHidden() {

		return hideFilters;
	}

	private boolean isInvalid(char c) {

		return "?=&".indexOf(c) > -1;
	}
}

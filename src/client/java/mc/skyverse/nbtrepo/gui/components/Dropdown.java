package mc.skyverse.nbtrepo.gui.components;

import java.awt.Color;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

import mc.skyverse.nbtrepo.util.ModResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class Dropdown extends TextFieldWidget {

	private static final ButtonTextures TEXTURES = new ButtonTextures(new Identifier("widget/text_field"), new Identifier("widget/text_field_highlighted"));

	private TextRenderer textRenderer;
	private LinkedList<String> options = new LinkedList<String>();
	private Listener listener = new Listener();
	
	private String placeholder = "";

	private int selectedOption = 0;
	private int hoveredOption = -1;
	private String keyTyped = "";
	
	private boolean hovered = false;
	private boolean chosen = false;

	
	public Dropdown(TextRenderer textRenderer, int width, int height, Text text) {

		this(textRenderer, 0, 0, width, height, text);
	}

	public Dropdown(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {

		this(textRenderer, x, y, width, height, null, text);
	}

	public Dropdown(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable Dropdown copyFrom, Text text) {

		super(textRenderer, x, y, width, height, text);

		this.textRenderer = textRenderer;
		
		if (copyFrom != null) this.setOptions((String[])(getOptions().toArray()));
	}

	public void setOptions(String... options) {

		for (String option : options) {

			this.options.add(option);
		}
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		
		Identifier identifier = TEXTURES.get(isNarratable(), isFocused());
		context.drawGuiTexture(identifier, getX(), getY(), getWidth(), getHeight());
		
		context.drawTexture(ModResourceManager.EXPAND_BUTTON, getX() + getWidth() - 19, getY() + 1, 0, 0F, 0F, 18, 18, 20, 20);
		
		context.drawTextWithShadow(textRenderer, getSelectedOption(), getX() + 4, getY() + (getHeight() - 8) / 2, 0xE0E0E0);
		
		this.hovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight() * (isFocused() ? options.size() + 1 : 1);

		if (!isFocused() || chosen) return;

		context.fill(RenderLayer.getGuiOverlay(), getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight() * (options.size() + 1), Color.BLACK.getRGB());

		int i = 1;
		for (; i < options.size() + 1; i++) {

			if (getY() + getHeight() * (i + 1) > mouseY && getY() + getHeight() < mouseY && mouseX > getX() && mouseX <= getX() + getWidth()) {

				hoveredOption = i - 1;
				break;
			}
		}
		i = 0;
		for (String option : options) {

			context.drawTextWithShadow(this.textRenderer, "§r§" + (hoveredOption == i ? "l" : "7") + option, getX() + 4, getY() + getHeight() + 6 + (textRenderer.fontHeight + 1) * 2 * i, 0xE0E0E0);
			i++;
		}
	}
	
	public void setListener(Listener listener) {
		
		this.listener = listener;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {

		super.onClick(mouseX, mouseY);

		if (getY() + getHeight() > mouseY) {
			
			if (getX() + getWidth() - 19 <= mouseX && mouseX <= getX() + getWidth()) {
				
				chosen = !chosen;
				
				return;
			}
			chosen = false;
			
			return;
		}
		
		for (int i = 0; i < options.size() + 1; i++) {

			if (getY() + getHeight() * (i + 1) > mouseY) {

				selectedOption = i - 1;
				chosen = true;
				listener.onUpdate(this);
				
				return;
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (!isNarratable() || !isFocused()) return false;

		int l = options.size();

		switch (keyCode) {

		case 235:
		case 257:
			if (chosen) {
				chosen = true;
				return true;
			}
			saveParam();
			return true;

		case 258:
		case 264:
			hoveredOption = (hoveredOption + 1) % l;
			return true;

		case 265:
			hoveredOption = (hoveredOption + l - 1) % l;
			return true;

		case 256:
			setFocused(false);
			return true;

		default:
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
	
    @Override
    public boolean charTyped(char chr, int modifiers) {
        
    	return charTyped(chr, false);
    }
    
    private boolean charTyped(char chr, boolean again) {
    	
    	if (!this.isActive() || !SharedConstants.isValidChar((char)chr)) return false;
    	
    	keyTyped += (chr + "").toLowerCase();
    	
    	int i = 0;
		for (String s : options) {

			if (s.toLowerCase().startsWith(keyTyped)) {
				
				selectedOption = i;
				return true;
			}
			i++;
		}
			
		keyTyped = "";
		
    	return again ? charTyped(chr, true) : true;
    }

	public void saveParam() {

		chosen = true;
		
		if (hoveredOption < 0) return;
		
		selectedOption = hoveredOption;
		listener.onUpdate(this);
	}
	
	public void setPlaceholder(String text) {
		
		placeholder = text;
	}

	public String getSelectedOption() {

		return selectedOption < 0 ? placeholder : options.get(selectedOption);
	}

	public LinkedList<String> getOptions() {

		return options;
	}

	@Override
	public boolean isHovered() {

		return hovered;
	}
}

